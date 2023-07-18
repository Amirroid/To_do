package ir.amirroid.todo.models.repository

import ir.amirroid.todo.models.data.TaskInfo
import ir.amirroid.todo.models.db.TaskDao
import ir.amirroid.todo.models.helpers.AlarmHelper
import ir.amirroid.todo.utils.Constants
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TaskRepository @Inject constructor(
    private val dao: TaskDao,
    private val alarmHelper: AlarmHelper
) {
    suspend fun insert(task: TaskInfo) {
        task.id = getLastId()?.plus(1) ?: 1
        dao.insertTask(task)
        if (task.startDate != 0L && task.alarmMode != Constants.ALARM_NOT) {
            alarmHelper.setAlarm(
                task
            )
        }
    }

    fun getAllFavorites() = dao.getAllFavorites()
    suspend fun update(task: TaskInfo, scheduleAlarm: Boolean) {
        dao.updateTask(task)
        if (task.startDate != 0L && task.alarmMode != Constants.ALARM_NOT && scheduleAlarm) {
            alarmHelper.setAlarm(
                task
            )
        }
    }

    suspend fun delete(task: TaskInfo) = dao.deleteTask(task)
    fun getAll(categoryId: Int) = dao.getAllTasksWithCategory(categoryId)
    fun getAll() = dao.getAllTasks()
    suspend fun search(title: String) = dao.search(title)
    private suspend fun getLastId() = dao.getLastId()
    suspend fun getTask(id: Int) = dao.getTask(id)
}