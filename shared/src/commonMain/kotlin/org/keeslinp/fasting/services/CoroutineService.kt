package org.keeslinp.fasting.services

import kotlinx.coroutines.CoroutineScope

interface CoroutineService {
    suspend fun start(scope: CoroutineScope)
}