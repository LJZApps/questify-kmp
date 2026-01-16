package de.ljz.questify.core.domain.repositories

interface SyncRepository {
    suspend fun sync(): Result<Unit>
}
