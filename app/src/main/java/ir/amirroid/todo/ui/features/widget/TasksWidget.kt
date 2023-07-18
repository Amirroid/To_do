package ir.amirroid.todo.ui.features.widget

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.RadioButton
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import dagger.hilt.android.AndroidEntryPoint
import ir.amirroid.todo.models.repository.TaskRepository
import kotlinx.coroutines.launch
import javax.inject.Inject


@Composable
fun TasksWidget(repository: TaskRepository, context: Context, id: GlanceId) {
    var tasks = repository.getAll().collectAsState(initial = emptyList()).value.reversed()
    val scope = rememberCoroutineScope()
    GlanceTheme {
        LazyColumn(
            modifier = GlanceModifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        ) {
            items(tasks.size) {
                val task = tasks[it]
                Box(
                    modifier = GlanceModifier.fillMaxWidth().height(64.dp).clickable {
                        scope.launch {
                            repository.update(task.copy(isDone = task.isDone.not()), false)
                            updateAppWidgetState(context, id) {

                            }
                        }
                    }
                ) {
                    Row(
                        modifier = GlanceModifier.fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = androidx.glance.layout.Alignment.CenterVertically,
                    ) {
                        androidx.glance.text.Text(text = task.title)
                        Spacer(GlanceModifier.defaultWeight())
                        RadioButton(checked = task.isDone, onClick = null)

                    }
                }
            }
//            androidx.glance.text.Text(text = "hello")
        }
    }
}


class TaskWidgetGlance(private val repository: TaskRepository) : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            TasksWidget(repository, context, id)
        }
    }
}


@AndroidEntryPoint
class TaskWidgetReceiver : GlanceAppWidgetReceiver() {
    @Inject
    lateinit var repository: TaskRepository
    override val glanceAppWidget: GlanceAppWidget
        get() = TaskWidgetGlance(repository)
}
