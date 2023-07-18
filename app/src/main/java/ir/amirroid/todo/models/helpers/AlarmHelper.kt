package ir.amirroid.todo.models.helpers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amirroid.todo.models.data.TaskInfo
import ir.amirroid.todo.models.receivers.AlarmReceiver
import ir.amirroid.todo.utils.Constants
import ir.amirroid.todo.utils.getEndDate
import ir.amirroid.todo.utils.getStartDate
import java.util.Date
import javax.inject.Inject

class AlarmHelper @Inject constructor(
    @ApplicationContext val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private lateinit var pi: PendingIntent

    fun setAlarm(task: TaskInfo) {
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(Constants.TASK, task)
        val nowTime = Date().time
        when (task.alarmMode) {
            Constants.ALARM_NOT -> Unit
            Constants.ALARM_ONE_TIME -> {
                intent.putExtra(Constants.TYPE, Constants.START)
                pi = PendingIntent.getBroadcast(
                    context,
                    (0..Int.MAX_VALUE).random(),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP, task.getStartDate(), pi
                )
            }

            Constants.ALARM_RANGE -> {
                intent.putExtra(Constants.TYPE, Constants.START)
                pi = PendingIntent.getBroadcast(
                    context,
                    (0..Int.MAX_VALUE).random(),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager. setExact(
                    AlarmManager.RTC_WAKEUP, task.getStartDate(), pi
                )
                intent.putExtra(Constants.TYPE, Constants.END)
                val pi2 = PendingIntent.getBroadcast(
                    context,
                    (0..Int.MAX_VALUE).random(),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP, task.getEndDate(), pi2
                )
            }
        }
    }
}