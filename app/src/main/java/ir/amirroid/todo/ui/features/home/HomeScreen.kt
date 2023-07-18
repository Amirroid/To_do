package ir.amirroid.todo.ui.features.home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.navigation.animation.AnimatedComposeNavigator
import ir.amirroid.todo.R
import ir.amirroid.todo.models.data.TaskInfo
import ir.amirroid.todo.ui.components.AddTaskBottomSheet
import ir.amirroid.todo.ui.components.BottomBarSpacer
import ir.amirroid.todo.ui.components.FabMenu
import ir.amirroid.todo.ui.components.ListItemView
import ir.amirroid.todo.ui.components.TaskView
import ir.amirroid.todo.utils.AppPages
import ir.amirroid.todo.utils.Constants
import ir.amirroid.todo.utils.addIfNotExist
import ir.amirroid.todo.utils.formatDate
import ir.amirroid.todo.utils.formatDateTime
import ir.amirroid.todo.utils.formatTime
import ir.amirroid.todo.utils.getOrDefaultValue
import ir.amirroid.todo.utils.longClickWithInteraction
import ir.amirroid.todo.utils.zeroBottom
import ir.amirroid.todo.viewmodels.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint(
    "UnrememberedMutableState", "UnusedCrossfadeTargetStateParameter",
    "UnusedContentLambdaTargetStateParameter"
)
@OptIn(
    ExperimentalFoundationApi::class, ExperimentalAnimationApi::class,
    ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class
)
@Composable
fun HomeScreen(
    navigation: NavController
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val viewModel: HomeViewModel = hiltViewModel()
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val changes by viewModel.changes.collectAsStateWithLifecycle()
    val removableMode by viewModel.removableMode.collectAsStateWithLifecycle()
    val favoriteMode by viewModel.favoriteMode.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState {
        categories.size.plus(1)
    }
    var searchActive by remember {
        mutableStateOf(false)
    }
    var isAddTaskBottomSheet by remember {
        mutableStateOf(false)
    }
    val keyboard = LocalSoftwareKeyboardController.current
    var isAddCategoryBottomSheet by remember {
        mutableStateOf(false)
    }
    val fabIcons = listOf(
        R.drawable.round_note_add_24,
        R.drawable.baseline_category_24,
    )
    val deletedList by viewModel.deleted.collectAsStateWithLifecycle()
    val showedList = remember {
        mutableStateListOf<TaskInfo>()
    }
    val spaceSearch by animateDpAsState(
        targetValue = if (searchActive) 0.dp else 12.dp,
        label = "space"
    )
    var firstLaunch by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = categories) {
        if (firstLaunch) {
            pagerState.animateScrollToPage(categories.size)
            viewModel.favoriteMode.value = false
        }
        firstLaunch = false
    }
    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(spaceSearch))
            SearchBar(
                query = searchText,
                onQueryChange = {
                    viewModel.searchTasks(it)
                },
                onSearch = {},
                active = searchActive,
                onActiveChange = {
                    viewModel.searchTasks("")
                    searchActive = it
                },
                placeholder = {
                    Text(text = stringResource(id = R.string.search))
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                leadingIcon = {
                    if (searchActive) {
                        IconButton(onClick = {
                            searchActive = false
                            viewModel.searchTasks("")
                        }) {
                            Icon(imageVector = Icons.Rounded.Close, contentDescription = "close")
                        }
                    } else {
                        Icon(imageVector = Icons.Rounded.Search, contentDescription = "search")
                    }
                },
            ) {
                val search by remember(changes) {
                    derivedStateOf {
                        viewModel.searchTasks.value
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    search.sortedBy {
                        it.dateCreated
                    }.reversed().groupBy { it.dateCreated.formatDate() }
                        .forEach { (header, tasks) ->
                            stickyHeader {
                                val text =
                                    if (header == System.currentTimeMillis().formatDate()
                                    ) context.getString(
                                        R.string.to_day
                                    ) else header
                                Text(
                                    text = text,
                                    modifier = Modifier
                                        .padding(top = 12.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                            items(tasks.size, key = { tasks[it].id }) {
                                val task = tasks[it]
                                AnimatedVisibility(
                                    visible = deletedList.contains(task).not(),
                                    exit = shrinkVertically(tween(300)),
                                    enter = slideInVertically(tween(300)),
                                    initiallyVisible = false
                                ) {
                                    showedList.addIfNotExist(task)
                                    ListItemView(onDelete = {
                                        viewModel.deleteTask(task)
                                    }, onEdit = {
                                        navigation.navigate(
                                            AppPages.DetailScreen.route + "?${Constants.DETAIL_PARAM}=${task.id}&${Constants.DETAIL_PARAM_EDIT}=true"
                                        )
                                    }, modifier = Modifier.animateItemPlacement()) {
                                        TaskView(task = task, { done ->
                                            val newTask = task.copy(isDone = done)
                                            viewModel.editTask(newTask, false)
                                        }) {
                                            navigation.navigate(AppPages.DetailScreen.route + "?${Constants.DETAIL_PARAM}=${task.id}")
                                        }
                                    }
                                }
                            }
                        }
                    item { BottomBarSpacer() }
                }
            }
            Box {
                ScrollableTabRow(
                    selectedTabIndex = if (favoriteMode) 0 else pagerState.currentPage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    edgePadding = 12.dp,
                    divider = {},
                    indicator = {
                        TabRowDefaults.PrimaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(it[pagerState.currentPage]),
                            shape = MaterialTheme.shapes.medium.zeroBottom()
                        )
                    }
                ) {
                    Tab(selected = false, onClick = {
                        viewModel.favoriteMode.value = true
                        scope.launch { pagerState.animateScrollToPage(0) }
                    }, icon = {
                        Icon(imageVector = Icons.Rounded.Star, contentDescription = null)
                    },
                        modifier = Modifier.clip(
                            MaterialTheme.shapes.medium.zeroBottom()
                        )
                    )
                    categories.forEachIndexed { index, category ->
                        val interaction = remember {
                            MutableInteractionSource()
                        }
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(),
                            exit = fadeOut(),
                            initiallyVisible = false
                        ) {
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    scope.launch {
                                        viewModel.favoriteMode.value = false
                                        pagerState.animateScrollToPage(index.plus(1))
                                    }
                                },
                                text = {
                                    Text(text = category.title)
                                },
                                interactionSource = interaction,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(
                                        MaterialTheme.shapes.medium.zeroBottom()
                                    )
                                    .longClickWithInteraction(interaction.interactions) {
                                        if (category.id != 1) {
                                            viewModel.removableMode.value = category.id
                                        }
                                    }
                            )

                            DropdownMenu(
                                expanded = removableMode == category.id,
                                onDismissRequest = { viewModel.removableMode.value = null },
                            ) {
                                DropdownMenuItem(text = {
                                    Text(text = stringResource(id = R.string.delete))
                                }, onClick = {
                                    scope.launch {
                                        viewModel.removableMode.value = null
                                        val categoryIndex = categories.indexOf(category)
                                        pagerState.animateScrollToPage(
                                            categoryIndex,
                                        )
                                        viewModel.deleteCategory(category)
                                    }
                                })
                            }
                        }
                    }
                    Tab(
                        selected = false,
                        onClick = {
                            isAddCategoryBottomSheet = true
                        },
                        icon = {
                            Icon(imageVector = Icons.Rounded.Add, contentDescription = "add")
                        },
                        text = {
                            Text(text = stringResource(id = R.string.add))
                        },
                        modifier = Modifier.clip(
                            MaterialTheme.shapes.medium.zeroBottom()
                        )
                    )
                }
            }
            Divider()
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize(),
            ) { page ->
                val pageTasks by remember(changes) {
                    derivedStateOf {
                        try {
                            tasks.getOrDefaultValue(
                                if (page == 0) Constants.FAVORITE else categories[page.minus(1)].id.toString(),
                                emptyList()
                            )
                        } catch (_: Exception) {
                            emptyList()
                        }
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    pageTasks.sortedBy {
                        it.dateCreated
                    }.reversed().groupBy { it.dateCreated.formatDate() }
                        .forEach { (header, tasks) ->
                            stickyHeader {
                                val text =
                                    if (header == System.currentTimeMillis().formatDate()
                                    ) context.getString(
                                        R.string.to_day
                                    ) else header
                                Text(
                                    text = text,
                                    modifier = Modifier
                                        .padding(top = 12.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                            items(tasks.size, key = { tasks[it].id }) {
                                val task = tasks[it]
                                AnimatedVisibility(
                                    visible = deletedList.contains(task).not(),
                                    exit = shrinkVertically(tween(300)),
                                    enter = slideInVertically(tween(300)),
                                    initiallyVisible = showedList.contains(task)
                                ) {
                                    showedList.addIfNotExist(task)
                                    ListItemView(onDelete = {
                                        viewModel.deleteTask(task)
                                    }, onEdit = {
                                        navigation.navigate(
                                            AppPages.DetailScreen.route + "?${Constants.DETAIL_PARAM}=${task.id}&${Constants.DETAIL_PARAM_EDIT}=true"
                                        )
                                    }, modifier = Modifier.animateItemPlacement()) {
                                        TaskView(task = task, { done ->
                                            val newTask = task.copy(isDone = done)
                                            viewModel.editTask(newTask, false)
                                        }) {
                                            navigation.navigate(AppPages.DetailScreen.route + "?${Constants.DETAIL_PARAM}=${task.id}")
                                        }
                                    }
                                }
                            }
                        }
                    item {
                        BottomBarSpacer()
                    }
                }
            }
        }
        FabMenu(onClick = {
            if (it == 0) {
                isAddTaskBottomSheet = true
            } else {
                isAddCategoryBottomSheet = true
            }
        }, icon = Icons.Rounded.Add, icons = fabIcons, fabVisible = searchActive.not())
    }
    if (isAddCategoryBottomSheet) {
        keyboard?.hide()
        AddCategoryBottomSheet(onConfirm = {
            viewModel.addCategory(it)
            isAddCategoryBottomSheet = false
        }) {
            isAddCategoryBottomSheet = false
        }
    }
    if (isAddTaskBottomSheet) {
        AddTaskBottomSheet(onConfirm = {
            viewModel.addTask(it)
            isAddTaskBottomSheet = false
        }, categories = categories) {
            isAddTaskBottomSheet = false
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AddCategoryBottomSheet(
    onConfirm: (text: String) -> Unit,
    onCancel: () -> Unit,
) {
    var text by remember {
        mutableStateOf("")
    }
    var isError by remember {
        mutableStateOf(false)
    }
    val keyboard = LocalSoftwareKeyboardController.current
    val focusRequest = FocusRequester()
    LaunchedEffect(key1 = Unit) {
        try {
            focusRequest.requestFocus()
            keyboard?.show()
        } catch (_: Exception) {
        }
    }
    ModalBottomSheet(onDismissRequest = {
        keyboard?.hide()
        onCancel.invoke()
    }, modifier = Modifier.padding(horizontal = 12.dp)) {
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = stringResource(id = R.string.add_a_category), style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
            OutlinedTextField(value = text, onValueChange = {
                text = it
                if (it.isNotEmpty())
                    isError = false
            }, label = {
                Text(text = stringResource(id = R.string.title))
            }, modifier = Modifier
                .padding(vertical = 12.dp)
                .focusRequester(focusRequest)
                .fillMaxWidth(),
                isError = isError
            )
            Row(modifier = Modifier.align(Alignment.End)) {
                OutlinedButton(onClick = {
                    keyboard?.hide()
                    onCancel.invoke()
                }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    if (text.isEmpty()) {
                        isError = true
                    } else onConfirm.invoke(text)
                }) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            }
        }
    }
}

