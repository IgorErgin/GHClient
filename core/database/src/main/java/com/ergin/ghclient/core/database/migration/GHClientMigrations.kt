package com.ergin.ghclient.core.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object GHClientMigrations {

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
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
                """.trimIndent()
            )
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `issue_drafts_new` (
                    `uid` TEXT NOT NULL,
                    `owner` TEXT NOT NULL,
                    `repoName` TEXT NOT NULL,
                    `title` TEXT NOT NULL,
                    `body` TEXT NOT NULL,
                    `priority` INTEGER NOT NULL,
                    `updatedAt` INTEGER NOT NULL,
                    PRIMARY KEY(`uid`)
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                INSERT INTO `issue_drafts_new` (`uid`, `owner`, `repoName`, `title`, `body`, `priority`, `updatedAt`)
                SELECT `uid`, `owner`, `repoName`, `title`, `body`,
                    CASE UPPER(`priority`)
                        WHEN 'LOW' THEN 0
                        WHEN 'CRITICAL' THEN 2
                        ELSE 1
                    END,
                    `updatedAt`
                FROM `issue_drafts`
                """.trimIndent()
            )

            db.execSQL("DROP TABLE `issue_drafts`")

            db.execSQL("ALTER TABLE `issue_drafts_new` RENAME TO `issue_drafts`")
        }
    }
}
