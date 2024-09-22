package org.keeslinp.fasting

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import org.keeslinp.fasting.screens.RootComponent


@Composable
fun RootContent(component: RootComponent, modifier: Modifier = Modifier) {
    Children(stack = component.stack, modifier=modifier, animation = stackAnimation(fade())) {
        when (val child = it.instance) {
            is RootComponent.Child.Home -> HomeContent(child.component)
        }
    }
}