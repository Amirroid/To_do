package ir.amirroid.todo.ui.components

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import ir.amirroid.todo.utils.toDp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ListItemView(
    scope: CoroutineScope = rememberCoroutineScope(),
    context: Context = LocalContext.current,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val widthDp = configuration.screenWidthDp
    val widthView = widthDp * .95f
    val matrix = context.resources.displayMetrics
    val widthPx = matrix.widthPixels
    val widthViewPx = widthPx * .95f
    var offsetItem = 0f
    var offsetX = remember {
        Animatable(0f)
    }
    val density = LocalDensity.current
    val draggableState = rememberDraggableState {
        scope.launch {
            offsetX.snapTo(
                offsetX.value
                    .plus(it)
                    .coerceIn(offsetItem, offsetItem.plus(widthViewPx))
            )
        }
    }
    val space = 4.dp
    val initialOffset = (0.025f * widthPx).toInt()
    val deleterSize by animateDpAsState(
        when {
            widthPx.div(2) < offsetX.value -> {
                with(density) { (offsetX.value * (offsetX.value / widthViewPx)).toDp() } - space
            }

            else -> {
                with(density) { offsetX.value.div(2).toDp() } - space
            }
        }, label = ""
    )
    val editOffset = with(density) { deleterSize.toPx() + initialOffset }
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(84.dp)
        .draggable(
            draggableState,
            orientation = Orientation.Horizontal,
            onDragStopped = {
                when {
                    offsetX.value >= widthPx.div(2) -> {
                        offsetX.animateTo(
                            offsetItem + widthViewPx
                        )
                        onDelete.invoke()
                    }

                    offsetX.value >= widthPx.div(4) -> {
                        offsetX.animateTo(
                            widthPx.div(2f)
                        )
                    }

                    else -> {
                        offsetX.animateTo(
                            offsetItem
                        )
                    }
                }
            }
        )
        .then(modifier),
        contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier
                .offset {
                    IntOffset(initialOffset, 0)
                }
                .fillMaxHeight()
                .width(deleterSize)
                .align(Alignment.CenterStart),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = Color.Red
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        scope.launch {
                            offsetX.animateTo(
                                offsetItem + widthViewPx
                            )
                        }
                        onDelete.invoke()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
            }
        }
        Card(
            modifier = Modifier
                .offset {
                    IntOffset(editOffset.toInt(), 0)
                }
                .fillMaxHeight()
                .width(with(density) {
                    offsetX.value
                        .div(2)
                        .toDp()
                })
                .align(Alignment.CenterStart),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = Color.Gray
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        scope.launch {
                            offsetX.animateTo(0f)
                        }
                        onEdit.invoke()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
            }
        }
        Surface(
            modifier = Modifier
                .offset {
                    IntOffset(offsetX.value.toInt(), 0)
                }
                .fillMaxHeight()
                .width(widthView.dp),
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 2.dp
        ) {
            content.invoke()
        }
    }
}