package de.ljz.questify.feature.profile.domain.use_cases

import de.ljz.questify.feature.profile.domain.repositories.AppUserRepository

class FetchRemoteProfileUseCase(
    private val appUserRepository: AppUserRepository
) {
    suspend operator fun invoke() = appUserRepository.fetchRemoteProfile()
}
