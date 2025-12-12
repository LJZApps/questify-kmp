package de.ljz.questify.feature.quests.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.ljz.questify.R

private const val SPACING_RATIO = 0.1f

@Composable
fun EasyIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    Canvas(modifier = modifier.size(16.dp).aspectRatio(1f)) {
        val padding = size.width * 0.15f
        inset(padding) {
            rotate(45f) {
                drawSword(tint)
            }
        }
    }
}

@Composable
fun MediumIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    Canvas(modifier = modifier.size(16.dp).aspectRatio(1f)) {
        val gap = size.width * SPACING_RATIO

        val swordSize = (size.width - gap) / 2

        val verticalInset = (size.height - swordSize) / 2

        inset(
            left = 0f,
            top = verticalInset,
            right = size.width - swordSize,
            bottom = verticalInset
        ) {
            rotate(45f) { drawSword(tint) }
        }

        inset(
            left = size.width - swordSize,
            top = verticalInset,
            right = 0f,
            bottom = verticalInset
        ) {
            rotate(45f) { drawSword(tint) }
        }
    }
}

@Composable
fun HardIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    Canvas(modifier = modifier.size(16.dp).aspectRatio(1f)) {
        val gap = size.width * SPACING_RATIO

        val swordSize = (size.width - gap) / 2

        val sideInsetTop = (size.width - swordSize) / 2
        val bottomInsetTop = size.height / 2 + gap / 2

        inset(
            left = sideInsetTop,
            top = 0f,
            right = sideInsetTop,
            bottom = bottomInsetTop
        ) {
            rotate(45f) { drawSword(tint) }
        }

        val topInsetBottom = size.height / 2 + gap / 2
        val rightInsetBottom = size.width / 2 + gap / 2

        inset(
            left = 0f,
            top = topInsetBottom,
            right = rightInsetBottom,
            bottom = 0f
        ) {
            rotate(45f) { drawSword(tint) }
        }

        val leftInsetBottom = size.width / 2 + gap / 2

        inset(
            left = leftInsetBottom,
            top = topInsetBottom,
            right = 0f,
            bottom = 0f
        ) {
            rotate(45f) { drawSword(tint) }
        }
    }
}

private fun DrawScope.drawSword(color: Color) {
    val path = Path().apply {
        moveTo(size.width / 2f, 0f)
        lineTo(size.width * 0.65f, size.height * 0.6f)
        lineTo(size.width * 0.35f, size.height * 0.6f)
        close()
    }
    drawPath(path = path, color = color)

    drawLine(
        color = color,
        start = Offset(size.width * 0.25f, size.height * 0.6f),
        end = Offset(size.width * 0.75f, size.height * 0.6f),
        strokeWidth = size.width * 0.1f // Etwas dünnerer Strich (10% statt 15%) für eleganteren Look
    )

    drawRect(
        color = color,
        topLeft = Offset(size.width * 0.45f, size.height * 0.6f),
        size = Size(width = size.width * 0.1f, height = size.height * 0.4f)
    )
}

@Composable
private fun DifficultyIconsPreview() {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            EasyIcon(Modifier.size(32.dp))
            Text(
                stringResource(id = R.string.difficulty_easy),
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            MediumIcon(Modifier.size(32.dp))
            Text(
                stringResource(id = R.string.difficulty_medium),
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            HardIcon(Modifier.size(32.dp))
            Text(
                stringResource(id = R.string.difficulty_hard),
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Preview
@Composable
private fun PreviewDifficultyIcons() {
    MaterialTheme {
        DifficultyIconsPreview()
    }
}