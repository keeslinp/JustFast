package org.keeslinp.fasting

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.keeslinp.fasting.data.fast.FastEntity
import org.keeslinp.fasting.screens.HomeComponent
import java.time.Clock

@Composable
@Preview
private fun NoFast() {
    MaterialTheme {
        HomeInterior(state = HomeComponent.State.Inactive, toggleFast = {})
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview
private fun ActiveFast() {
    MaterialTheme {
        HomeInterior(state = HomeComponent.State.Active(FastEntity(startTime = Clock.systemDefaultZone().millis())), toggleFast = {})
    }
}
