package com.ergin.ghclient.core.database.migration

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.ergin.ghclient.core.database.GHClientDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GHClientDatabaseMigrationTest {

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        GHClientDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory(),
    )

    @Test
    fun migrate2To3() {
        var db = helper.createDatabase(TEST_DB, 2)
        db.execSQL(
            """
            INSERT INTO repos (id, name, ownerName, ownerAvatarUrl, description, language, stars, isFavorite)
            VALUES (1, 'kotlin', 'jetbrains', '', 'Kotlin Programming Language', 'Kotlin', 45000, 1)
            """.trimIndent()
        )
        db.close()

        db = helper.runMigrationsAndValidate(
            TEST_DB,
            3,
            true,
            GHClientMigrations.MIGRATION_2_3
        )

        val cursorRepo = db.query("SELECT * FROM repos WHERE id = 1")
        assertTrue(cursorRepo.moveToFirst())
        assertEquals("kotlin", cursorRepo.getString(cursorRepo.getColumnIndexOrThrow("name")))
        cursorRepo.close()

        val cursorDrafts = db.query("SELECT * FROM issue_drafts")
        assertEquals(0, cursorDrafts.count)
        cursorDrafts.close()
    }

    @Test
    fun migrate3To4_convertsPriorityTextToInt() {
        var db = helper.createDatabase(TEST_DB, 3)
        db.execSQL(
            """
            INSERT INTO issue_drafts (uid, owner, repoName, title, body, priority, updatedAt)
            VALUES ('1', 'jetbrains', 'kotlin', 'Low Draft', 'Body 1', 'LOW', 100)
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO issue_drafts (uid, owner, repoName, title, body, priority, updatedAt)
            VALUES ('2', 'jetbrains', 'kotlin', 'Normal Draft', 'Body 2', 'NORMAL', 200)
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO issue_drafts (uid, owner, repoName, title, body, priority, updatedAt)
            VALUES ('3', 'jetbrains', 'kotlin', 'Critical Draft', 'Body 3', 'CRITICAL', 300)
            """.trimIndent()
        )
        db.close()

        db = helper.runMigrationsAndValidate(
            TEST_DB,
            4,
            true,
            GHClientMigrations.MIGRATION_3_4
        )

        val cursor1 = db.query("SELECT priority, title FROM issue_drafts WHERE uid = '1'")
        assertTrue(cursor1.moveToFirst())
        assertEquals(0, cursor1.getInt(cursor1.getColumnIndexOrThrow("priority")))
        assertEquals("Low Draft", cursor1.getString(cursor1.getColumnIndexOrThrow("title")))
        cursor1.close()

        val cursor2 = db.query("SELECT priority, title FROM issue_drafts WHERE uid = '2'")
        assertTrue(cursor2.moveToFirst())
        assertEquals(1, cursor2.getInt(cursor2.getColumnIndexOrThrow("priority")))
        assertEquals("Normal Draft", cursor2.getString(cursor2.getColumnIndexOrThrow("title")))
        cursor2.close()

        val cursor3 = db.query("SELECT priority, title FROM issue_drafts WHERE uid = '3'")
        assertTrue(cursor3.moveToFirst())
        assertEquals(2, cursor3.getInt(cursor3.getColumnIndexOrThrow("priority")))
        assertEquals("Critical Draft", cursor3.getString(cursor3.getColumnIndexOrThrow("title")))
        cursor3.close()
    }

    @Test
    fun migrateAll_2To4() {
        var db = helper.createDatabase(TEST_DB, 2)
        db.close()

        db = helper.runMigrationsAndValidate(
            TEST_DB,
            4,
            true,
            GHClientMigrations.MIGRATION_2_3,
            GHClientMigrations.MIGRATION_3_4
        )
        db.close()
    }

    private companion object {
        const val TEST_DB = "migration-test"
    }
}
