package ir.amirroid.todo.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownEditText(
    text: String,
    items: List<String>,
    readOnly: Boolean = true,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(),
    onSelected: (Int) -> Unit
) {
    val ins = remember { MutableInteractionSource() }
    var isExpanded by remember {
        mutableStateOf(false)
    }
    var size by remember {
        mutableStateOf(Size.Zero)
    }
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    Box(modifier = modifier) {
        OutlinedTextField(
            value = text,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged {
                    size = it.toSize()
                },
            readOnly = readOnly,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = isExpanded
                )
            },
            interactionSource = ins.apply {
                scope.launch {
                    interactions.collectLatest {
                        if (it is PressInteraction) {
                            isExpanded = isExpanded.not()
                        }
                    }
                }
            },
            enabled = enabled,
            colors = colors
        )
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier.width(with(density) { size.width.toDp() })
        ) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(text = {
                    Text(text = item)
                }, onClick = {
                    onSelected.invoke(index)
                    isExpanded = false
                })
            }
        }
    }
}