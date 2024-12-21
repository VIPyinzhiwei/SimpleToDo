package com.example.simpletodo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.simpletodo.data.model.Tag
import com.example.simpletodo.data.model.Todo

@Database(
    entities = [Todo::class, Tag::class],
    version = 5
)
@TypeConverters(Converters::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun tagDao(): TagDao

    companion object {
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE todos ADD COLUMN dueDate TEXT"
                )
                database.execSQL(
                    "ALTER TABLE todos ADD COLUMN enableReminder INTEGER NOT NULL DEFAULT 0"
                )
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE todos ADD COLUMN note TEXT"
                )
            }
        }
    }
} 