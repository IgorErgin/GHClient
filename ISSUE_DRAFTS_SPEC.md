# Спецификация: Локальные черновики Issue (`IssueDraft`) и задачи для агентов

Документ адаптирует учебное задание (FileStorage-кэш, сетевые заглушки, реактивный репозиторий, 2 связанных экрана, обработка `IOException` через одноразовые события и безопасность корутин) под архитектуру проекта **GHClient** в рамках модуля `:feature:issues`.

---

## 1. Общая идея
Расширяем фичу `:feature:issues` так, чтобы пользователь мог создавать, редактировать, удалять и синхронизировать локальные черновики рапортов (`IssueDraft`) в файловом кэше, а при готовности — публиковать их в GitHub через уже существующий `IssueApi`.

Существующий контракт `IssueRepository` и тесты реального API (`IssueApiContractTest`, `LiveIssueApiIntegrationTest`) остаются нетронутыми.

---

## 2. Соответствие заданиям ДЗ и архитектуре GHClient

### Задание 1. Кэш на `FileStorage` (`:feature:issues:impl` / `data/local`)
- **Интерфейс и реализация:** `IssueDraftFileStorage` и `IssueDraftFileStorageImpl`.
- **Хранение:** JSON-файл `File(context.filesDir, "issue_drafts_cache.json")` с использованием `kotlinx.serialization.json.Json` и `Mutex` для потокобезопасности.
- **Операции:**
  - `suspend fun loadAll(): List<IssueDraftDto>`
  - `suspend fun saveAll(items: List<IssueDraftDto>)`
  - `suspend fun getById(uid: String): IssueDraftDto?`
  - `suspend fun save(item: IssueDraftDto)`
  - `suspend fun delete(uid: String)`
- **Потоки:** Все функции являются **main-safe `suspend`-функциями**. Переключение на `Dispatchers.IO` (`withContext(Dispatchers.IO)`) выполняется строго внутри `IssueDraftFileStorageImpl`. При отсутствии или повреждении файла возвращается пустой список, а `CancellationException` всегда пробрасывается.

### Задание 2. Сетевые заглушки (`:feature:issues:impl` / `data/remote`)
- **Интерфейс и реализация:** `IssueDraftRemoteDataSource` и `StubIssueDraftRemoteDataSource`.
- **Операции:**
  - `suspend fun fetchDrafts(): List<IssueDraftDto>`
  - `suspend fun pushDraft(draft: IssueDraftDto)`
  - `suspend fun deleteDraft(uid: String)`
- **Поведение:** Заглушки не выполняют реальных HTTP-запросов, логируют имя вызванного метода и аргументы через `Log.d("IssueDraftRemote", ...)` и имитируют сетевую задержку через `delay(...)` (например, `500 ms`).
- **Симуляция сбоя (для Задания 5):** Каждый N-й вызов (например, каждый 3-й вызов `fetchDrafts` / `pushDraft`) выбрасывает `IOException("Симуляция сбоя связи с сервером ГОСТ")`.

### Задание 3. Репозиторий (`:feature:issues:api` + `:feature:issues:impl`)
- **Доменная модель (`IssueDraft`):**
  - `uid: String` (`UUID.randomUUID().toString()`)
  - `owner: String`
  - `repoName: String`
  - `title: String`
  - `body: String`
  - `priority: DraftPriority` (`LOW`, `NORMAL`, `CRITICAL`)
  - `updatedAt: Long`
- **Контракт репозитория (`IssueDraftRepository` в `:feature:issues:api`):**
  ```kotlin
  interface IssueDraftRepository {
      fun observeDrafts(): Flow<List<IssueDraft>>
      suspend fun getDraft(uid: String): IssueDraft?
      suspend fun save(draft: IssueDraft)
      suspend fun delete(uid: String)
      suspend fun refreshFromBackend()
  }
  ```
