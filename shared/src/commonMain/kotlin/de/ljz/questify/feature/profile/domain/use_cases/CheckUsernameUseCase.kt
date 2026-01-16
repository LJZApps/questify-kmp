package de.ljz.questify.feature.profile.domain.use_cases

import de.ljz.questify.feature.profile.domain.repositories.AppUserRepository

class CheckUsernameUseCase(
    private val appUserRepository: AppUserRepository
) {
    suspend operator fun invoke(username: String): Boolean {
        if (username.isBlank()) return false
        return appUserRepository.checkUsername(username)
    }
}
