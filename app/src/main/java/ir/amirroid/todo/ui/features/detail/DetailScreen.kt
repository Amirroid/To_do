package ir.amirroid.todo.ui.features.detail

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ir.amirroid.todo.R
import ir.amirroid.todo.ui.components.BottomBarSpacer
import ir.amirroid.todo.ui.components.DropdownEditText
import ir.amirroid.todo.utils.Constants
import ir.amirroid.todo.utils.formatDate
import ir.amirroid.todo.utils.formatTime
import ir.amirroid.todo.utils.getDetailModeColorTextField
import ir.amirroid.todo.utils.toTime
import ir.amirroid.todo.viewmodels.DetailViewModel
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navigation: NavController?,
    taskId: Int,
    initialEdit: Boolean,
    editableMode: Boolean = true
) {
    var isError by remember {
        mutableStateOf(false)
    }
    val viewModel: DetailViewModel = hiltViewModel()
    val datePickerState = rememberDatePickerState()
    val endDatePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()
    val endTimePickerState = rememberTimePickerState()
    val context = LocalContext.current
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    val title = viewModel.title

    val isStar = viewModel.isStar

    val isDateDialog = viewModel.isDateDialog

    val isEndDateDialog = viewModel.isEndDateDialog

    val isStartTimePicker = viewModel.isStartTimePicker

    val isTimePickerDialog = viewModel.isTimePickerDialog

    val startDate = viewModel.startDate

    val endDate = viewModel.endDate

    val startTime = viewModel.startTime

    val endTime = viewModel.endTime

    val alarmMode = viewModel.alarmMode

    val categoryIndex = viewModel.categoryIndex

    val editMode = viewModel.editMode

    val done = viewModel.done

    val activity = LocalContext.current as Activity

    LaunchedEffect(key1 = startDate, key2 = endDate) {
        datePickerState.selectedDateMillis = startDate
        endDatePickerState.selectedDateMillis = endDate
    }

    BackHandler {
        if (editMode) {
            viewModel.editMode = false
            viewModel.cancelEdit()
        } else {
            if (navigation == null) {
                activity.finish()
            } else {
                navigation.popBackStack()
            }
        }
    }
    DisposableEffect(key1 = Unit) {
        viewModel.loadTask(taskId)
        onDispose { }
    }
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(top = 12.dp)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = {
                    if (editMode) {
                        viewModel.editMode = false
                        viewModel.cancelEdit()
                    } else {
                        if (navigation != null) {
                            navigation.popBackStack()
                        } else {
                            activity.finish()
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowLeft,
                        contentDescription = "back"
                    )
                }
            },
            title = {
                Text(text = stringResource(id = R.string.detail))
            },
            actions = {
                if (editMode.not() && editableMode) {
                    IconButton(onClick = { viewModel.editMode = true }) {
                        Icon(imageVector = Icons.Rounded.Edit, contentDescription = null)
                    }
                }
            },
        )
        OutlinedTextField(value = title, onValueChange = {
            viewModel.title = it
            if (it.isNotEmpty())
                isError = false
        }, label = {
            Text(text = stringResource(id = R.string.title))
        }, modifier = Modifier
            .padding(top = 12.dp)
            .padding(horizontal = 12.dp)
            .fillMaxWidth(),
            isError = isError,
            enabled = editMode,
            colors = getDetailModeColorTextField()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            if (categories.isNotEmpty()) {
                DropdownEditText(
                    text = categories[categoryIndex].title,
                    items = categories.map { it.title },
                    onSelected = {
                        viewModel.categoryIndex = it
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    enabled = editMode,
                    colors = getDetailModeColorTextField()
                )
            }
            IconButton(onClick = { viewModel.isStar = isStar.not() }, enabled = editMode) {
                if (isStar) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "star",
                        tint = Color.Yellow
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "star",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(top = 12.dp)
        ) {
            Button(
                onClick = { viewModel.isDateDialog = true },
                modifier = Modifier.weight(1f),
                enabled = editMode
            ) {
                Text(
                    text = if (startDate == 0L) stringResource(id = R.string.set_date) else startDate.formatDate(),
                    color = if (editMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = { viewModel.isEndDateDialog = true },
                modifier = Modifier.weight(1f),
                enabled = startDate != 0L && editMode
            ) {
                Text(
                    text = if (endDate == 0L) stringResource(id = R.string.set_end_date) else endDate.formatDate(),
                    color = if (startDate != 0L && editMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(top = 12.dp)
        ) {
            Button(
                onClick = {
                    viewModel.isStartTimePicker = true
                    viewModel.isTimePickerDialog = true
                },
                modifier = Modifier.weight(1f),
                enabled = startDate != 0L && editMode,
            ) {
                Text(
                    text = if (startTime == 0L) stringResource(id = R.string.set_start_time) else startTime.plus(
                        startDate
                    ).formatTime(),
                    color = if (startDate != 0L && editMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = {
                    viewModel.isTimePickerDialog = true
                    viewModel.isStartTimePicker = false
                },
                modifier = Modifier.weight(1f),
                enabled = endDate != 0L && editMode
            ) {
                Text(
                    text = if (endTime == 0L) stringResource(id = R.string.set_end_time) else endTime.plus(
                        endDate
                    ).formatTime(),
                    color = if (endDate != 0L && editMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                )
            }
        }
        Text(
            text = stringResource(id = R.string.alarm_mode),
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(top = 12.dp)
                .alpha(0.5f)
        )
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Constants.alarmModes.forEach {
                if (alarmMode == it.first) {
                    OutlinedIconButton(
                        onClick = {},
                        enabled = editMode
                    ) {
                        Icon(
                            painter = painterResource(id = it.second),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            if (it.first == Constants.ALARM_NOT) {
                                viewModel.endTime = 0L
                                viewModel.startTime = 0L
                                viewModel.startDate = 0L
                                viewModel.endDate = 0L
                            }
                            viewModel.alarmMode = it.first
                        },
                        enabled = editMode
                    ) {
                        Icon(
                            painter = painterResource(id = it.second),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
        ListItem(headlineContent = {
            Text(text = stringResource(id = R.string.done))
        },
            trailingContent = {
                Switch(
                    checked = done, onCheckedChange = null,
                    enabled = editMode
                )
            }, modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .then(
                    if (editMode) {
                        Modifier.toggleable(done) {
                            viewModel.done = it
                        }
                    } else Modifier
                )
        )
        Spacer(modifier = Modifier.height(12.dp))
        AnimatedVisibility(visible = editMode) {
            Button(
                onClick = {
                    viewModel.editTask()
                    viewModel.editMode = false
                }, modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.edit))
            }
        }
        BottomBarSpacer()
    }
    if (isDateDialog) {
        DatePickerDialog(
            onDismissRequest = { viewModel.isDateDialog = false },
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
                        viewModel.startDate = calendar.time.time
                        viewModel.isDateDialog = false
                        viewModel.alarmMode = Constants.ALARM_ONE_TIME
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
                    viewModel.isDateDialog = false
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
            onDismissRequest = { viewModel.isEndDateDialog = false },
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
                        viewModel.endDate = calendar.time.time
                        viewModel.isEndDateDialog = false
                        viewModel.alarmMode = Constants.ALARM_RANGE
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
                    viewModel.isEndDateDialog = false
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
                viewModel.isTimePickerDialog = false
            },
            confirmButton = {
                Button(onClick = {
                    if (isStartTimePicker) {
                        viewModel.startTime = timePickerState.toTime()
                        viewModel.isTimePickerDialog = false
                    } else {
                        if (startTime <= endTimePickerState.toTime()) {
                            viewModel.endTime = endTimePickerState.toTime()
                            viewModel.isTimePickerDialog = false
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
                    viewModel.isTimePickerDialog = false
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