package org.keeslinp.fasting.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.collections.immutable.persistentListOf
import org.keeslinp.fasting.screens.Screen
import org.keeslinp.fasting.screens.HomeViewModel

data class TopLevelRoute<T : Any>(
    val name: String,
    val route: T,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val topLevelRoutes = persistentListOf(
    TopLevelRoute("Home", Screen.Home, Icons.Filled.Home, Icons.Outlined.Home),
    TopLevelRoute("Settings", Screen.Settings, Icons.Filled.Settings, Icons.Outlined.Settings)
)

@Composable
fun RootContent() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                topLevelRoutes.forEach { route ->
                    val selected = currentDestination?.hierarchy?.any {
                        it.hasRoute(route.route::class)
                    } == true
                    NavigationBarItem(
                        selected = selected,
                        icon = {
                            Icon(
                                if (selected) {
                                    route.selectedIcon
                                } else {
                                    route.unselectedIcon
                                }, route.name
                            )
                        },
                        onClick = {
                            navController.navigate(route.route)
                        },
                        label = { Text(route.name) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController,
            startDestination = Screen.Home,
            modifier = Modifier.padding(padding)
        ) {
            composable<Screen.Home> { HomeContent(viewModel { HomeViewModel() }) }
            composable<Screen.Settings> { SettingsContent() }
        }
    }
}