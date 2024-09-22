package org.keeslinp.fasting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.keeslinp.fasting.screens.HomeComponent
import org.keeslinp.fasting.screens.HomeComponent.State

@Composable
fun HomeInterior(state: State?, toggleFast: () -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Home") }) }
    ) { padding ->
        Surface(modifier = Modifier.padding(padding)) {
            if (state == null) {
                CircularProgressIndicator()
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(state.contentText)
                    Button(onClick = toggleFast) {
                        Text(state.toggleText)
                    }
                }
            }
        }
    }
}

@Composable
fun HomeContent(component: HomeComponent) {
    val state by component.state.collectAsState()
    HomeInterior(state, component::toggleFast)
}
