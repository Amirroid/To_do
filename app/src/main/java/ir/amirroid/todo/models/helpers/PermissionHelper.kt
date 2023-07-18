package ir.amirroid.todo.models.helpers

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PermissionHelper @Inject constructor(
    @ApplicationContext val context: Context
) {
    fun checkPermission() = checkAlarm() && checkNotification()

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun checkAlarm() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }

    fun checkNotification() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}