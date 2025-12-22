package de.ljz.questify.feature.quests.presentation.dialogs

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import de.ljz.questify.R
import de.ljz.questify.feature.quests.presentation.screens.quest_overview.QuestDoneDialogState
import kotlinx.coroutines.launch

@Suppress("EffectKeys")
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun QuestDoneDialog(
    state: QuestDoneDialogState,
    onDismiss: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    val soundPool = remember {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(attributes)
            .build()
    }

    val soundId = remember { soundPool.load(context, R.raw.success, 1) }

    val fadeIn = remember { Animatable(0f) }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.completed))

    LaunchedEffect(Unit) {
        launch {
            fadeIn.animateTo(1f, animationSpec = tween(500))
        }
    }

    LaunchedEffect(Unit) {
        val timings = longArrayOf(
            0, 10,
            240, 20,
            150, 10,
            40, 15,
            40, 35
        )

        val amplitudes = intArrayOf(
            0, 60,
            0, 110,
            0, 150,
            0, 200,
            0, 255
        )
        vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))

        soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
    }

    DisposableEffect(Unit) {
        onDispose {
            soundPool.release()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        )
    ) {
        Surface(
            shape = AlertDialogDefaults.shape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier.widthIn(min = 280.dp, max = 560.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LottieAnimation(
                    composition = composition,
                    modifier = Modifier
                        .size(80.dp),
                    clipToCompositionBounds = false,
                    clipTextToBoundingBox = false,
                    contentScale = ContentScale.FillBounds
                )

                Text(
                    text = stringResource(R.string.quest_done_dialog_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer { alpha = fadeIn.value }
                )

                Text(
                    text = state.questName,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                if (state.xp > 0 || state.points > 0 || state.newLevel > 0) {
                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
                }

                RewardSection(
                    xp = state.xp,
                    points = state.points
                )

                AnimatedVisibility(visible = state.newLevel > 0) {
                    LevelUpBanner(newLevel = state.newLevel)
                }

                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = if (state.xp > 0 || state.points > 0)
                            stringResource(R.string.quest_done_dialog_take_rewards_button)
                        else
                            stringResource(R.string.quest_done_dialog_great_button)
                    )
                }
            }
        }
    }
}

@Composable
private fun RewardSection(xp: Int, points: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if (xp > 0) {
            RewardItem(label = stringResource(R.string.quest_done_dialog_xp), value = "+$xp")
        }

        if (points > 0) {
            RewardItem(
                label = stringResource(R.string.quest_done_dialog_points),
                value = "+$points"
            )
        }
    }
}

@Composable
private fun RewardItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun LevelUpBanner(newLevel: Int) {
    val scaleAnim = remember { Animatable(0.8f) }

    LaunchedEffect(newLevel) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scaleAnim.value; scaleY = scaleAnim.value }
            .shadow(8.dp, shape = RoundedCornerShape(16.dp))
            .background(color = MaterialTheme.colorScheme.primaryContainer)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_trending_up),
                contentDescription = "Trending up icon",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(48.dp)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.quest_done_dialog_level_up_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = stringResource(
                        R.string.quest_done_dialog_level_up_description,
                        newLevel
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
