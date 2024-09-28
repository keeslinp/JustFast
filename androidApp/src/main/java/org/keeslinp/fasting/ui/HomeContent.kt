package org.keeslinp.fasting.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetAt
import kotlinx.datetime.toLocalDateTime
import org.keeslinp.fasting.data.fast.DisplayFast
import org.keeslinp.fasting.data.fast.FastEntity
import org.keeslinp.fasting.screens.HomeViewModel
import org.keeslinp.fasting.screens.HomeViewModel.FastState

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

@OptIn(ExperimentalMaterial3Api::class)
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
                    fast.durationText?.also {
                        Text(
                            it,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        FastLabel("Start:")
                        if (fast.endTime != null) {
                            FastLabel("End:")
                        }
                    }
                    Column(modifier = Modifier.width(IntrinsicSize.Max)) {
                        FastDate(fast.startTime, fast.startSeconds) { newStart ->
                            updater {
                                it.copy(
                                    startTime = newStart
                                )
                            }
                        }
                        fast.endTime?.also { endTime ->
                            fast.endSeconds?.also { endSeconds ->
                                FastDate(endTime, endSeconds) { newEnd ->
                                    updater {
                                        it.copy(
                                            startTime = newEnd
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.weight(1.0f))
                    DeleteButton(delete) {
                        IconButton(it) {
                            Icon(Icons.Default.Delete, "Delete fast")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ActiveFast(
    fast: DisplayFast,
    updater: ((FastEntity) -> FastEntity) -> Unit,
    endFast: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope, sharedTransitionScope: SharedTransitionScope,
) {
    with(sharedTransitionScope) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column {
                Text("Active Fast")
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Column {
                        FastLabel("Start:")
                        if (fast.endTime != null) {
                            FastLabel("Target:")
                        }
                        FastLabel("Duration:")
                    }
                    Column(modifier = Modifier.width(IntrinsicSize.Max)) {
                        FastDate(fast.startTime, fast.startSeconds) { newStart ->
                            updater {
                                it.copy(
                                    startTime = newStart
                                )
                            }
                        }
                        fast.endTime?.also { endTime ->
                            fast.endSeconds?.also { endSeconds ->
                                FastDate(endTime, endSeconds) { newEnd ->
                                    updater {
                                        it.copy(
                                            goalDuration = newEnd - it.startTime
                                        )
                                    }
                                }
                            }
                        }
                        fast.durationText?.also {
                            Text(
                                it,
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
            Button(
                endFast,
                Modifier.sharedBounds(
                    rememberSharedContentState(key = "button"),
                    animatedVisibilityScope
                )
            ) {
                Text("Stop")
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NoActiveFast(
    startFast: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope
) {
    with(sharedTransitionScope) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("No active fast")
            Button(
                startFast,
                Modifier.sharedBounds(
                    rememberSharedContentState(key = "button"),
                    animatedVisibilityScope
                )
            ) {
                Text("Start")
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ActiveFastArea(
    toggleFast: () -> Unit,
    updater: (id: Long, (FastEntity) -> FastEntity) -> Unit,
    fastState: FastState?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card {
            SharedTransitionLayout {
                AnimatedContent(
                    targetState = fastState,
                    label = "Active Fast",
                    modifier = Modifier.padding(16.dp)
                ) { fastState ->
                    when (fastState) {
                        is FastState.Active -> ActiveFast(
                            fastState.fast,
                            updater = {
                                updater(
                                    fastState.fast.id,
                                    it
                                )
                            },
                            endFast = toggleFast,
                            this@AnimatedContent, this@SharedTransitionLayout
                        )

                        FastState.Inactive -> NoActiveFast(
                            toggleFast,
                            this@AnimatedContent,
                            this@SharedTransitionLayout
                        )

                        null -> CircularProgressIndicator()
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
    Scaffold { padding ->
        Surface(modifier = Modifier.padding(padding)) {
            LazyColumn(modifier = Modifier.padding(horizontal = 8.dp)) {
                item {
                    ActiveFastArea(toggleFast, updater, fastState)
                }
                if (!history.isEmpty()) {
                    item { Spacer(Modifier.height(16.dp)) }
                }
                fastHistoryItems(history, updater, delete)
            }
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
