# Сводка для проверки ДЗ: Локальные черновики Issue (`IssueDraft`)

В проекте **GHClient** (многомодульная Clean Architecture + MVI + Hilt + Jetpack Compose) задания реализованы на базе доменной сущности **`IssueDraft`** (локальные черновики рапортов/Issue для репозиториев GitHub) в модулях `:feature:issues:api`, `:feature:issues:impl` и `:app`.

> **Точка входа в приложении:** Детали любого репозитория (`RepoDetailsScreen`) → кнопка **`[ 📑 ЧЕРНОВИКИ РАПОРТОВ ]`** → список черновиков (`IssueDraftsScreen`) ⇄ редактор (`CreateIssueScreen`).

---

## Быстрая карта соответствия заданиям

| Задание | Что реализовано | Ключевые файлы |
|---|---|---|
| **1. Кэш на `FileStorage`** | JSON-кэш в `filesDir/issue_drafts_cache.json`, потокобезопасность через `Mutex`, все методы main-safe (`withContext(Dispatchers.IO)` внутри) | `feature/issues/impl/.../data/local/IssueDraftFileStorage.kt`<br>`feature/issues/impl/.../data/local/IssueDraftFileStorageImpl.kt` |
| **2. Сетевые заглушки** | `fetchDrafts`, `pushDraft`, `deleteDraft` с логированием (`Log.d("IssueDraftRemote", ...)`) и имитацией сети через `delay(500L)` | `feature/issues/impl/.../data/remote/IssueDraftRemoteDataSource.kt`<br>`feature/issues/impl/.../data/remote/StubIssueDraftRemoteDataSource.kt` |
| **3. Репозиторий + DI** | Реактивный `Flow<List<IssueDraft>>` (`observeDrafts()`), `suspend`-команды (`getDraft`, `save`, `delete`, `refreshFromBackend`), обновление кэша и заглушки | `feature/issues/api/.../domain/repository/IssueDraftRepository.kt`<br>`feature/issues/impl/.../data/repository/IssueDraftRepositoryImpl.kt`<br>`feature/issues/impl/.../di/IssueDataModule.kt` |
| **4. Связка с UI** | Список черновиков (`IssueDraftsScreen`) и редактор (`CreateIssueScreen` с `draftUid: String?`). `StateFlow` + `collectAsStateWithLifecycle()`. Автообновление списка после редактирования | `feature/issues/impl/.../presentation/IssueDraftsViewModel.kt`<br>`feature/issues/impl/.../presentation/ui/IssueDraftsScreen.kt`<br>`feature/issues/impl/.../presentation/CreateIssueViewModel.kt`<br>`feature/issues/impl/.../presentation/ui/CreateIssueScreen.kt`<br>`app/.../navigation/GHClientNavHost.kt` |
| **5. Ошибки и события** | Каждый 3-й вызов заглушки бросает `IOException`. Кэш остаётся на экране, одноразовый `Snackbar` идёт через `Channel(BufferOverflow.DROP_OLDEST)`, `CancellationException` всегда пробрасывается | `StubIssueDraftRemoteDataSource.kt`<br>`IssueDraftRepositoryImpl.kt`<br>`IssueDraftsViewModel.kt` / `CreateIssueViewModel.kt` |

---

## Детализация по критериям проверки

### 1. `FileStorage` (main-safe + `Mutex`)
* Файл: `IssueDraftFileStorageImpl` (`@Singleton`).
* Методы: `loadAll()`, `saveAll()`, `getById(uid)`, `save(item)`, `delete(uid)`.
* Все операции чтения/записи обернуты в `withContext(Dispatchers.IO)` и `mutex.withLock { ... }`.
* При отсутствии файла или битом JSON возвращается `emptyList()`, при этом `if (e is CancellationException) throw e` строго соблюдается.

### 2. Сетевые заглушки
* Файл: `StubIssueDraftRemoteDataSource` (`@Singleton`).
* Каждый метод пишет в Logcat (`tag = "IssueDraftRemote"`) имя вызова и аргументы и выполняет `delay(500L)`.
* Имитация ошибки для Задания 5: счётчик `callCounter` на каждый 3-й вызов (`count % 3 == 0`) бросает `IOException("Симуляция сбоя связи с сервером ГОСТ")`.

### 3. Репозиторий (`IssueDraftRepositoryImpl`)
* `observeDrafts(): Flow<List<IssueDraft>>` (не `suspend`) — возвращает `MutableStateFlow`, инициализируемый из `IssueDraftFileStorage` при первой подписке через `onStart { ensureInitialized() }`.
* `save(draft)` и `delete(uid)` сразу обновляют `FileStorage` и `_draftsFlow`, затем вызывают `remoteDataSource`.
* `refreshFromBackend()` при успехе перезаписывает кэш и `_draftsFlow`, а при `IOException` оставляет локальный кэш нетронутым и пробрасывает исключение вверх.

