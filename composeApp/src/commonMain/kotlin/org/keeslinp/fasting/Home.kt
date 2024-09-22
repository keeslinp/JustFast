package org.keeslinp.fasting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import org.keeslinp.fasting.data.fast.DisplayFast
import org.keeslinp.fasting.screens.HomeComponent
import org.keeslinp.fasting.screens.HomeComponent.FastState

@Composable
fun FastLabel(label: String) {
    Text(label, style = MaterialTheme.typography.labelSmall.copy(lineHeight = MaterialTheme.typography.bodyLarge.lineHeight), color = MaterialTheme.colorScheme.secondary)
}

@Composable
fun FastRow(fast: DisplayFast, modifier: Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Card(onClick = { expanded = !expanded }, modifier = modifier) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(fast.startDate)
                    Text("${fast.durationHours} hours", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand"
                )
            }
            AnimatedVisibility(expanded) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column {
                        FastLabel("Start:")
                        if (fast.endTime != null) {
                            FastLabel("End:")
                        }
                    }
                    Column {
                        Text(fast.startTime)
                        fast.endTime?.also {
                            Text(it)
                        }

                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeInterior(fastState: FastState?, toggleFast: () -> Unit, history: ImmutableList<DisplayFast>) {
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Home") }) }
    ) { padding ->
        Surface(modifier = Modifier.padding(padding)) {
            LazyColumn(modifier = Modifier.padding(horizontal = 8.dp)) {
                if (fastState == null) {
                    item { CircularProgressIndicator() }
                } else {
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text(fastState.contentText)
                            Button(onClick = toggleFast) {
                                Text(fastState.toggleText)
                            }
                        }
                    }
                }
                item { Text("Past fasts:", style = MaterialTheme.typography.headlineSmall) }
                if (history.isEmpty()) {
                    item { Text("No fasting history")}
                }
                items(history, key = {it.id}) { fast ->
                    FastRow(fast, Modifier.animateItem(placementSpec = spring(
                        stiffness = Spring.StiffnessHigh,
                        visibilityThreshold = IntOffset.VisibilityThreshold
                    )
                    ))
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
fun HomeContent(component: HomeComponent) {
    val state by component.fastState.collectAsState()
    val history by component.history.collectAsState()
    HomeInterior(state, component::toggleFast, history)
}
