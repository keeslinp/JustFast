package org.keeslinp.fasting.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.keeslinp.fasting.data.fast.FastDao
import org.keeslinp.fasting.data.fast.FastEntity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.uuid.Uuid

class FastHistoryEntryViewModel(private val id: Uuid) : KoinComponent, ViewModel() {
    private val fastDao: FastDao by inject()
    private val backgroundScope = CoroutineScope(viewModelScope.coroutineContext + Dispatchers.IO)
    private val hasActiveFast = fastDao.getActiveFastCount().map { it > 0 }
    private val isMostRecentFast = fastDao.getMostRecentFast().map { it == id }
    private val isResumable = hasActiveFast.combine(isMostRecentFast) { hasActive, isLatest -> !hasActive && isLatest}
    val display = fastDao.watchFast(id).filterNotNull()
        .combine(isResumable) { entity, resumable -> entity.display(resumable) }
        .stateIn(backgroundScope, SharingStarted.WhileSubscribed(5000), null)

    private val _expanded = MutableStateFlow(false)
    val expanded = _expanded.asStateFlow()
    fun toggleExpanded() {
        _expanded.update { !it }
    }

    fun update(updater: (FastEntity) -> FastEntity) {
        backgroundScope.launch {
            fastDao.getFast(id)?.let(updater)?.also { fastDao.update(it) }
        }
    }

    fun delete() {
        backgroundScope.launch {
            fastDao.deleteFast(id)
        }
    }
}

class FastHistoryViewModel : KoinComponent, ViewModel() {
    private val fastDao: FastDao by inject()
    private val backgroundScope = CoroutineScope(viewModelScope.coroutineContext + Dispatchers.IO)
    val history =
        fastDao.getPastFasts().map { it.toPersistentList() }
            .stateIn(
                scope = backgroundScope,
                started = SharingStarted.WhileSubscribed(5000),
                persistentListOf()
            )
}