package ir.amirroid.todo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import ir.amirroid.todo.models.data.TaskInfo
import ir.amirroid.todo.utils.formatDate
import ir.amirroid.todo.utils.formatDateTime
import ir.amirroid.todo.utils.formatDateTimeWithFormatLine
import ir.amirroid.todo.utils.formatDateWithFormatLine
import ir.amirroid.todo.utils.getColor
import ir.amirroid.todo.utils.getType

@Composable
fun LineChartView(
    data: List<TaskInfo>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val groupedData = data.reversed().groupBy {
        it.dateCreated.formatDate()
    }
    val measurable = rememberTextMeasurer()
    val textColor = MaterialTheme.colorScheme.onBackground
    val density = LocalDensity.current
//    var size by remember {
//        mutableStateOf(Size.Zero)
//    }
    LazyRow(modifier = modifier) {
        groupedData.keys.forEachIndexed { _, key ->
            val tasks = groupedData[key]
            item {
//                val height = size.height
                val heightDp = 140.dp
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .width(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    if (tasks != null) {
                        val tasksGrouped = tasks.groupBy {
                            it.getType()
                        }
                        tasksGrouped.forEach { (type, values) ->
                            val height = heightDp.div(tasks.size) * values.size
                            Box(
                                modifier = Modifier
                                    .height(height)
                                    .fillMaxWidth()
                                    .clip(CircleShape)
                                    .background(
                                        type.getColor()
                                    )
                            )
                        }
                        Text(
                            text = tasks.first().dateCreated.formatDateWithFormatLine()
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }
}