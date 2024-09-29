package org.keeslinp.fasting.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetAt
import kotlinx.datetime.toLocalDateTime
import org.keeslinp.fasting.data.fast.DisplayFast
import org.keeslinp.fasting.data.fast.FastEntity
import org.keeslinp.fasting.screens.HomeViewModel
import org.keeslinp.fasting.screens.HomeViewModel.FastState
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Composable
fun FastLabel(label: String) {
    Text(
        label,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

enum class DialogState {
    None,
    Date,
    Time
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FastDate(text: String, timeSeconds: Long, onChange: (Long) -> Unit) {
    var dialogState by remember { mutableStateOf(DialogState.None) }
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()
    fun getOffset() = TimeZone.currentSystemDefault()
        .offsetAt(Instant.fromEpochMilliseconds(timeSeconds * 1000)).totalSeconds

    fun onConfirm() {
        dialogState = DialogState.None
        onChange(
            (datePickerState.selectedDateMillis
                ?: 0) / 1000 - getOffset() + (timePickerState.hour * 60 + timePickerState.minute) * 60
        )
    }

    fun onCancel() {
        dialogState = DialogState.None
    }

    fun onEdit() {
        dialogState = DialogState.Date
        datePickerState.selectedDateMillis = (timeSeconds + getOffset()) * 1000
        val local =
            Instant.fromEpochSeconds(timeSeconds).toLocalDateTime(TimeZone.currentSystemDefault())
        timePickerState.hour = local.hour
        timePickerState.minute = local.minute
    }
    Card(::onEdit, modifier = Modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text)
            Icon(
                imageVector = Icons.Default.Edit,
                tint = MaterialTheme.colorScheme.secondary,
                contentDescription = "Edit",
                modifier = Modifier
                    .padding(start = 8.dp, bottom = 4.dp)
                    .size(18.dp)
            )
        }
    }
    when (dialogState) {
        DialogState.None -> {}
        DialogState.Date -> {
            DatePickerDialog(
                confirmButton = { Button({ dialogState = DialogState.Time }) { Text("Next") } },
                onDismissRequest = ::onCancel,
                dismissButton = { Button(::onCancel) { Text("Cancel") } }) {
                DatePicker(datePickerState)
            }
        }

        DialogState.Time -> {
            AlertDialog(
                onDismissRequest = ::onCancel,
                dismissButton = { Button({ dialogState = DialogState.Date }) { Text("Back") } },
                confirmButton = { Button(::onConfirm) { Text("Confirm") } },
                text = {
                    TimePicker(timePickerState)
                }
            )
        }
    }
}

@Composable
fun DeleteButton(onDelete: () -> Unit, content: @Composable (onPress: () -> Unit) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm deletion") },
            text = { Text("Are you sure you want to delete this fast?") },
            dismissButton = {
                TextButton({ showDialog = false }) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                TextButton({
                    showDialog = false
                    onDelete()
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }
    content {
        showDialog = true
    }
}

@Composable
fun FastRow(
    fast: DisplayFast,
    modifier: Modifier = Modifier,
    updater: ((FastEntity) -> FastEntity) -> Unit,
    delete: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Card(onClick = { expanded = !expanded }, modifier = modifier) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(fast.startDate, modifier = Modifier.testTag("start-date"))
                    Text(
                        fast.durationText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) {
                        "Collapse"
                    } else {
                        "Expand"
                    },
                )
            }
            AnimatedVisibility(expanded) {
                Column {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            FastLabel("Start:")
                            FastLabel("End:")
                        }
                        Column(modifier = Modifier.width(IntrinsicSize.Max)) {
                            FastDate(fast.startTime, fast.startSeconds) { newStart ->
                                updater {
                                    it.copy(
                                        startTime = newStart
                                    )
                                }
                            }
                            FastDate(fast.endTime, fast.endSeconds) { newEnd ->
                                updater {
                                    it.copy(
                                        startTime = newEnd
                                    )
                                }
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End), modifier = Modifier.fillMaxWidth()) {
                        if (fast.first) {
                            Button({ updater { it.copy(endTime = null)}}) {
                                Icon(Icons.Outlined.Restore, "Resume fast")
                                Text("Resume")

                            }
                        }
                        DeleteButton(delete) {
                            Button(it) {
                                Icon(Icons.Outlined.Delete, "Delete fast")
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TickWhileActive(duration: Duration, action: () -> Unit) {
    var active by remember { mutableStateOf(false) }
    LifecycleResumeEffect(Unit) {
        active = true
        onPauseOrDispose { active = false }
    }
    LaunchedEffect(active, action, duration) {
        while (active) {
            action()
            delay(duration)
        }
    }
}

@Composable
fun rememberCurrentTime(active: Boolean): Long {
    var currentTime by remember {
        mutableLongStateOf(
            Clock.System.now().epochSeconds
        )
    }
    if (active) {
        TickWhileActive(duration = 1.minutes) {
            currentTime = Clock.System.now().epochSeconds
        }
    }

    return currentTime
}

@Composable
fun ProgressCircle(fast: DisplayFast?) {
    val currentTime = rememberCurrentTime(fast != null)
    val backgroundColor by animateColorAsState(
        if (fast == null) {
            MaterialTheme.colorScheme.surfaceBright
        } else {
            MaterialTheme.colorScheme.onSurface
        }, label = "Arc background color"
    )
    val completionRatio by animateFloatAsState(fast?.let { (currentTime - it.startSeconds).toFloat() / it.goalDuration.toFloat() }
        ?: 0f, label = "Completion ratio")
    val primaryColor = MaterialTheme.colorScheme.primary

    val padding = 6f;
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.0f)
    ) {
        drawCircle(
            color = backgroundColor,
            style = Stroke(width = 32f),
            radius = (size.width / 2) - padding
        )
        if (completionRatio > 0f) {
            rotate(90f) {
                drawArc(
                    color = primaryColor,
                    startAngle = 0f,
                    sweepAngle = 360f * completionRatio,
                    useCenter = false,
                    topLeft = Offset(padding, padding),
                    style = Stroke(width = 32f),
                    size = Size(size.width - (padding * 2), size.height - (padding * 2))
                )
            }
        }
    }
}

@Composable
fun ActiveFastInterior(fast: DisplayFast, updater: ((FastEntity) -> FastEntity) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Column {
                FastLabel("Start:")
                FastLabel("Target:")
            }
            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .height(IntrinsicSize.Max)
            ) {
                // If the fast is gone, remember the last fast
                FastDate(fast.startTime, fast.startSeconds) { newStart ->
                    updater {
                        it.copy(
                            startTime = newStart
                        )
                    }
                }
                FastDate(fast.endTime, fast.endSeconds) { newEnd ->
                    updater {
                        it.copy(
                            goalDuration = newEnd - it.startTime
                        )
                    }
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                { updater { it.copy(goalDuration = it.goalDuration - 3600) } },
                enabled = fast.goalDuration > 3600,
            ) {
                Icon(Icons.Outlined.Remove, "Subtract an hour")
            }
            Text(fast.durationText)
            IconButton({ updater { it.copy(goalDuration = it.goalDuration + 3600) } }) {
                Icon(Icons.Outlined.Add, "Add an hour")
            }

        }
    }
}

@Composable
fun ActiveFastArea(
    toggleFast: () -> Unit,
    updater: (id: Long, (FastEntity) -> FastEntity) -> Unit,
    fastState: FastState
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
                ProgressCircle(fastState.fast)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AnimatedVisibility(visible = fastState.fast != null, label = "loading") {
                        // We want to remember the previous fast so that when we are fading out
                        // we can continue to show the fast
                        var rememberedFast by remember { mutableStateOf(fastState.fast) }
                        SideEffect {
                            // We don't want to set to null because we want the fast to last until AnimatedVisibility removes
                            // this composable from the tree
                            if (rememberedFast != fastState.fast && fastState.fast != null) {
                                rememberedFast = fastState.fast
                            }
                        }
                        (fastState.fast ?: rememberedFast)?.also { fast ->
                            ActiveFastInterior(fast) { updater(fast.id, it) }
                        }
                    }
                    Button(
                        toggleFast,
                    ) {
                        Text(fastState.toggleText)
                    }
                }
            }
        }
    }
}

