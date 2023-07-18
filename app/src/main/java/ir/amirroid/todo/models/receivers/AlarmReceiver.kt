package ir.amirroid.todo.models.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint
import ir.amirroid.todo.models.data.TaskInfo
import ir.amirroid.todo.models.helpers.NotificationHelper
import ir.amirroid.todo.models.repository.TaskRepository
import ir.amirroid.todo.utils.Constants
import ir.amirroid.todo.utils.getEndDate
import ir.amirroid.todo.utils.getStartDate
import ir.amirroid.todo.utils.isAlarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var repository: TaskRepository


    private val job = Job()
    private val scope = CoroutineScope(job)

    override fun onReceive(context: Context, intent: Intent) {
        val task = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Constants.TASK, TaskInfo::class.java)
        } else {
            intent.getParcelableExtra(Constants.TASK)
        }
        val type = intent.getStringExtra(Constants.TYPE)
        if (task != null) {
//            Toast.makeText(context, task.title, Toast.LENGTH_SHORT).show()
            scope.launch {
                val newTask = repository.getTask(task.id)
                if (newTask != null) {
                    if (newTask.isAlarm && checkTimeWithType(
                            type!!,
                            task,
                            newTask
                        ) && newTask.isDone.not()
                    ) {
                        notificationHelper.notify(newTask)
                    }
                }
            }
        }
    }

    private fun checkTimeWithType(type: String, taskInfo: TaskInfo, newTask: TaskInfo): Boolean {
        return when (type) {
            Constants.START -> {
                taskInfo.getStartDate() == newTask.getStartDate()
            }

            Constants.END -> {
                taskInfo.getEndDate() == newTask.getEndDate()
            }

            else -> false
        }
    }
}