- **Реализация (`IssueDraftRepositoryImpl`, `@Singleton`):**
  - Объединяет `IssueDraftFileStorage` и `IssueDraftRemoteDataSource`.
  - Внутри содержит `MutableStateFlow`, инициализируемый из `FileStorage`.
  - `observeDrafts()` возвращает `Flow<List<IssueDraft>>` (не `suspend`), сразу отдавая актуальный кэш и реагируя на каждое изменение (`save`, `delete`, `refreshFromBackend`).
  - `save` и `delete` обновляют кэш в `FileStorage` + `Flow` и вызывают сетевую заглушку. При `IOException` кэшированные данные сохраняются, а ошибка пробрасывается вверх.
  - `refreshFromBackend()` запрашивает данные у заглушки; при успехе перезаписывает кэш и обновляет `Flow`, при `IOException` сохраняет текущий кэш нетронутым.

### Задание 4. Связка с интерфейсом (MVI + Compose + Type-Safe Navigation)
- **Экран 1 — Список черновиков (`IssueDraftsScreen`):**
  - `IssueDraftsViewModel` подписывается на `ObserveIssueDraftsUseCase()` и хранит единый `StateFlow<IssueDraftsState>`.
  - Экран использует `collectAsStateWithLifecycle()`.
  - Поддерживает просмотр списка, создание нового черновика (`+`), открытие на редактирование по клику, удаление и принудительную синхронизацию (`RefreshClicked`).
- **Экран 2 — Создание / Редактирование (`CreateIssueScreen`):**
  - Расширяется поддержкой опционального `draftUid: String?` через `SavedStateHandle` и `CreateIssueDestination`.
  - При `draftUid != null` загружает существующий черновик через `GetIssueDraftUseCase(draftUid)` и предзаполняет форму.
  - Поддерживает действие «Сохранить черновик» (через `SaveIssueDraftUseCase`) и существующую публикацию в GitHub (`CreateIssueUseCase`).
  - После сохранения и возврата на `IssueDraftsScreen` обновлённый черновик появляется в списке автоматически через `Flow`, без ручного обновления.

### Задание 5. Ошибки, одноразовые события и корутины
- При выбросе `IOException` сетевой заглушкой кэшированные данные из `FileStorage` остаются на экране.
- Ошибка не записывается в `StateFlow` (чтобы не дублироваться при пересоздании конфигурации/повороте экрана), а отправляется однократно через `Channel(capacity = Channel.BUFFERED, onBufferOverflow = BufferOverflow.DROP_OLDEST)` (`UiEffect.ShowSnackbar`).
- Во всех `try / catch` в `data` и `presentation` слоях обязательно проверяется и пробрасывается `CancellationException`:
  ```kotlin
  catch (e: Exception) {
      if (e is CancellationException) throw e
      // обработка ошибки
  }
  ```

### Критерий самопроверки
Ни в одном `@Composable` и ни в одной `ViewModel` не должно быть `Dispatchers.IO`, `runBlocking`, `GlobalScope` и прямых обращений к `IssueDraftFileStorage`.

---

## 3. Задачи для агентов

> **Порядок выполнения:**
> - Задачи 1 и 2 могут выполняться параллельно.
> - Задача 3 зависит от Задач 1 и 2.
> - Задачи 4 и 5 зависят от Задачи 1 (могут выполняться параллельно с Задачей 3).
> - Задача 6 зависит от Задач 3, 4 и 5.
> - Задача 7 выполняется финально после всех задач.

---

### Задача 1 — Domain-слой и контракты (`:feature:issues:api`)
- **Модуль:** `:feature:issues:api` (pure Kotlin JVM, без Android-зависимостей).
- **Действия:**
  1. Создать `domain/model/IssueDraft.kt` (`IssueDraft` и `enum class DraftPriority`).
  2. Создать `domain/repository/IssueDraftRepository.kt`.
  3. Создать UseCase'ы в `domain/usecase/`:
     - `ObserveIssueDraftsUseCase` (возвращает `Flow<List<IssueDraft>>`, не `suspend`)
     - `GetIssueDraftUseCase`
     - `SaveIssueDraftUseCase`
     - `DeleteIssueDraftUseCase`
     - `RefreshIssueDraftsUseCase`
