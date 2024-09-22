package org.keeslinp.fasting

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.collections.immutable.persistentListOf
import org.keeslinp.fasting.data.fast.DisplayFast
import org.keeslinp.fasting.screens.HomeComponent

@Composable
@Preview
private fun NoFast() {
    MaterialTheme {
        HomeInterior(fastState = HomeComponent.FastState.Inactive, toggleFast = {}, history = persistentListOf())
    }
}

@Composable
@Preview
private fun History() {
    MaterialTheme {
        HomeInterior(fastState = HomeComponent.FastState.Inactive, toggleFast = {}, history = persistentListOf(
            DisplayFast(id = 0, startTime = "Sep 21, 2024 6:02 PM", endTime = "Sep 22, 2024 11:00 AM", startDate = "Sep 22, 2024", durationHours = 15),
            DisplayFast(id = 1, startTime = "Sep 19, 2024 6:02 PM", endTime = "Sep 20, 2024 11:00 AM", startDate = "Sep 19, 2024", durationHours = 15)
        ))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview
private fun ActiveFast() {
    MaterialTheme {
        HomeInterior(fastState = HomeComponent.FastState.Active(DisplayFast(startTime = "Sep 21, 2024 6:02 PM", id = 1, startDate = "Sep 21, 2024", durationHours = 10)), toggleFast = {}, history = persistentListOf())
    }
}