private fun LazyListScope.fastHistoryItems(
    history: ImmutableList<DisplayFast>,
    updater: (id: Long, (FastEntity) -> FastEntity) -> Unit,
    delete: (id: Long) -> Unit,
) {
    items(history, key = { it.id }) { fast ->
        FastRow(
            fast,
            Modifier.animateItem(
                placementSpec = null
            ),
            updater = { updater(fast.id, it) },
            delete = { delete(fast.id) },
        )
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
fun HomeInterior(
    fastState: FastState?,
    toggleFast: () -> Unit,
    history: ImmutableList<DisplayFast>,
    updater: (id: Long, (FastEntity) -> FastEntity) -> Unit,
    delete: (id: Long) -> Unit,
) {
    Surface {
        LazyColumn(modifier = Modifier.padding(horizontal = 8.dp)) {
            item {
                fastState?.also { ActiveFastArea(toggleFast, updater, it) }
                    ?: CircularProgressIndicator()
            }
            if (!history.isEmpty()) {
                item { Spacer(Modifier.height(16.dp)) }
            }
            fastHistoryItems(history, updater, delete)
        }
    }
}

@Composable
fun HomeContent(viewModel: HomeViewModel) {
    val state by viewModel.fastState.collectAsState()
    val history by viewModel.history.collectAsState()
    HomeInterior(
        state,
        viewModel::toggleFast,
        history,
        viewModel::updateFast,
        viewModel::deleteFast
    )
}
