package de.ljz.questify.core.data.sync

import de.ljz.questify.feature.profile.domain.use_cases.FetchRemoteProfileUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SyncCoordinator(
    private val questSyncManager: QuestSyncManager,
    private val fetchRemoteProfileUseCase: FetchRemoteProfileUseCase
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun syncAll() {
        scope.launch {
            try {
                // Parallel sync
                launch { questSyncManager.sync() }
                launch { fetchRemoteProfileUseCase() }
            } catch (e: Exception) {
                // Log error
            }
        }
    }
}
