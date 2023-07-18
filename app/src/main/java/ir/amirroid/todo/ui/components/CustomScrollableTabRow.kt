package ir.amirroid.todo.ui.components

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.log

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomScrollableTabRow(
    state: PagerState,
    tabs: List<String>,
    modifier: Modifier = Modifier,
    scope: CoroutineScope,
    endContent: @Composable () -> Unit,
    onPageSelected: (Int) -> Unit
) {
    val textMeasurable = rememberTextMeasurer()

    val context = LocalContext.current
    val matrix = context.resources.displayMetrics
    val density = LocalDensity.current
    val paddingText = with(density) { 12.dp.toPx() }
    val scrollState = rememberScrollState()

    val width = matrix.widthPixels
    val tabWidth = width / 4f
    val fraction = state.currentPageOffsetFraction
    val textWidth by animateFloatAsState(
        targetValue = (textMeasurable.measure(tabs[state.targetPage]).size.width + paddingText).coerceAtMost(
            tabWidth
        ),
        label = "text_size",
    )
    val tabWidthDp = with(density) { tabWidth.toDp() }

    val offsetX =
        ((state.currentPage + fraction) * tabWidth + (tabWidth - textWidth) / 2).plus(-scrollState.value)


    LaunchedEffect(key1 = state.currentPage){
        scope.launch {
            scrollState.animateScrollTo(
                (tabWidth * state.currentPage).toInt()
            )
        }
    }

    Box(modifier = Modifier.wrapContentSize().then(modifier), contentAlignment = Alignment.BottomStart) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .horizontalScroll(scrollState)
                .height(52.dp)
        ) {
            tabs.forEachIndexed { index, text ->
                val selected = state.currentPage == index
                val color by animateColorAsState(
                    targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                    label = "color_tab"
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(tabWidthDp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable {
                            onPageSelected.invoke(index)
                            scope.launch {
                                scrollState.animateScrollTo(
                                    (tabWidth * index).toInt()
                                )
                            }
                        }, contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = text,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = color,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            endContent.invoke()
        }
        Box(modifier = Modifier
            .offset {
                IntOffset(offsetX.toInt(), 0)
            }
            .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
            .background(MaterialTheme.colorScheme.primary)
            .width(with(density) { textWidth.toDp() })
            .height(3.dp)
        )
    }
}