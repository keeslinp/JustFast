package org.keeslinp.fasting.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
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

@Composable
fun FastRow(
    fast: DisplayFast,
    modifier: Modifier = Modifier,
    updater: ((FastEntity) -> FastEntity) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Card(onClick = { expanded = !expanded }, modifier = modifier) {
        Column(modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()) {
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
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
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
                }
            }
        }
    }
}

@Composable
fun ActiveFast(fast: DisplayFast, updater: ((FastEntity) -> FastEntity) -> Unit) {
    Card(modifier = Modifier.padding(bottom = 16.dp)) {
        Column (modifier = Modifier.padding(16.dp)) {
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
                        Text(it, modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun NoActiveFast() {
    Text("Start a fast!")
}

private fun LazyListScope.fastHistoryItems(
    history: ImmutableList<DisplayFast>,
    updater: (id: Long, (FastEntity) -> FastEntity) -> Unit
) {
    items(history, key = { it.id }) { fast ->
        FastRow(
            fast, Modifier.animateItem(
                placementSpec = spring(
                    stiffness = Spring.StiffnessHigh,
                    visibilityThreshold = IntOffset.VisibilityThreshold
                )
            )
        ) { updater(fast.id, it) }
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
fun HomeInterior(
    fastState: FastState?,
    toggleFast: () -> Unit,
    history: ImmutableList<DisplayFast>,
    updater: (id: Long, (FastEntity) -> FastEntity) -> Unit
) {
    Scaffold { padding ->
        Surface(modifier = Modifier.padding(padding)) {
            LazyColumn(modifier = Modifier.padding(horizontal = 8.dp)) {
                if (fastState == null) {
                    item { CircularProgressIndicator() }
                } else {
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            when (fastState) {
                                is FastState.Active -> ActiveFast(fastState.fast) {
                                    updater(
                                        fastState.fast.id,
                                        it
                                    )
                                }

                                FastState.Inactive -> NoActiveFast()
                            }
                            Button(onClick = toggleFast) {
                                Text(fastState.toggleText)
                            }
                        }
                    }
                }
                if (!history.isEmpty()) {
                    item { Spacer(Modifier.height(16.dp)) }
                }
                fastHistoryItems(history, updater)
            }
        }
    }
}

@Composable
fun HomeContent(component: HomeViewModel) {
    val state by component.fastState.collectAsState()
    val history by component.history.collectAsState()
    HomeInterior(state, component::toggleFast, history, component::updateFast)
}
