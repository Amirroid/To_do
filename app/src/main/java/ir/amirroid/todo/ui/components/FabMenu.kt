package ir.amirroid.todo.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun BoxScope.FabMenu(
    onClick: (Int) -> Unit, icon: ImageVector, icons: List<Int>, fabVisible: Boolean
) {
    var visible by remember {
        mutableStateOf(false)
    }
    val rotate by animateFloatAsState(
        targetValue = if (visible) 45f else 0f, label = "rotate_fab_icon"
    )
    val size by animateFloatAsState(
        targetValue = if (visible) 1.5f else 1f, label = "rotate_fab_icon"
    )
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.4f))
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { visible = false })
                }
        )
    }
    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .navigationBarsPadding()
            .padding(bottom = 96.dp, end = 12.dp)
    ) {
        icons.forEachIndexed { index, i ->
            SmallFab(visible = visible, icon = i) {
                onClick.invoke(index)
                visible = false
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        AnimatedVisibility(
            visible = fabVisible,
            exit = scaleOut(),
            enter = scaleIn()
        ) {
            FloatingActionButton(onClick = {
                visible = visible.not()
            }) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .rotate(rotate)
                        .size(24.dp * size)
                )
            }
        }
    }
}

@Composable
fun SmallFab(visible: Boolean, icon: Int, onClick: () -> Unit) {
    AnimatedVisibility(visible = visible,
        enter = slideInVertically { 200 } + fadeIn(),
        exit = slideOutVertically { 200 } + fadeOut()) {
        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.background
        ) {
            Icon(painter = painterResource(id = icon), contentDescription = null)
        }
    }
}