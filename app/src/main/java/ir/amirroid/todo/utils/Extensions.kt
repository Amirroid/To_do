package ir.amirroid.todo.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import ir.amirroid.todo.models.data.TaskInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
import java.text.SimpleDateFormat

//val WindowInsets.Companion.isImeVisible: Boolean
//    @Composable
//    get() {
//        val density = LocalDensity.current
//        val ime = this.ime
//        return remember {
//            derivedStateOf {
//                ime.getBottom(density) > 0
//            }
//        }.value
//    }

fun <K, V> Map<K, V>.getOrDefaultValue(
    key: K,
    default: V
): V {
    return (try {
        get(key) ?: default
    } catch (_: Exception) {
        default
    })
}


fun Modifier.longClickWithInteraction(
    interaction: Flow<Interaction>,
    onLongClick: (Offset) -> Unit
) = composed {
    var timeTouch = 0L
    LaunchedEffect(key1 = interaction) {
        interaction.collectLatest {
            when (it) {
                is PressInteraction.Press -> {
                    timeTouch = System.currentTimeMillis()
                }

                is PressInteraction.Release -> {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - timeTouch >= 600) {
                        onLongClick.invoke(it.press.pressPosition)
                    }
                }

                is PressInteraction.Cancel -> {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - timeTouch >= 1000) {
                        onLongClick.invoke(it.press.pressPosition)
                    }
                }
            }
        }
    }
    Modifier
}

fun Long.formatDate() = SimpleDateFormat("yyyy/MM/dd").format(this).toString()

fun Long.formatDateTime() = SimpleDateFormat("yyyy/MM/dd HH:mm").format(this).toString()

@SuppressLint("SimpleDateFormat")
fun Long.formatDateWithFormatLine() =
    SimpleDateFormat("yy/MM/dd").format(this).toString().lineFormat("/")

@SuppressLint("SimpleDateFormat")
fun Long.formatDateTimeWithFormatLine() =
    SimpleDateFormat("yyyy/MM/dd/HH/mm").format(this).toString().lineFormat("/")

fun Long.formatTime() = SimpleDateFormat("HH:mm").format(this).toString()


@OptIn(ExperimentalMaterial3Api::class)
fun TimePickerState.toTime(): Long {
    return (hour * 1000 * 3600) + (minute * 60 * 1000L)
}


fun <T> SnapshotStateList<T>.addIfNotExist(obj: T) {
    if (contains(obj).not()) {
        add(obj)
    }
}


val TaskInfo.isAlarm: Boolean
    get() = alarmMode != Constants.ALARM_NOT


fun TaskInfo.getStartDate() = startDate.plus(startTime)
fun TaskInfo.getEndDate() = endDate.plus(endTime)


fun <T> List<T>.getIfExist(element: T): Int? {
    return try {
        if (contains(element))
            indexOf(element)
        else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Int.toDp(context: Context) = context.resources.displayMetrics.density * this


fun String.lineFormat(regex: String): String {
    val split = split(regex)
    var newText = ""
    split.forEach {
        newText += it + "\n"
    }
    return newText
}

fun CornerBasedShape.zeroBottom() =
    copy(bottomEnd = CornerSize(0.dp), bottomStart = CornerSize(0.dp))


fun TaskInfo.isRangeDate() =
    getStartDate() <= System.currentTimeMillis() && getEndDate() >= System.currentTimeMillis()


fun TaskInfo.getType() = when {
    isDone -> 2
    isRangeDate() -> 3
    else -> 1
}


@Composable
fun Int.getColor() = when (this) {
    3 -> colorScheme.tertiary
    2 -> colorScheme.primary
    else -> colorScheme.surfaceContainerHighest
}


fun Offset.toIntOffset() = IntOffset(x.toInt(), y.toInt())