- **Критерий приёмки:** Модуль `:feature:issues:api` компилируется без зависимостей от Android SDK.

---

### Задача 2 — `FileStorage` и сетевые заглушки (`:feature:issues:impl` / Data)
- **Модуль:** `:feature:issues:impl` (`data/local`, `data/remote`, `data/mapper`).
- **Действия:**
  1. Создать `@Serializable data class IssueDraftDto` и мапперы `IssueDraftMapper.kt` (`toDomain()` / `toDto()`).
  2. Создать интерфейс `IssueDraftFileStorage` и реализацию `IssueDraftFileStorageImpl`:
     - Внедрение `@ApplicationContext context: Context` и `Json`.
     - Файл кэша: `File(context.filesDir, "issue_drafts_cache.json")`.
     - Потокобезопасность через `Mutex`.
     - Все методы (`loadAll`, `saveAll`, `getById`, `save`, `delete`) используют `withContext(Dispatchers.IO)` внутри и пробрасывают `CancellationException`.
  3. Создать интерфейс `IssueDraftRemoteDataSource` и реализацию `StubIssueDraftRemoteDataSource`:
     - Методы: `fetchDrafts`, `pushDraft`, `deleteDraft`.
     - Логирование вызовов и аргументов через `Log.d`, эмуляция задержки через `delay(500L)`.
     - Периодический выброс `IOException` (например, каждый 3-й вызов `fetchDrafts` / `pushDraft`) для проверки сценария Задания 5.
- **Критерий приёмки:** Все методы `FileStorage` являются main-safe `suspend`-функциями; `Dispatchers.IO` не протекает в репозиторий или UI.

---

### Задача 3 — Репозиторий и Hilt DI (`:feature:issues:impl` / Repository & DI)
- **Зависит от:** Задачи 1 и 2.
- **Действия:**
  1. Реализовать `IssueDraftRepositoryImpl` (`@Singleton`):
     - Хранит внутренний `MutableStateFlow<List<IssueDraft>>` и флаг/мьютекс начальной загрузки из `IssueDraftFileStorage`.
     - `observeDrafts()` возвращает `Flow<List<IssueDraft>>`, автоматически обновляющийся при `save`, `delete`, `refreshFromBackend`.
     - При ошибке сети (`IOException`) от `IssueDraftRemoteDataSource` локальный кэш в `FileStorage` и `Flow` сохраняется, а `IOException` пробрасывается вызывающей стороне.
     - Обязательный проброс `CancellationException` во всех `catch`.
  2. Зарегистрировать биндинги в `IssueDataModule`:
     - `IssueDraftFileStorageImpl` -> `IssueDraftFileStorage`
     - `StubIssueDraftRemoteDataSource` -> `IssueDraftRemoteDataSource`
     - `IssueDraftRepositoryImpl` -> `IssueDraftRepository`
- **Критерий приёмки:** Граф Hilt собирается без ошибок; изменения в кэше мгновенно эмитятся подписчикам `observeDrafts()`.

---

### Задача 4 — Экран списка черновиков (`IssueDraftsScreen`)
- **Зависит от:** Задачи 1.
- **Модуль:** `:feature:issues:impl` (`presentation/`).
- **Действия:**
  1. Создать `IssueDraftsContract.kt`:
     - `IssueDraftsState(drafts: List<IssueDraft>, isRefreshing: Boolean)` (без поля для одноразовых ошибок).
     - `IssueDraftsEvent`: `RefreshClicked`, `DraftClicked(uid)`, `CreateDraftClicked`, `DeleteDraftClicked(uid)`, `BackClicked`.
     - `IssueDraftsEffect`: `NavigateToEditor(owner, repoName, draftUid?)`, `NavigateBack`, `ShowSnackbar(message)`.
  2. Создать `IssueDraftsViewModel.kt`:
     - Подписка на `ObserveIssueDraftsUseCase` в `init`.
     - `Channel(BufferOverflow.DROP_OLDEST)` для `effect`.
     - Обработка ошибок при `refreshFromBackend` / `delete` через `ShowSnackbar`, с обязательным `if (e is CancellationException) throw e`.
  3. Создать `IssueDraftsScreen.kt` и `IssueDraftsRoute` в CRT-стиле проекта (`CrtScreenOverlay`, `Scaffold`, `SnackbarHost`, `collectAsStateWithLifecycle()`), а также `@Preview` (Filled, Empty, Refreshing).
