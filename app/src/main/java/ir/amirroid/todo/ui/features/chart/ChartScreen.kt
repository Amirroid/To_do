package ir.amirroid.todo.ui.features.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.amirroid.todo.R
import ir.amirroid.todo.ui.components.BottomBarSpacer
import ir.amirroid.todo.ui.components.CircleChart
import ir.amirroid.todo.ui.components.HelpColorList
import ir.amirroid.todo.ui.components.LineChartView
import ir.amirroid.todo.utils.getType
import ir.amirroid.todo.viewmodels.ChartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartScreen() {
    val viewModel: ChartViewModel = hiltViewModel()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        CenterAlignedTopAppBar(title = { Text(text = stringResource(id = R.string.chart)) })
        Text(
            text = stringResource(id = R.string.sort_by_time),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 12.dp, start = 12.dp)
        )
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(top = 8.dp)
                .fillMaxWidth()
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.primaryContainer,
                    MaterialTheme.shapes.medium
                ),
        ) {
            Row(modifier = Modifier.padding(12.dp)) {
                LineChartView(
                    data = tasks,
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                HelpColorList()
            }
        }
        Text(
            text = stringResource(id = R.string.categorize_by_activity),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 12.dp, start = 12.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(top = 8.dp)
                .fillMaxWidth()
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.primaryContainer,
                    MaterialTheme.shapes.medium
                ),
        ) {
            Row(modifier = Modifier.padding(12.dp)) {
                Spacer(modifier = Modifier.width(12.dp))
                CircleChart(
                    data = tasks.map { it.getType() }, modifier = Modifier
                        .padding(vertical = 12.dp)
                        .weight(0.8f)
                )
                Spacer(modifier = Modifier.weight(.2f))
                HelpColorList()
            }
        }
        BottomBarSpacer()
    }
}
