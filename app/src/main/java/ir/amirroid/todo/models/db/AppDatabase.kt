package ir.amirroid.todo.models.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.amirroid.todo.models.data.CategoryInfo
import ir.amirroid.todo.models.data.TaskInfo


@Database(
    version = 14,
    exportSchema = false,
    entities = [
        TaskInfo::class,
        CategoryInfo::class
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun taskDao(): TaskDao
}