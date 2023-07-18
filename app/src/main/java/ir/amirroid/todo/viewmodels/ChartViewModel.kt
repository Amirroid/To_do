package ir.amirroid.todo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amirroid.todo.models.data.TaskInfo
import ir.amirroid.todo.models.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChartViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {
    private val _tasks = MutableStateFlow<List<TaskInfo>>(emptyList())
    val tasks = _tasks.asStateFlow()

    init {
        collectToTasks()
    }

    private fun collectToTasks() = viewModelScope.launch(Dispatchers.IO) {
        repository.getAll().collectLatest {
            _tasks.value = it
        }
    }
}