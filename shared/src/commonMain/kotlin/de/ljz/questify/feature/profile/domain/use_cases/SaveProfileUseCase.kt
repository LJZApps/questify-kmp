package de.ljz.questify.feature.profile.domain.use_cases

import de.ljz.questify.feature.profile.domain.repositories.AppUserRepository

class SaveProfileUseCase(
    private val appUserRepository: AppUserRepository
) {
    suspend operator fun invoke(
        displayName: String,
        aboutMe: String,
        imageUri: String
    ) {
        appUserRepository.saveProfile(
            displayName = displayName,
            aboutMe = aboutMe,
            imageUri = imageUri,
        )
    }
}