### 4. Экраны и реактивность UI
* **Список (`IssueDraftsScreen` + `IssueDraftsViewModel`):**
  * Подписывается на `ObserveIssueDraftsUseCase()` в `init` и держит единый `StateFlow<IssueDraftsState>`.
  * Экран читает состояние через `collectAsStateWithLifecycle()`.
  * При `isRefreshing = true` отображается `LinearProgressIndicator`.
* **Редактирование / создание (`CreateIssueScreen` + `CreateIssueViewModel`):**
  * Получает `draftUid: String?` из `SavedStateHandle` (type-safe навигация `CreateIssueDestination`).
  * Если `draftUid != null`, подгружает черновик через `GetIssueDraftUseCase(uid)` и предзаполняет форму (`title`, `body`, `priority`).
  * Нажатие **`[ 💾 СОХРАНИТЬ В ЧЕРНОВИКИ ]`** сохраняет изменения и возвращает назад; список на `IssueDraftsScreen` обновляется автоматически из `Flow` без ручного refresh.

### 5. Обработка ошибок, поворот экрана и корутины
* Одноразовые уведомления (`ShowSnackbar`) отправляются через `Channel<UiEffect>(Channel.BUFFERED, BufferOverflow.DROP_OLDEST)` и собираются в `LaunchedEffect(Unit)`.
* Текст ошибки **не** хранится в `UiState`, поэтому при повороте экрана `Snackbar` не показывается повторно.
* В `try/catch` слоёв `data` и `presentation` везде стоит проверка `if (e is CancellationException) throw e`.

### 6. Самопроверка чистоты слоёв
* В `@Composable` и `*ViewModel` отсутствуют `Dispatchers.IO`, `runBlocking`, `GlobalScope` и прямые обращения к `IssueDraftFileStorage`.
* Проверить поиском по проекту можно регулярным выражением `Dispatchers\.IO|runBlocking|GlobalScope|FileStorage` в директориях `**/presentation/**` и `app/`.

---

## Как проверить руками и тестами

### Сценарий ручной проверки на устройстве / эмуляторе
1. Открыть любой репозиторий → нажать **`[ 📑 ЧЕРНОВИКИ РАПОРТОВ ]`**.
2. Нажать **`+`** → создать черновик и сохранить. Убедиться, что он сразу появился в списке.
3. Нажать на созданный черновик → изменить заголовок/приоритет → сохранить. Убедиться, что список обновился сам без перезагрузки.
4. Нажать иконку обновления (Refresh) в шапке `IssueDraftsScreen` несколько раз (на 3-й сетевой вызов заглушка выбросит `IOException`):
   * кэшированные черновики останутся на экране;
   * появится `Snackbar` с сообщением об ошибке сети;
   * повернуть экран — `Snackbar` повторно не появится.
5. В Logcat можно отфильтровать по тегу `IssueDraftRemote`, чтобы увидеть вызовы заглушек и их аргументы.

### Unit-тесты
Реализованы тесты на кэш, репозиторий, MVI-состояния и одноразовые эффекты (`Turbine` + `MockK` + `kotlinx-coroutines-test`):
* `IssueDraftFileStorageImplTest` — чтение, сохранение, поиск по `uid`, удаление, обработка битого JSON.
* `IssueDraftRepositoryImplTest` — начальная загрузка из кэша, реактивное обновление `Flow` при `save`/`delete`, сохранение кэша при `IOException`, проброс `CancellationException`.
* `IssueDraftsViewModelTest` — фильтрация по репозиторию, `ShowSnackbar` при `IOException`.
* `CreateIssueViewModelTest` — предзагрузка черновика по `draftUid`, сохранение и обработка `IOException`.

Команда запуска (21 unit-тест, без live-теста в GitHub API):
```bash
./gradlew :feature:issues:impl:testDebugUnitTest --tests "*.IssueDraft*" --tests "*.CreateIssueViewModelTest" --tests "*.IssueMapperTest" --tests "*.IssueApiContractTest" :app:assembleDebug
```
*(Примечание: полный запуск `:feature:issues:impl:testDebugUnitTest` включает старый сетевой интеграционный тест `LiveIssueApiIntegrationTest`, который может возвращать HTTP 403 при исчерпании лимита неавторизованных запросов GitHub API.)*
