package de.ljz.questify.feature.main.presentation

data class MainUiState(
    val userPoints: Int,
    val userXP: Int,
    val userLevel: Int,
    val userName: String,
    val userProfilePicture: String,
)