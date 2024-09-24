package org.keeslinp.fasting

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.collections.immutable.persistentListOf
import org.keeslinp.fasting.data.fast.DisplayFast
import org.keeslinp.fasting.data.fast.FastEntity
import org.keeslinp.fasting.screens.HomeComponent

@Composable
@Preview
private fun NoFast() {
    MaterialTheme {
        HomeInterior(fastState = HomeComponent.FastState.Inactive, toggleFast = {}, history = persistentListOf(), { _, _ -> })
    }
}

@Composable
@Preview
private fun History() {
    MaterialTheme {
        HomeInterior(fastState = HomeComponent.FastState.Inactive, toggleFast = {}, history = persistentListOf(
            DisplayFast(id = 0, startSeconds = 1727138788, endSeconds = 1727139789),
        ), { _, _ -> })
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview
private fun ActiveFast() {
    MaterialTheme {
        HomeInterior(fastState = HomeComponent.FastState.Active(DisplayFast(id = 0, startSeconds = 1727138788, endSeconds = 1727139789)), toggleFast = {}, history = persistentListOf(), {_, _ ->})
    }
}
