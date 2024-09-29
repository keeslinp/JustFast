package org.keeslinp.fasting.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.keeslinp.fasting.data.fast.DisplayFast
import org.keeslinp.fasting.data.fast.FastDao
import org.keeslinp.fasting.data.fast.FastEntity
import org.keeslinp.fasting.useCases.ToggleFastUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeViewModel() : KoinComponent, ViewModel() {
    private val toggleFastUseCase: ToggleFastUseCase by inject()

    sealed interface FastState {
        val toggleText: String
        val contentText: String
        val fast: DisplayFast?

        data class Active(override val fast: DisplayFast) : FastState {
            override val toggleText = "Stop"
            override val contentText: String = "Fasting since ${fast.startTime}"
        }

        data object Inactive : FastState {
            override val fast: DisplayFast? = null
            override val toggleText = "Start"
            override val contentText = "Not fasting"
        }
    }

    private val backgroundScope = CoroutineScope(viewModelScope.coroutineContext + Dispatchers.IO)

    fun toggleFast() {
        backgroundScope.launch {
            toggleFastUseCase.toggleFast()
        }
    }

    private val fastDao: FastDao by inject()
    private val activeFast = fastDao.getActiveFast()

    val fastState = activeFast.map {
        if (it != null) {
            FastState.Active(it.display())
        } else {
            FastState.Inactive
        }
    }.stateIn(
        scope = backgroundScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val history = fastDao.getPastFasts().mapLatest {
        it.withIndex().map { (index, value) ->
            value.display(index == 0)
        }.toImmutableList()
    }
        .stateIn(
            scope = backgroundScope,
            started = SharingStarted.WhileSubscribed(5000),
            persistentListOf()
        )

    fun updateFast(id: Long, updater: (FastEntity) -> FastEntity) {
        backgroundScope.launch {
            fastDao.getFast(id)?.let(updater)?.also { fastDao.update(it) }
        }
    }

    fun deleteFast(id: Long) {
        backgroundScope.launch {
            fastDao.deleteFast(id)
        }
    }
}