- **Критерий приёмки:** При `IOException` список из кэша остаётся на экране, `Snackbar` показывается ровно один раз и не дублируется при повороте экрана.

---

### Задача 5 — Доработка экрана создания/редактирования (`CreateIssueScreen`)
- **Зависит от:** Задачи 1.
- **Модуль:** `:feature:issues:impl` (`presentation/`).
- **Действия:**
  1. Обновить `CreateIssueContract.kt`: добавить `draftUid: String?`, `priority: DraftPriority`, события `PriorityChanged`, `SaveDraftClicked`.
  2. Обновить `CreateIssueViewModel.kt`:
     - Извлечь `draftUid: String?` из `SavedStateHandle`.
     - Если `draftUid != null`, загрузить черновик через `GetIssueDraftUseCase` и заполнить форму.
     - Реализовать сохранение через `SaveIssueDraftUseCase` (создание нового или обновление существующего по `uid`).
     - Если при сохранении заглушка бросит `IOException` — черновик остаётся в кэше, пользователю отправляется одноразовый `ShowSnackbar` и выполняется возврат назад.
  3. Обновить `CreateIssueScreen.kt` и `CreateIssueForm.kt`: добавить селектор приоритета и кнопку «Сохранить в черновики», обновить `@Preview`.
- **Критерий приёмки:** После редактирования черновика и нажатия «Сохранить» список в `IssueDraftsScreen` обновляется сам через `Flow`.

---

### Задача 6 — Навигация и интеграция (`:app` и `:feature:repository:impl`)
- **Зависит от:** Задач 3, 4, 5.
- **Действия:**
  1. В `app/.../navigation/NavDestinations.kt`:
     - Добавить `@Serializable data class IssueDraftsDestination(val owner: String, val repoName: String)`.
     - Добавить параметр `val draftUid: String? = null` в `CreateIssueDestination`.
  2. В `app/.../navigation/GHClientNavHost.kt`:
     - Зарегистрировать `composable<IssueDraftsDestination>`.
     - Связать переходы `RepoDetailsDestination` -> `IssueDraftsDestination` <-> `CreateIssueDestination`.
  3. В `feature/repository/impl/.../RepoDetailsScreen.kt` (и контракте `RepoDetailsContract.kt` / `RepoDetailsViewModel.kt`) добавить кнопку перехода к черновикам рапортов для текущего репозитория.
- **Критерий приёмки:** Полный пользовательский сценарий от деталей репозитория до списка черновиков, создания, редактирования, удаления и возврата работает без сбоев.

---

### Задача 7 — Тестирование и аудит по критериям ДЗ (QA)
- **Зависит от:** Задач 2, 3, 4, 5, 6.
- **Действия:**
  1. Написать unit-тесты:
     - `IssueDraftFileStorageImplTest` (CRUD, пустой/битый файл).
     - `IssueDraftRepositoryImplTest` (реактивный `Flow`, сохранение кэша при `IOException`, проброс `CancellationException`).
     - `IssueDraftsViewModelTest` и `CreateIssueViewModelTest` (проверка `StateFlow` и одноразового `Channel` через `Turbine`).
  2. Провести статический аудит кода во всех `@Composable` и `*ViewModel`: убедиться в отсутствии `Dispatchers.IO`, `runBlocking`, `GlobalScope` и прямых вызовов `FileStorage`.
  3. Запустить тесты и сборку:
     - `./gradlew :feature:issues:api:test :feature:issues:impl:testDebugUnitTest :app:assembleDebug`
  4. Проверить UI на устройстве/эмуляторе:
     - Автообновление списка после редактирования черновика.
     - Поворот экрана после появления `Snackbar` об ошибке — повторного показа `Snackbar` быть не должно.
