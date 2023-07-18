package ir.amirroid.todo.utils

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import ir.amirroid.todo.models.data.TaskInfo

fun getEmptyTask() = TaskInfo(
    0,
    0,
    "",
    false,
    0L,
    0L,
    0L,
    0,
    0,
    false,
    0
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun getDetailModeColorTextField() = TextFieldDefaults.outlinedTextFieldColors(
    disabledTextColor = MaterialTheme.colorScheme.onBackground
)