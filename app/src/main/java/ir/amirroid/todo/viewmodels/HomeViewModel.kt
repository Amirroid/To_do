package ir.amirroid.todo.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amirroid.todo.models.data.CategoryInfo
import ir.amirroid.todo.models.data.TaskInfo
import ir.amirroid.todo.models.db.CategoryDao
import ir.amirroid.todo.models.repository.CategoryRepository
import ir.amirroid.todo.models.repository.TaskRepository
import ir.amirroid.todo.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val taskRepository: TaskRepository,
) : ViewModel() {
    private val _categories = MutableStateFlow(emptyList<CategoryInfo>())
    val categories = _categories.asStateFlow()

    private val _tasks = MutableStateFlow<HashMap<String, List<TaskInfo>>>(HashMap())
    val tasks = _tasks.asStateFlow()

    private val _searchTasks = MutableStateFlow<List<TaskInfo>>(emptyList())
    val searchTasks = _searchTasks.asStateFlow()

    private val _deleted = MutableStateFlow<MutableList<TaskInfo>>(mutableListOf())
    val deleted = _deleted.asStateFlow()


    val changes = MutableStateFlow(0)


    val removableMode = MutableStateFlow<Int?>(null)

    val favoriteMode = MutableStateFlow(false)


    val searchText = MutableStateFlow("")


    init {
        observeToCategories()
    }

    private fun observeToCategories() = viewModelScope.launch(Dispatchers.IO) {
        categoryRepository.getAll().collectLatest {
            _categories.value = it
            changes.value++
            observeToTasks(it.map { model -> model.id })
        }
    }

    private fun observeToTasks(ids: List<Int>) {
        getAllFavorites()
        ids.forEach {
            observeToTasksWithCategory(it)
        }
    }

    private fun getAllFavorites() = viewModelScope.launch(Dispatchers.IO) {
        taskRepository.getAllFavorites().collectLatest {
            changes.value++
            _tasks.value = tasks.value.apply {
                this[Constants.FAVORITE] = it
                changes.value++
            }
        }
    }

    private fun observeToTasksWithCategory(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        taskRepository.getAll(id).collectLatest { newTasks ->
            Log.d("hodish", "observeToTasksWithCategory: $newTasks")
            _tasks.value = tasks.value.apply {
                this[id.toString()] = newTasks
                changes.value++
            }
        }
    }

    fun addCategory(title: String) = viewModelScope.launch(Dispatchers.IO) {
        categoryRepository.insertCategory(
            CategoryInfo(title = title)
        )
    }

    fun addTask(task: TaskInfo) = viewModelScope.launch(Dispatchers.IO) {
        taskRepository.insert(
            task
        )
        changes.value++
    }

    fun deleteTask(task: TaskInfo) = viewModelScope.launch(Dispatchers.IO) {
        _deleted.value = deleted.value.apply {
            add(task)
        }
        changes.value++
        delay(300)
        taskRepository.delete(task)
    }

    fun searchTasks(title: String) = viewModelScope.launch(Dispatchers.IO) {
        searchText.value = title
        if (title.isEmpty()) {
            _searchTasks.value = emptyList()
        } else {
            _searchTasks.value = taskRepository.search(title)
        }
        changes.value++
    }

    fun deleteCategory(category: CategoryInfo) = viewModelScope.launch(Dispatchers.IO) {
        categoryRepository.delete(category)
    }

    fun editTask(task: TaskInfo, scheduleAlarm: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        taskRepository.update(task, scheduleAlarm)
    }

    fun editTaskWithSearch(task: TaskInfo, scheduleAlarm: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.update(task, scheduleAlarm)
            searchTasks(searchText.value)
        }
}