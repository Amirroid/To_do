package ir.amirroid.todo.models.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ir.amirroid.todo.models.data.CategoryInfo
import ir.amirroid.todo.utils.Constants
import kotlinx.coroutines.flow.Flow


@Dao
interface CategoryDao {
    @Query("SELECT * FROM ${Constants.CATEGORY}")
    fun getAllCategories(): Flow<List<CategoryInfo>>

    @Insert
    suspend fun insertCategory(categoryInfo: CategoryInfo)

    @Update
    suspend fun updateCategory(categoryInfo: CategoryInfo)

    @Delete
    suspend fun deleteCategory(categoryInfo: CategoryInfo)

    @Query("SELECT EXISTS(SELECT * FROM ${Constants.CATEGORY} WHERE id = :id)")
    fun existCategory(id: Int): Boolean

    @Transaction
    suspend fun insertIsNotExists(categoryInfo: CategoryInfo) {
        if (existCategory(categoryInfo.id).not()) {
            insertCategory(categoryInfo)
        }
    }

    @Query("SELECT * FROM ${Constants.CATEGORY} WHERE id = :id")
    suspend fun getCategory(id:Int):CategoryInfo
}