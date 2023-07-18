package ir.amirroid.todo.ui.components

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ir.amirroid.todo.R
import ir.amirroid.todo.models.data.CategoryInfo
import ir.amirroid.todo.models.data.TaskInfo
import ir.amirroid.todo.utils.Constants
import ir.amirroid.todo.utils.formatDate
import ir.amirroid.todo.utils.formatTime
import ir.amirroid.todo.utils.toTime
import java.util.Calendar
import java.util.Date


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddTaskBottomSheet(
    categories: List<CategoryInfo>,
    onConfirm: (TaskInfo) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember {
        mutableStateOf("")
    }
    var categoryIndex by remember {
        mutableIntStateOf(0)
    }
    var isError by remember {
        mutableStateOf(false)
    }
    val focusRequester = FocusRequester()
    val keyboard = LocalSoftwareKeyboardController.current
    var isStar by remember {
        mutableStateOf(false)
    }
    var isDateDialog by remember {
        mutableStateOf(false)
    }
    var isEndDateDialog by remember {
        mutableStateOf(false)
    }
    var isStartTimePicker by remember {
        mutableStateOf(false)
    }
    var isTimePickerDialog by remember {
        mutableStateOf(false)
    }
    var startDate by remember {
        mutableLongStateOf(0L)
    }
    var endDate by remember {
        mutableLongStateOf(0L)
    }
    var startTime by remember {
        mutableLongStateOf(0L)
    }
    var endTime by remember {
        mutableLongStateOf(0L)
    }
    var alarmMode by remember {
        mutableIntStateOf(0)
    }
    val datePickerState = rememberDatePickerState()
    val endDatePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()
    val endTimePickerState = rememberTimePickerState()
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        try {
            focusRequester.requestFocus()
            keyboard?.show()
        } catch (_: Exception) {
        }
    }
    ModalBottomSheet(
        onDismissRequest = {
            onCancel.invoke()
        },
        modifier = Modifier.padding(horizontal = 12.dp),
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
                .padding(bottom = 36.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = stringResource(id = R.string.add_a_task), style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
            OutlinedTextField(value = title, onValueChange = {
                title = it
                if (it.isNotEmpty())
                    isError = false
            }, label = {
                Text(text = stringResource(id = R.string.title))
            }, modifier = Modifier
                .focusRequester(focusRequester)
                .padding(top = 12.dp)
                .fillMaxWidth(),
                isError = isError
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                DropdownEditText(
                    text = categories[categoryIndex].title,
                    items = categories.map { it.title },
                    onSelected = {
                        categoryIndex = it
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                IconButton(onClick = { isStar = isStar.not() }) {
                    if (isStar) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = "star",
                            tint = Color.Yellow
                        )
                    } else {
                        Icon(imageVector = Icons.Rounded.Star, contentDescription = "star")
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Button(onClick = { isDateDialog = true }, modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (startDate == 0L) stringResource(id = R.string.set_date) else startDate.formatDate()
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = { isEndDateDialog = true },
                    modifier = Modifier.weight(1f),
                    enabled = startDate != 0L
                ) {
                    Text(
                        text = if (endDate == 0L) stringResource(id = R.string.set_end_date) else endDate.formatDate()
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Button(
                    onClick = {
                        isStartTimePicker = true
                        isTimePickerDialog = true
                    },
                    modifier = Modifier.weight(1f),
                    enabled = startDate != 0L
                ) {
                    Text(
                        text = if (startTime == 0L) stringResource(id = R.string.set_start_time) else startTime.plus(
                            startDate
                        ).formatTime()
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = {
                        isTimePickerDialog = true
                        isStartTimePicker = false
                    },
                    modifier = Modifier.weight(1f),
                    enabled = endDate != 0L
                ) {
                    Text(
                        text = if (endTime == 0L) stringResource(id = R.string.set_end_time) else endTime.plus(
                            endDate
                        ).formatTime()
                    )
                }
            }
            Text(
                text = stringResource(id = R.string.alarm_mode),
                modifier = Modifier
                    .padding(top = 12.dp)
                    .alpha(0.5f)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Constants.alarmModes.forEach {
                    if (alarmMode == it.first) {
                        OutlinedIconButton(onClick = {}) {
                            Icon(
                                painter = painterResource(id = it.second),
                                contentDescription = null
                            )
                        }
                    } else {
                        IconButton(onClick = {
                            if (it.first == Constants.ALARM_NOT) {
                                endTime = 0L
                                startTime = 0L
                                startDate = 0L
                                endDate = 0L
                            }
                            alarmMode = it.first
                        }) {
                            Icon(
                                painter = painterResource(id = it.second),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 12.dp)
            ) {
                OutlinedButton(onClick = {
                    onCancel.invoke()
                }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    if (title.isEmpty()) {
                        isError = true
                    } else onConfirm.invoke(
                        TaskInfo(
                            title = title,
                            categoryId = categories[categoryIndex].id,
                            isDone = false,
                            startDate = startDate,
                            endDate = endDate,
                            startTime = startTime,
                            endTime = endTime,
                            isFavorite = isStar,
                            alarmMode = alarmMode
                        )
                    )
                }) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            }
        }
    }
    if (isDateDialog) {
        DatePickerDialog(
            onDismissRequest = { isDateDialog = false },
            confirmButton = {
                Button(onClick = {
                    val selectedDate = datePickerState.selectedDateMillis ?: 0L
                    val todaySelect =
                        selectedDate.formatDate() == System.currentTimeMillis().formatDate()
                    if (selectedDate > System.currentTimeMillis() || todaySelect) {
                        val calendar = Calendar.getInstance()
                        calendar.clear()
                        calendar.time = Date(selectedDate)
                        calendar.set(Calendar.HOUR_OF_DAY, 0)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.MILLISECOND, 0)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.HOUR, 0)
                        startDate = calendar.time.time
                        isDateDialog = false
                        alarmMode = Constants.ALARM_ONE_TIME
                    } else {
                        Toast.makeText(context, R.string.date_is_not_valid, Toast.LENGTH_SHORT)
                            .show()
                    }
                }) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    isDateDialog = false
                }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    if (isEndDateDialog) {
        DatePickerDialog(
            onDismissRequest = { isEndDateDialog = false },
            confirmButton = {
                Button(onClick = {
                    val selectedDate = endDatePickerState.selectedDateMillis ?: 0L
                    if (selectedDate >= (datePickerState.selectedDateMillis ?: 0L)) {
                        val calendar = Calendar.getInstance()
                        calendar.time = Date(selectedDate)
                        calendar.set(Calendar.HOUR_OF_DAY, 0)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.MILLISECOND, 0)
                        calendar.set(Calendar.SECOND, 0)
                        endDate = calendar.time.time
                        isEndDateDialog = false
                        alarmMode = Constants.ALARM_RANGE
                    } else {
                        Toast.makeText(context, R.string.date_is_not_valid, Toast.LENGTH_SHORT)
                            .show()
                    }
                }) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    isEndDateDialog = false
                }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
        ) {
            DatePicker(state = endDatePickerState)
        }
    }
    if (isTimePickerDialog) {
        DatePickerDialog(
            onDismissRequest = {
                isTimePickerDialog = false
            },
            confirmButton = {
                Button(onClick = {
                    if (isStartTimePicker) {
                        startTime = timePickerState.toTime()
                        isTimePickerDialog = false
                    } else {
                        if (startTime <= endTimePickerState.toTime()) {
                            endTime = endTimePickerState.toTime()
                            isTimePickerDialog = false
                        } else {
                            Toast.makeText(context, R.string.date_is_not_valid, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    isTimePickerDialog = false
                }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        ) {
            TimePicker(
                state = if (isStartTimePicker) timePickerState else endTimePickerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            )
        }
    }
}

