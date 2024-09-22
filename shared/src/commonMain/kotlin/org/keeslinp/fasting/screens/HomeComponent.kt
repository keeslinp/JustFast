package org.keeslinp.fasting.screens

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.keeslinp.fasting.data.fast.FastDao
import org.keeslinp.fasting.data.fast.FastEntity
import org.keeslinp.fasting.useCases.ToggleFastUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface HomeComponent {
    fun toggleFast()
    sealed interface State {
        val toggleText: String
        val contentText: String
        data class Active(val fast: FastEntity): State {
            override val toggleText = "Stop"
            override val contentText: String  get() = "Fasting since ${fast.formatStartTime()}"
        }
        data object Inactive: State {
            override val toggleText = "Start"
            override val contentText = "Not fasting"
        }
    }

    val state: StateFlow<State?>
}
class DefaultHomeComponent(componentContext: ComponentContext): ComponentContext by componentContext, KoinComponent, HomeComponent {
    private val scope = coroutineScope(Dispatchers.IO)
    private val toggleFastUseCase: ToggleFastUseCase by inject()

    override fun toggleFast() {
        scope.launch {
            toggleFastUseCase.toggleFast()
        }
    }

    private val fastDao: FastDao by inject()
    private val activeFast = fastDao.getActiveFast()

    override val state = activeFast.map { if (it != null) { HomeComponent.State.Active(it) } else { HomeComponent.State.Inactive } }.stateIn(scope=scope, started = SharingStarted.WhileSubscribed(5000), initialValue = null)
}