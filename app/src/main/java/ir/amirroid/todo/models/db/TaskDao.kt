package ir.amirroid.todo.models.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ir.amirroid.todo.models.data.TaskInfo
import ir.amirroid.todo.utils.Constants
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {

    @Query("SELECT * FROM ${Constants.TASK} WHERE categoryId = :categoryId")
    fun getAllTasksWithCategory(categoryId: Int): Flow<List<TaskInfo>>

    @Query("SELECT * FROM ${Constants.TASK}")
    fun getAllTasks(): Flow<List<TaskInfo>>

    @Query("SELECT * FROM ${Constants.TASK} WHERE isFavorite = 1")
    fun getAllFavorites(): Flow<List<TaskInfo>>


    @Insert
    suspend fun insertTask(taskInfo: TaskInfo)

    @Update
    suspend fun updateTask(taskInfo: TaskInfo)


    @Delete
    suspend fun deleteTask(taskInfo: TaskInfo)


    @Query("SELECT * FROM ${Constants.TASK} WHERE title LIKE '%' || :title || '%'")
    suspend fun search(title: String): List<TaskInfo>

    @Query("SELECT MAX(id) FROM ${Constants.TASK}")
    suspend fun getLastId(): Int?

    @Query("SELECT * FROM ${Constants.TASK} WHERE id = :id")
    suspend fun getTask(id: Int): TaskInfo?

}