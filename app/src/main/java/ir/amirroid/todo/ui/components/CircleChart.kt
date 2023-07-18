package ir.amirroid.todo.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import ir.amirroid.todo.utils.getColor

@Composable
fun CircleChart(
    data: List<Int>,
    modifier: Modifier = Modifier
) {
    val sweepAnimation = remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 = Unit) {
        sweepAnimation.animateTo(360f, tween(1500))
    }
    val groupedData = data.groupBy { it.getColor() }
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .drawWithContent {
                var indexSweep = 2f
                val dataSize = data.size
                groupedData.forEach { (color, values) ->
                    val dataSweep = (values.size / dataSize.toFloat()) * 356
                    drawArc(
                        color = color,
                        indexSweep,
                        sweepAnimation.value
                            .minus(indexSweep)
                            .coerceIn(0f, dataSweep),
                        false,
                        size = size,
                        style = Stroke(
                            54f,
                        )
                    )
                    indexSweep += dataSweep + 2
                }
                drawContent()
            },
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(horizontalAlignment = Alignment.End) {
                groupedData.forEach { (color, values) ->
                    HelpColor(text = values.size.toString(), color = color)
                }
            }
        }
    }
}