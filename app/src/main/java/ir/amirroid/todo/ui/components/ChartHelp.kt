package ir.amirroid.todo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ir.amirroid.todo.R


@Composable
fun HelpColorList() {
    Column(horizontalAlignment = Alignment.End) {
        HelpColor(
            text = stringResource(id = R.string.done),
            color = MaterialTheme.colorScheme.primary
        )
        HelpColor(
            text = stringResource(id = R.string.undone),
            color = MaterialTheme.colorScheme.surfaceContainerHighest
        )
        HelpColor(
            text = stringResource(id = R.string.doing),
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
fun HelpColor(text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = text)
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
    }
}