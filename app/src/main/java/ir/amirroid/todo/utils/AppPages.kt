package ir.amirroid.todo.utils

import androidx.annotation.StringRes
import ir.amirroid.todo.R

sealed class AppPages(val route: String, val icon: Int? = null, @StringRes val resId: Int = 0) {
    object PermissionScreen : AppPages(Constants.PERMISSION)
    object HomeScreen : AppPages(Constants.HOME, R.drawable.round_home_24, R.string.home)
    object ChartScreen : AppPages(Constants.CHART, R.drawable.round_bar_chart_24, R.string.chart)
    object DetailScreen : AppPages(Constants.DETAIL)
//    object AddScreen : AppPages(Constants.ADD)
}