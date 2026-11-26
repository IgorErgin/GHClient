# Спецификация: Локальный кэш на Room и ручные миграции БД (`GHClientDatabase`)

Документ адаптирует домашнее задание по модулю «Базы данных (Room)» (**Базовая задача + Усложнение 1**: кэш на Room с корутинами, экспорт схем, несколько версий модели с ручной сменой типа столбца без `@AutoMigration`, тестирование через `MigrationTestHelper` и проверка на устройстве в Database Inspector) под архитектуру проекта **GHClient**.

Логически продолжает спецификацию из [ISSUE_DRAFTS_SPEC.md](file:///D:/Android/Projects/GHClient/ISSUE_DRAFTS_SPEC.md): переносит локальный кэш черновиков `IssueDraft` с файлового хранилища (`IssueDraftFileStorage`) на Room в модуле `:core:database` и добавляет версионированные миграции в `GHClientDatabase`.

---

## 1. Текущая отправная точка в проекте

- **База данных:** [`GHClientDatabase`](file:///D:/Android/Projects/GHClient/core/database/src/main/java/com/ergin/ghclient/core/database/GHClientDatabase.kt) (`ghclient.db`).
- **Текущая версия:** `version = 2`, `exportSchema = false`.
- **Существующие таблицы:** `repos` (`RepoEntity`), `repo_details` (`RepoDetailsEntity`), `user_profiles` (`UserProfileEntity`), `issues` (`IssueEntity`).
- **DI:** [`DatabaseModule`](file:///D:/Android/Projects/GHClient/core/database/src/main/java/com/ergin/ghclient/core/database/di/DatabaseModule.kt) сейчас использует `.fallbackToDestructiveMigration(false)` и не содержит ручных `Migration`.

---

## 2. Соответствие требованиям ДЗ

### Базовая задача: Локальный кэш `IssueDraft` на Room (`suspend` + `Flow`)

Переводим локальное хранилище черновиков `IssueDraft` на Room в модуле `:core:database` (используем в `IssueDraftRepositoryImpl` в `:feature:issues:impl`).

1. **Сущность `IssueDraftEntity` (таблица `issue_drafts`):**
   - `@PrimaryKey val uid: String`
   - `val owner: String`
   - `val repoName: String`
   - `val title: String`
   - `val body: String`
   - `val priority: ...` *(меняется между версиями 3 и 4 — см. Усложнение 1)*
   - `val updatedAt: Long`

2. **DAO `IssueDraftDao` (все методы — `Flow` и `suspend`, без блокирующих вызовов):**
   - **Наблюдение за списком:**
     ```kotlin
     @Query("SELECT * FROM issue_drafts ORDER BY updatedAt DESC")
     fun observeAll(): Flow<List<IssueDraftEntity>>
     ```
   - **Получение конкретного дела:**
     ```kotlin
     @Query("SELECT * FROM issue_drafts WHERE uid = :uid LIMIT 1")
     suspend fun getById(uid: String): IssueDraftEntity?
     ```
   - **Сохранение / изменение конкретного дела:**
     ```kotlin
     @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun upsert(draft: IssueDraftEntity)

     @Update
     suspend fun update(draft: IssueDraftEntity)
     ```
   - **Сохранение списка дел (для `refreshFromBackend`):**
     ```kotlin
     @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertAll(drafts: List<IssueDraftEntity>)

     @Query("DELETE FROM issue_drafts")
     suspend fun clearAll()

     @Transaction
     suspend fun replaceAll(drafts: List<IssueDraftEntity>) {
         clearAll()
         insertAll(drafts)
     }
     ```
   - **Удаление конкретного дела:**
     ```kotlin
     @Query("DELETE FROM issue_drafts WHERE uid = :uid")
     suspend fun deleteById(uid: String)
     ```

3. **Подключение к слою данных (`:feature:issues:impl`):**
   - В [`IssueDraftRepositoryImpl`](file:///D:/Android/Projects/GHClient/feature/issues/impl/src/main/java/com/ergin/ghclient/feature/issues/impl/data/repository/IssueDraftRepositoryImpl.kt) заменяем `IssueDraftFileStorage` на `IssueDraftDao`:
     - `observeDrafts()` подписывается напрямую на `issueDraftDao.observeAll().map { list -> list.map { it.toDomain() } }` (реактивный поток Room без ручного `MutableStateFlow` для кэша).
     - `getDraft(uid)` вызывает `issueDraftDao.getById(uid)?.toDomain()`.
     - `save(draft)` вызывает `issueDraftDao.upsert(draft.toEntity())`, затем сетевую заглушку.
     - `delete(uid)` вызывает `issueDraftDao.deleteById(uid)`, затем сетевую заглушку.
     - `refreshFromBackend()` при успехе вызывает `issueDraftDao.replaceAll(...)`, а при `IOException` не трогает таблицу `issue_drafts` (данные остаются на экране).

---

### Усложнение 1: Несколько версий модели, экспорт схем, ручные миграции и `MigrationTestHelper`

Чтобы наглядно продемонстрировать **экспорт схем с начальной версии**, **ручную миграцию со сменой типа столбца и преобразованием данных** и **обновление поверх установленного приложения**, используем поэтапное версионирование `GHClientDatabase`:

#### Шаг А. Включение экспорта схем
- В `core/database/build.gradle.kts` настраиваем выгрузку JSON-схем Room через KSP (`ksp { arg("room.schemaLocation", "$projectDir/schemas") }`) и подключаем `schemas` как `androidTest.assets.srcDirs` для `MigrationTestHelper`.
- В `GHClientDatabase` ставим `exportSchema = true`.
- Фиксируем базовую схему **версии 2** (`2.json`) до добавления новой таблицы (для полноты истории миграций `2 -> 3 -> 4`).

#### Шаг Б. Версия 3 (`version = 3`) — таблица `issue_drafts` со строковым `priority`
- В `IssueDraftEntity` (v3) поле `priority` имеет тип **`String`** (`TEXT` в SQLite): хранятся значения `"LOW"`, `"NORMAL"`, `"CRITICAL"`.
- Ручная миграция **`MIGRATION_2_3`** (без `@AutoMigration`):
  ```sql
  CREATE TABLE IF NOT EXISTS `issue_drafts` (
      `uid` TEXT NOT NULL,
      `owner` TEXT NOT NULL,
      `repoName` TEXT NOT NULL,
      `title` TEXT NOT NULL,
      `body` TEXT NOT NULL,
      `priority` TEXT NOT NULL,
      `updatedAt` INTEGER NOT NULL,
      PRIMARY KEY(`uid`)
  )
  ```
- Генерируется схема **`3.json`**.
- **Контрольная точка для живой проверки:**
  - На этом этапе можно собрать и установить приложение версии **3** на устройство/эмулятор, создать 2–3 черновика с разными приоритетами (`LOW`, `NORMAL`, `CRITICAL`) и убедиться в **Database Inspector**, что в столбце `priority` лежат строки (`TEXT`).

#### Шаг В. Версия 4 (`version = 4`) — смена типа столбца `priority` (`TEXT` → `INTEGER`) и преобразование данных
- В новой модели `IssueDraftEntity` (v4) меняем тип поля `priority` со `String` на **`Int`** (`INTEGER` в SQLite):
  - `0` → `DraftPriority.LOW`
  - `1` → `DraftPriority.NORMAL`
  - `2` → `DraftPriority.CRITICAL`
- (Опционально добавляем `val isSynced: Int = 0` / `Boolean` для демонстрации добавления столбца с дефолтным значением вместе со сменой типа, но ключевое требование — **смена типа столбца с преобразованием существующих данных**.)
- В SQLite нельзя напрямую поменять тип существующего столбца через `ALTER COLUMN`, поэтому пишем **ручную миграцию `MIGRATION_3_4`** (класс `Migration(3, 4)` — **не `@AutoMigration`**):
  1. Создаём новую таблицу `issue_drafts_new` с нужной схемой (`priority INTEGER NOT NULL`).
  2. Копируем все существующие записи с преобразованием `TEXT -> INTEGER`:
     ```sql
     INSERT INTO `issue_drafts_new` (`uid`, `owner`, `repoName`, `title`, `body`, `priority`, `updatedAt`)
     SELECT `uid`, `owner`, `repoName`, `title`, `body`,
            CASE UPPER(`priority`)
                WHEN 'LOW' THEN 0
                WHEN 'CRITICAL' THEN 2
                ELSE 1
            END,
            `updatedAt`
     FROM `issue_drafts`
     ```
  3. Удаляем старую таблицу: `DROP TABLE \`issue_drafts\``.
  4. Переименовываем новую таблицу: `ALTER TABLE \`issue_drafts_new\` RENAME TO \`issue_drafts\``.
- Генерируется схема **`4.json`**.
- В [`DatabaseModule`](file:///D:/Android/Projects/GHClient/core/database/src/main/java/com/ergin/ghclient/core/database/di/DatabaseModule.kt) подключаем `.addMigrations(MIGRATION_2_3, MIGRATION_3_4)` и **убираем** `.fallbackToDestructiveMigration(...)`, чтобы при любой ошибке миграции база падала явно, а не тихо удаляла данные пользователя.

#### Шаг Г. Тесты на `MigrationTestHelper`
- В `core/database/build.gradle.kts` добавляем:
  - `androidTestImplementation(libs.room.testing)`
  - `androidTestImplementation(libs.androidx.junit)`
  - `androidTestImplementation(libs.androidx.espresso.core)` / `testRunner`
- Создаём `GHClientDatabaseMigrationTest` в `core/database/src/androidTest/...`:
  - `migrate2To3()`: создаёт БД v2 с тестовыми `repos`, запускает `runMigrationsAndValidate(TEST_DB, 3, true, MIGRATION_2_3)`, проверяет наличие `issue_drafts` и сохранность `repos`.
  - `migrate3To4_convertsPriorityTextToInt()`: создаёт БД v3, вставляет через SQL 3 черновика со строковыми приоритетами (`"LOW"`, `"NORMAL"`, `"CRITICAL"`), запускает `runMigrationsAndValidate(TEST_DB, 4, true, MIGRATION_3_4)`, делает `SELECT priority FROM issue_drafts` и проверяет, что значения преобразовались в `0`, `1`, `2`, а остальные поля не потерялись.
  - `migrateAll_2To4()`: прогоняет сквозную миграцию `2 → 4` через `Room.databaseBuilder(...).addMigrations(MIGRATION_2_3, MIGRATION_3_4)`.

---

## 3. Задачи для агентов

> **Порядок выполнения:**
> - **Задача 1** (настройка Gradle, экспорта схем и фиксация v2) выполняется первой.
> - **Задача 2** (v3: `IssueDraftEntity` со `String` priority, `IssueDraftDao`, `MIGRATION_2_3`, генерация `3.json`) зависит от Задачи 1.
> - **Задача 3** (v4: смена типа `priority` на `Int`, `MIGRATION_3_4`, генерация `4.json`, обновление `DatabaseModule`) зависит от Задачи 2.
> - **Задача 4** (перевод `IssueDraftRepositoryImpl` и мапперов `:feature:issues:impl` на `IssueDraftDao`) зависит от Задачи 3 (контракт `IssueDraftDao` можно согласовать с Задачей 2).
> - **Задача 5** (тесты `MigrationTestHelper` и unit-тесты `IssueDraftRepositoryImplTest`) зависит от Задач 3 и 4.
> - **Задача 6** (финальная сборка, прогон тестов и инструкция проверки на устройстве в Database Inspector).

---

### Задача 1 — Настройка экспорта схем и тестовой конфигурации Room (`:core:database`, `libs.versions.toml`)
- **Область:** `gradle/libs.versions.toml`, `core/database/build.gradle.kts`, `GHClientDatabase.kt`.
- **Действия:**
  1. Добавить `room-testing = { group = "androidx.room", name = "room-testing", version.ref = "room" }` в `gradle/libs.versions.toml`.
  2. В `core/database/build.gradle.kts`:
     - Настроить `ksp { arg("room.schemaLocation", "$projectDir/schemas") }`.
     - Добавить `sourceSets { getByName("androidTest").assets.srcDirs("$projectDir/schemas") }`.
     - Настроить `testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"`.
     - Добавить `androidTestImplementation` зависимости (`room-testing`, `androidx-junit`, `androidx-espresso-core` / `coroutines-test`).
  3. В `GHClientDatabase.kt` включить `exportSchema = true` (оставляя `version = 2`) и собрать `:core:database` для генерации `schemas/.../2.json`.
- **Критерий приёмки:** Файл `core/database/schemas/com.ergin.ghclient.core.database.GHClientDatabase/2.json` сгенерирован и присутствует в проекте.

---

### Задача 2 — Модель v3 (`IssueDraftEntity`, `IssueDraftDao`, `MIGRATION_2_3`)
- **Зависит от:** Задачи 1.
- **Область:** `:core:database`.
- **Действия:**
  1. Создать `IssueDraftEntity` (v3, `priority: String`) в `core/database/entity/IssueDraftEntity.kt`.
  2. Создать `IssueDraftDao` с методами `observeAll(): Flow<List<IssueDraftEntity>>`, `suspend getById(uid)`, `suspend upsert(...)`, `suspend update(...)`, `suspend replaceAll(...)`, `suspend deleteById(uid)`.
  3. Создать объект миграций `GHClientMigrations.kt` в `core/database/migration/` и реализовать `MIGRATION_2_3`.
  4. Подключить `IssueDraftEntity` и `abstract fun issueDraftDao(): IssueDraftDao` в `GHClientDatabase`, поднять версию до `version = 3`.
  5. Собрать `:core:database`, чтобы KSP сгенерировал `3.json` (проверить, что `priority` в `3.json` имеет `affinity: "TEXT"`).
- **Критерий приёмки:** Схемы `2.json` и `3.json` сгенерированы; при желании можно поставить сборку v3 на устройство для создания тестовых записей перед обновлением на v4.

---

### Задача 3 — Модель v4 (смена типа столбца `priority` и `MIGRATION_3_4`) и DI (`:core:database`)
- **Зависит от:** Задачи 2.
- **Область:** `:core:database`.
- **Действия:**
  1. Изменить в `IssueDraftEntity` тип поля `priority` с `String` на `Int`.
  2. Реализовать `MIGRATION_3_4` в `GHClientMigrations.kt`:
     - Создание временной таблицы `issue_drafts_new` с `priority INTEGER NOT NULL`.
     - Перенос строк из `issue_drafts` с `CASE UPPER(priority)` (`'LOW' -> 0`, `'CRITICAL' -> 2`, иначе `1`).
     - Удаление старой таблицы и переименование `issue_drafts_new` в `issue_drafts`.
  3. Поднять версию в `GHClientDatabase` до `version = 4`.
  4. Обновить [`DatabaseModule.kt`](file:///D:/Android/Projects/GHClient/core/database/src/main/java/com/ergin/ghclient/core/database/di/DatabaseModule.kt):
     - Добавить `.addMigrations(MIGRATION_2_3, MIGRATION_3_4)`.
     - Убрать `.fallbackToDestructiveMigration(...)`.
     - Предоставить `@Provides @Singleton fun provideIssueDraftDao(database: GHClientDatabase): IssueDraftDao`.
  5. Собрать `:core:database` и убедиться, что сгенерировался `4.json` (где `priority` имеет `affinity: "INTEGER"`).
- **Критерий приёмки:** Схемы `2.json`, `3.json`, `4.json` лежат в `core/database/schemas/...`, миграции зарегистрированы в `DatabaseModule`, деструктивный фоллбек удалён.

---

### Задача 4 — Перевод `:feature:issues:impl` с `FileStorage` на Room DAO
- **Зависит от:** Задачи 3.
- **Область:** `:feature:issues:impl`.
- **Действия:**
  1. Обновить мапперы `IssueDraftMapper.kt`: добавить конвертацию `IssueDraftEntity <-> IssueDraft` (маппинг `priority: Int` ↔ `DraftPriority`: `0 -> LOW`, `1 -> NORMAL`, `2 -> CRITICAL`).
  2. Обновить [`IssueDraftRepositoryImpl.kt`](file:///D:/Android/Projects/GHClient/feature/issues/impl/src/main/java/com/ergin/ghclient/feature/issues/impl/data/repository/IssueDraftRepositoryImpl.kt):
     - Заменить зависимость `IssueDraftFileStorage` на `IssueDraftDao`.
     - `observeDrafts()` → `issueDraftDao.observeAll().map { ... }` (реактивное чтение из Room).
     - `getDraft(uid)` → `issueDraftDao.getById(uid)?.toDomain()`.
     - `save(draft)` → `issueDraftDao.upsert(draft.toEntity())` + вызов `remoteDataSource.pushDraft(...)`.
     - `delete(uid)` → `issueDraftDao.deleteById(uid)` + вызов `remoteDataSource.deleteDraft(...)`.
     - `refreshFromBackend()` → при успехе `issueDraftDao.replaceAll(...)`, при `IOException` кэш в Room сохраняется нетронутым, `CancellationException` пробрасывается.
  3. Проверить [`IssueDataModule.kt`](file:///D:/Android/Projects/GHClient/feature/issues/impl/src/main/java/com/ergin/ghclient/feature/issues/impl/di/IssueDataModule.kt) (очистить неиспользуемые биндинги `FileStorage` или оставить по необходимости, если решим удалить старый файл-кэш).
- **Критерий приёмки:** Черновики `IssueDraft` сохраняются в таблицу `issue_drafts` базы `ghclient.db`, UI (`IssueDraftsScreen` и `CreateIssueScreen`) работает реактивно поверх `Flow` из Room без изменений презентационного слоя.

---

### Задача 5 — Тестирование (`MigrationTestHelper` + Unit-тесты)
- **Зависит от:** Задач 3 и 4.
- **Область:** `:core:database` (`androidTest`), `:feature:issues:impl` (`test`).
- **Действия:**
  1. Написать инструментальный тест `GHClientDatabaseMigrationTest` в `core/database/src/androidTest/...`:
     - Проверка `MIGRATION_2_3` (сохранение существующих `repos` + появление `issue_drafts`).
     - Проверка `MIGRATION_3_4` (вставка записей в v3 со строковым `priority` → миграция → проверка валидации схемы v4 и преобразования `"LOW" -> 0`, `"NORMAL" -> 1`, `"CRITICAL" -> 2`).
     - Проверка сквозной миграции `2 -> 3 -> 4`.
  2. Обновить [`IssueDraftRepositoryImplTest.kt`](file:///D:/Android/Projects/GHClient/feature/issues/impl/src/test/java/com/ergin/ghclient/feature/issues/impl/data/repository/IssueDraftRepositoryImplTest.kt) под моки/фейк `IssueDraftDao` вместо `IssueDraftFileStorage` + добавить тесты маппера `IssueDraftEntity` ↔ `IssueDraft`.
- **Критерий приёмки:** `MigrationTestHelper` успешно валидирует схемы и перенос данных; все unit-тесты `:feature:issues:impl` проходят.

---

### Задача 6 — Верификация на устройстве и сборка
- **Зависит от:** Задачи 5.
- **Действия:**
  1. Проверить сборку и тесты:
     - `./gradlew :core:database:compileDebugAndroidTestKotlin :feature:issues:impl:testDebugUnitTest :app:assembleDebug`
     - `./gradlew :core:database:connectedDebugAndroidTest` (при подключённом эмуляторе/устройстве).
  2. Провести ручную проверку обновления схемы на эмуляторе (для отчёта/преподавателя):
     - Установить сборку с `v3`, создать несколько черновиков, посмотреть таблицу `issue_drafts` в **App Inspection → Database Inspector**.
     - Установить сборку с `v4` **поверх** (без Uninstall), снова открыть **Database Inspector** и убедиться, что записи пережили обновление и столбец `priority` изменил тип с `TEXT` на `INTEGER` (`0/1/2`).
  3. Подготовить краткую сводку проверки для преподавателя.
