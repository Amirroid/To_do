package ir.amirroid.todo.ui.features.alarm_activity

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import ir.amirroid.todo.models.data.TaskInfo
import ir.amirroid.todo.ui.features.alarm_activity.ui.theme.ToDoTheme
import ir.amirroid.todo.ui.features.detail.DetailScreen
import ir.amirroid.todo.utils.Constants


@AndroidEntryPoint
class AlarmActivity : ComponentActivity() {
    private var task: TaskInfo? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    task = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(Constants.TASK, TaskInfo::class.java)
                    } else {
                        intent.getParcelableExtra(Constants.TASK)
                    }
                    if (task != null) {
                        DetailScreen(
                            navigation = null,
                            taskId = task!!.id,
                            initialEdit = false,
                            editableMode = false
                        )
                    }
                }
            }
        }
    }
}