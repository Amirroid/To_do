package ir.amirroid.todo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.amirroid.todo.models.data.CategoryInfo
import ir.amirroid.todo.models.helpers.PermissionHelper
import ir.amirroid.todo.models.repository.CategoryRepository
import ir.amirroid.todo.ui.features.chart.ChartScreen
import ir.amirroid.todo.ui.features.detail.DetailScreen
import ir.amirroid.todo.ui.features.home.HomeScreen
import ir.amirroid.todo.ui.theme.ToDoTheme
import ir.amirroid.todo.utils.AppPages
import ir.amirroid.todo.utils.Constants
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var categoryRepository: CategoryRepository

    @Inject
    lateinit var permissionHelper: PermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            categoryRepository.insertCategoryIfNotExist(
                CategoryInfo(
                    id = 1, title = getString(R.string.my_works)
                )
            )
        }
        setContent {
            ToDoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding()
                    ) {
                        MainScreen(permissionHelper)
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(permissionHelper: PermissionHelper) {
    val navController = rememberNavController()
    var currentPage: AppPages by remember {
        mutableStateOf(AppPages.HomeScreen)
    }
    val pages = listOf(
        AppPages.HomeScreen,
        AppPages.ChartScreen,
    )
    val currentPageRoute by navController.currentBackStackEntryAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = if (permissionHelper.checkPermission()) AppPages.HomeScreen.route else AppPages.PermissionScreen.route,
        ) {
            composable(AppPages.PermissionScreen.route) {
                PermissionScreen(navController, permissionHelper)
            }
            composable(AppPages.HomeScreen.route) {
                HomeScreen(navController)
            }
            composable(AppPages.ChartScreen.route) {
                ChartScreen()
            }
            composable(
                AppPages.DetailScreen.route + "?${Constants.DETAIL_PARAM}={${Constants.DETAIL_PARAM}}&${Constants.DETAIL_PARAM_EDIT}={${Constants.DETAIL_PARAM_EDIT}}",
                arguments = listOf(navArgument(Constants.DETAIL_PARAM) {
                    type = NavType.IntType
                }, navArgument(Constants.DETAIL_PARAM_EDIT) {
                    type = NavType.BoolType
                    defaultValue = false
                })
            ) {
                val id = it.arguments?.getInt(Constants.DETAIL_PARAM)
                val editMode = it.arguments?.getBoolean(Constants.DETAIL_PARAM_EDIT) ?: false
                DetailScreen(navController, id!!, editMode)
            }
        }
        AnimatedVisibility(
            visible = currentPageRoute?.destination?.route == AppPages.HomeScreen.route
                    || currentPageRoute?.destination?.route == AppPages.ChartScreen.route,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically { 200 } + fadeIn(),
            exit = slideOutVertically { 200 } + fadeOut()
        ) {
            MainBottomNavigation(pages = pages, currentPage = currentPage) {
                navController.navigate(it.route) {
                    popUpTo(currentPage.route) {
                        inclusive = true
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
                currentPage = it
            }
        }
    }
}

@Composable
fun MainBottomNavigation(
    pages: List<AppPages>,
    currentPage: AppPages,
    onNavigate: (AppPages) -> Unit,
) {
    NavigationBar {
        for (page in pages) {
            NavigationBarItem(selected = page == currentPage,
                onClick = { onNavigate.invoke(page) },
                icon = {
                    Icon(painter = painterResource(id = page.icon!!), contentDescription = null)
                }, label = { Text(text = stringResource(id = page.resId)) })
        }
    }
}

@Composable
fun PermissionScreen(navController: NavHostController, permissionHelper: PermissionHelper) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        if (permissionHelper.checkPermission()) {
            navController.navigate(AppPages.HomeScreen.route)
        }
        onDispose { }
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) {
        if (it) {
            if (permissionHelper.checkAlarm()) {
                navController.navigate(AppPages.HomeScreen.route)
            } else {
                openAlarmSettings(context)
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = {
            if (permissionHelper.checkNotification()) {
                if (permissionHelper.checkAlarm()) {
                    navController.navigate(AppPages.HomeScreen.route)
                } else {
                    openAlarmSettings(context)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    launcher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                }
            }
        }) {
            Text(text = stringResource(id = R.string.get_permission))
        }
    }
}

fun openAlarmSettings(context: Context) {
    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
    context.startActivity(intent)
}
