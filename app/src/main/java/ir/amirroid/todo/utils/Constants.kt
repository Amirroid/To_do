package ir.amirroid.todo.utils

import ir.amirroid.todo.R

object Constants {
    const val PERMISSION = "permission"
    const val HOME = "home"
    const val CHART = "chart"
    const val DETAIL = "detail"
    const val ADD = "add"

    // db
    const val APP_DATABASE = "app_database"
    const val CATEGORY = "category"
    const val TASK = "task"


    // other
    const val FAVORITE = "favorite_app734677r839r3"

    // alarm modes
    const val ALARM_NOT = 0
    const val ALARM_ONE_TIME = 1
    const val ALARM_RANGE = 3
    val alarmModes = listOf(
        Pair(ALARM_NOT, R.drawable.round_do_not_disturb_24),
        Pair(ALARM_ONE_TIME, R.drawable.outline_looks_one_24),
        Pair(ALARM_RANGE, R.drawable.round_repeat_24),
    )

    // notification
    const val CHANNEL_ID = "to_do"

    // extras
    const val DETAIL_PARAM = "detail_param"
    const val DETAIL_PARAM_EDIT = "detail_param_edit"
    const val TYPE = "type"
    const val START = "start"
    const val END = "end"

}