package ir.amirroid.todo.models.repository

import ir.amirroid.todo.models.data.CategoryInfo
import ir.amirroid.todo.models.db.CategoryDao
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CategoryRepository @Inject constructor(
    private val dao: CategoryDao
) {
    suspend fun insertCategory(category: CategoryInfo) = dao.insertCategory(category)
    suspend fun insertCategoryIfNotExist(category: CategoryInfo) = dao.insertIsNotExists(category)
    suspend fun updateCategory(category: CategoryInfo) = dao.updateCategory(category)
    suspend fun delete(category: CategoryInfo) = dao.deleteCategory(category)
    fun getAll() = dao.getAllCategories()
    suspend fun getCategory(id: Int) = dao.getCategory(id)
}