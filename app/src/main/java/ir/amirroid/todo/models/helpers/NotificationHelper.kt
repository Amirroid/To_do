package ir.amirroid.todo.models.helpers

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amirroid.todo.R
import ir.amirroid.todo.models.data.TaskInfo
import ir.amirroid.todo.ui.features.alarm_activity.AlarmActivity
import ir.amirroid.todo.utils.Constants
import javax.inject.Inject

class NotificationHelper @Inject constructor(
    @ApplicationContext val context: Context
) {
    private val manager = NotificationManagerCompat.from(context)
    fun notify(task: TaskInfo) {
        val intent = Intent(context, AlarmActivity::class.java)
        intent.putExtra(Constants.TASK, task)
        val pi = PendingIntent.getActivity(
            context,
            task.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, Constants.CHANNEL_ID).apply {
            setContentText(task.title)
            setContentTitle(context.getString(R.string.app_name))
            setSmallIcon(R.drawable.round_alarm_24)
            setContentIntent(pi)
        }.build()
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            manager.notify(task.id, notification)
        }
    }
}