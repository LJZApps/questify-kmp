package de.ljz.questify.feature.auth.presentation.screens.login

import de.ljz.questify.core.presentation.navigation.AppNavKey
import kotlinx.serialization.Serializable

@Serializable
data class LoginRoute(val code: String? = null) : AppNavKey
