package org.keeslinp.fasting.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.keeslinp.fasting.screens.HomeScreen
import org.keeslinp.fasting.screens.HomeViewModel

@Composable
fun RootContent() {
    val navController = rememberNavController()
    Scaffold { padding ->
        NavHost(navController, startDestination = HomeScreen, modifier = Modifier.padding(padding)) {
            composable<HomeScreen> { HomeContent(viewModel { HomeViewModel() }) }
        }
    }
}