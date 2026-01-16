package de.ljz.questify.core.domain.use_cases

import de.ljz.questify.core.domain.repositories.SyncRepository

class SyncUseCase(
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return syncRepository.sync()
    }
}
