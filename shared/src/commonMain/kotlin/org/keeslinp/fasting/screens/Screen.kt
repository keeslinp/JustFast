package org.keeslinp.fasting.screens

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Home : Screen

    @Serializable
    data object Settings : Screen
}