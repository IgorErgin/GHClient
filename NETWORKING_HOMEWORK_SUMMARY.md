# Сводка по сетевому ДЗ (текущее состояние проекта GHClient)

Проект **GHClient** оставлен в текущей архитектуре без подключения учебного бэкенда `hive.mrdekk.ru/todo/`. Ниже приведено точное соответствие пунктов домашнего задания тому, что уже реализовано в кодовой базе поверх реального **GitHub REST API (`https://api.github.com/`)**.

---

## Карта соответствия требованиям ДЗ

| Пункт ДЗ | Статус в GHClient | Реализация и ключевые файлы |
|---|---|---|
| **Retrofit + сериализация + работа с сетью через слой данных** | ✅ Выполнено | `Retrofit` (`baseUrl("https://api.github.com/")`) с `kotlinx.serialization` в `core/network/di/NetworkModule.kt`. Разделённые API-интерфейсы и репозитории по фича-модулям |
| **Загрузка и изменение данных на сервере (CRUD без синхронизации ревизий)** | ✅ Выполнено (на GitHub API) | • `SearchApi` / `SearchRepositoryImpl` (`GET search/repositories`)<br>• `RepositoryApi` / `RepoDetailsRepositoryImpl` (`GET repos/{owner}/{repo}`, `readme`, `contents`, `commits`, `pulls`)<br>• `IssueApi` / `IssueRepositoryImpl` (`GET` и `POST repos/{owner}/{repo}/issues`)<br>• `ProfileApi` / `ProfileRepositoryImpl` (`GET user`, `events`, `repos`)<br>• `UploadApi` / `UploadRepositoryImpl` (`PUT repos/{owner}/{repo}/contents/{path}`) |
| **Авторизация через Bearer-токен в перехватчике OkHttp** | ✅ Выполнено | • `HeaderInterceptor` (`core/network/HeaderInterceptor.kt`) добавляет `Authorization: Bearer <token>` ко всем запросам<br>• `AuthInterceptor` (`core/network/AuthInterceptor.kt`) перехватывает `401` и очищает токен в `TokenStorage` (`DataStore`) |
| **Обработка сетевых ошибок** | ✅ Выполнено | `safeApiCall` (`core/network/SafeApiCall.kt`) перехватывает `HttpException` (`401`, `404`, `5xx`), `IOException` и маппит их в `Result<T, DomainError.Network>`; репозитории отдают локальный кэш при `NO_INTERNET` |
| **Усложнение 1: OAuth-авторизация через провайдера (без ввода пароля в форму)** | ✅ Выполнено (GitHub OAuth 2.0) | • Браузерный вход через `CustomTabsIntent` (`AuthScreen.kt`, `AuthViewModel.kt`)<br>• Deep link `ghclient://oauth/callback` (`AndroidManifest.xml`, `GHClientNavHost.kt`)<br>• Обмен `code` → `access_token` через `OAuthApi.exchangeCodeForToken` (`POST https://github.com/login/oauth/access_token`)<br>• Сохранение токена в `TokenStorageImpl` (`DataStore`) и использование для запросов к `api.github.com` |
| **Усложнение 2: Повторы изменяющих операций при ошибках** | ⚠️ Частично (через `WorkManager` для загрузки файлов) | В `FileUploadWorker` (`feature/upload/impl/.../data/worker/FileUploadWorker.kt`) при ошибке сети возвращается `ListenableWorker.Result.retry()` с ограничением `NetworkType.CONNECTED` в `EnqueueFileUploadUseCase`. Очереди с оптимистичными ревизиями (`X-Last-Known-Revision`) нет, т.к. GitHub API не использует ревизии курса |
| **Синхронизация `PATCH /objects` с оптимистичными ревизиями (`hive.mrdekk.ru/todo/`)** | ❌ Не подключалась | Черновики `IssueDraft` из прошлого ДЗ оставлены на локальном `FileStorage` и заглушке `StubIssueDraftRemoteDataSource`, т.к. проект работает с реальным GitHub API без учебного бэкенда курса |

