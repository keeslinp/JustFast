package org.keeslinp.fasting.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SettingsContent() {
    Scaffold { padding ->
        Surface(modifier = Modifier.padding(padding)) {
            Text("Settings")
        }
    }
}