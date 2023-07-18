package ir.amirroid.todo.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.amirroid.todo.models.data.TaskInfo
import ir.amirroid.todo.utils.formatDate
import ir.amirroid.todo.utils.formatDateTime
import ir.amirroid.todo.utils.getEndDate
import ir.amirroid.todo.utils.getStartDate

@Composable
fun TaskView(task: TaskInfo, onStatusChanged: (Boolean) -> Unit, onClick: () -> Unit) {
    var width by remember {
        mutableIntStateOf(0)
    }
    val lineColor = MaterialTheme.colorScheme.onBackground
    val lineWidth by animateFloatAsState(
        targetValue = if (task.isDone) width.toFloat() else 0f,
        label = ""
    )
    Row(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onClick.invoke() }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = task.title,
                style = TextStyle(
                    fontSize = 18.sp,
                ),
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .onSizeChanged {
                        width = it.width
                    }
                    .drawWithContent {
                        drawContent()
                        drawLine(
                            lineColor,
                            Offset(
                                0f,
                                size.height
                                    .div(2)
                                    .minus(2)
                            ),
                            Offset(
                                lineWidth,
                                size.height
                                    .div(2)
                                    .minus(2)
                            ),
                            strokeWidth = 4f
                        )
                    }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                if (task.getStartDate() != 0L) {
                    Text(
                        text = task.getStartDate().formatDateTime(),
                        modifier = Modifier.alpha(0.6f),
                        fontSize = 14.sp
                    )
                }
                if (task.getEndDate() != 0L) {
                    Text(
                        text = task.getEndDate().formatDateTime(),
                        modifier = Modifier.alpha(0.6f),
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
        RadioButton(selected = task.isDone, onClick = { onStatusChanged.invoke(task.isDone.not()) })
    }
}