---

## Детализация реализованных механизмов

### 1. Сетевой слой и перехватчики OkHttp (`:core:network`)
* **`NetworkModule.kt`** — собирает единый `OkHttpClient` с тремя перехватчиками (`HeaderInterceptor`, `AuthInterceptor`, `HttpLoggingInterceptor`) и `Retrofit` с `kotlinx.serialization`.
* **`HeaderInterceptor.kt`** — читает токен из `TokenStorage` (`DataStore`) и добавляет в каждый запрос заголовки:
  * `Authorization: Bearer <token>`
  * `Accept: application/vnd.github+json`
  * `X-GitHub-Api-Version: 2026-03-10`
* **`AuthInterceptor.kt`** — при получении HTTP `401 Unauthorized` очищает сохранённый токен в `TokenStorage`.
* **`SafeApiCall.kt`** — единая обёртка над сетевыми вызовами: преобразует `HttpException` и `IOException` в типизированный `Result.Error(DomainError.Network)`.

### 2. OAuth 2.0 через браузер (`:feature:auth` + `:app`)
* Пользователь нажимает «Войти через GitHub» на `AuthScreen`.
* Открывается `CustomTabsIntent` на `https://github.com/login/oauth/authorize` с `redirect_uri=ghclient://oauth/callback` (`GithubAuthConfig.kt`, `AuthViewModel.kt`).
* После подтверждения в браузере система возвращает пользователя в приложение по intent-filter из `AndroidManifest.xml`, `GHClientNavHost` извлекает параметр `code` и передаёт в `AuthViewModel`.
* `AuthRepositoryImpl` вызывает `OAuthApi.exchangeCodeForToken(...)`, получает `access_token` и сохраняет его в `TokenStorageImpl`.
* После этого все экраны (`Search`, `RepoDetails`, `Profile`, `Issues`, `Upload`) автоматически авторизуются этим OAuth-токеном через `HeaderInterceptor`.

### 3. Реальные сетевые операции в репозиториях
* **Загрузка данных при старте / открытии экранов + офлайн-кэш в Room:**
  * `SearchRepositoryImpl`: запрос `GET search/repositories`, кэширование в `RepoDao`, возврат кэша при `NO_INTERNET`.
  * `RepoDetailsRepositoryImpl`: запросы `GET repos/{owner}/{repo}`, `readme`, `contents/{path}`, `commits`, `pulls`, кэширование деталей и README в `RepoDetailsDao`.
  * `ProfileRepositoryImpl`: загрузка текущего пользователя `GET user`, событий и репозиториев, кэширование в `UserProfileDao`.
  * `IssueRepositoryImpl`: загрузка списка задач `GET repos/{owner}/{repo}/issues` с кэшированием в `IssueDao`.
* **Изменяющие операции на сервере:**
  * Создание Issue в репозитории: `IssueRepositoryImpl.createIssue(...)` → `POST repos/{owner}/{repo}/issues`.
  * Загрузка файла в репозиторий: `UploadRepositoryImpl.uploadFile(...)` → `PUT repos/{owner}/{repo}/contents/{path}` (запускается в фоне через `WorkManager` в `FileUploadWorker`, который при сетевой ошибке возвращает `Result.retry()`).

---

## Тестирование сетевого слоя в проекте
В проекте уже присутствуют контрактные и интеграционные тесты API/мапперов:
* `feature/auth/...` и сетевые тесты в модулях:
  * `feature/search/impl/src/test/.../remote/SearchApiContractTest.kt`
  * `feature/repository/impl/src/test/.../remote/RepositoryApiContractTest.kt`
  * `feature/issues/impl/src/test/.../remote/IssueApiContractTest.kt`
  * `feature/profile/impl/src/test/.../remote/ProfileApiContractTest.kt`
  * `feature/upload/impl/src/test/.../remote/UploadApiContractTest.kt`
