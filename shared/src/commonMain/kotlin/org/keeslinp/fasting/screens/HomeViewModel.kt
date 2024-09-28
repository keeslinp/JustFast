package org.keeslinp.fasting.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.keeslinp.fasting.data.fast.DisplayFast
import org.keeslinp.fasting.data.fast.FastDao
import org.keeslinp.fasting.data.fast.FastEntity
import org.keeslinp.fasting.useCases.ToggleFastUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Serializable
data object HomeScreen

class HomeViewModel(): KoinComponent, ViewModel() {
    private val toggleFastUseCase: ToggleFastUseCase by inject()

    sealed interface FastState {
        val toggleText: String
        val contentText: String
        data class Active(val fast: DisplayFast): FastState {
            override val toggleText = "Stop"
            override val contentText: String = "Fasting since ${fast.startTime}"
        }
        data object Inactive: FastState {
            override val toggleText = "Start"
            override val contentText = "Not fasting"
        }
    }

    fun toggleFast() {
        viewModelScope.launch {
            toggleFastUseCase.toggleFast()
        }
    }

    private val fastDao: FastDao by inject()
    private val activeFast = fastDao.getActiveFast()

    val fastState = activeFast.map { if (it != null) { FastState.Active(it.display()) } else { FastState.Inactive } }.stateIn(scope=viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = null)

    val history = fastDao.getPastFasts().mapLatest { it.map(FastEntity::display).toImmutableList() }.stateIn(scope=viewModelScope, started = SharingStarted.WhileSubscribed(5000), persistentListOf())

    fun updateFast(id: Long, updater: (FastEntity) -> FastEntity) {
        viewModelScope.launch {
            fastDao.getFast(id)?.let(updater)?.also { fastDao.update(it) }
        }
    }
}