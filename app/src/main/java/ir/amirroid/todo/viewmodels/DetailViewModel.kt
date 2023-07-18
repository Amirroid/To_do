package ir.amirroid.todo.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amirroid.todo.models.data.CategoryInfo
import ir.amirroid.todo.models.data.TaskInfo
import ir.amirroid.todo.models.repository.CategoryRepository
import ir.amirroid.todo.models.repository.TaskRepository
import ir.amirroid.todo.utils.getEmptyTask
import ir.amirroid.todo.utils.getIfExist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val taskRepository: TaskRepository,
) : ViewModel() {
    private val _task = MutableStateFlow(getEmptyTask())
    val task = _task.asStateFlow()
    var title by mutableStateOf("")

    val categories = MutableStateFlow(listOf<CategoryInfo>())

    var isStar by mutableStateOf(false)

    var isDateDialog by mutableStateOf(false)

    var isEndDateDialog by mutableStateOf(false)

    var isStartTimePicker by mutableStateOf(false)

    var isTimePickerDialog by mutableStateOf(false)

    var startDate by mutableLongStateOf(0L)

    var endDate by mutableLongStateOf(0L)

    var startTime by mutableLongStateOf(0L)

    var endTime by mutableLongStateOf(0L)

    var alarmMode by mutableIntStateOf(0)

    var categoryIndex by mutableIntStateOf(0)

    var editMode by mutableStateOf(false)

    var done by mutableStateOf(false)

    fun loadTask(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        taskRepository.getTask(id).let {
            _task.value = it!!
            isStar = it.isFavorite
            startTime = it.startTime
            endTime = it.endTime
            startDate = it.startDate
            endDate = it.endDate
            alarmMode = it.alarmMode
            if (categories.value.isNotEmpty()) {
                categoryIndex =
                    categories.value.getIfExist(categoryRepository.getCategory(it.categoryId)) ?: 0
            }
            done = it.isDone
            title = it.title
        }
    }

    fun cancelEdit() {
        loadTask(_task.value)
    }

    private fun loadTask(task: TaskInfo) = viewModelScope.launch(Dispatchers.IO) {
        task.let {
            isStar = it.isFavorite
            startTime = it.startTime
            endTime = it.endTime
            startDate = it.startDate
            endDate = it.endDate
            alarmMode = it.alarmMode
            if (categories.value.isNotEmpty()) {
                categoryIndex =
                    categories.value.getIfExist(categoryRepository.getCategory(it.categoryId)) ?: 0
            }
            done = it.isDone
            title = it.title
        }
    }


    init {
        loadCategories()
    }

    private fun loadCategories() = viewModelScope.launch(Dispatchers.IO) {
        categoryRepository.getAll().collectLatest {
            categories.value = it
            if (_task.value.categoryId != 0) {
                categoryIndex =
                    categories.value.getIfExist(categoryRepository.getCategory(_task.value.categoryId))
                        ?: 0
            }
        }
    }

    fun editTask(scheduleAlarm:Boolean = true) = viewModelScope.launch(Dispatchers.IO) {
        taskRepository.update(
            task.value.copy(
                title = title,
                categoryId = categories.value[categoryIndex].id,
                isDone = done,
                startDate = startDate,
                endDate = endDate,
                startTime = startTime,
                endTime = endTime,
                isFavorite = isStar,
                alarmMode = alarmMode,
            ),
            scheduleAlarm
        )
    }
}