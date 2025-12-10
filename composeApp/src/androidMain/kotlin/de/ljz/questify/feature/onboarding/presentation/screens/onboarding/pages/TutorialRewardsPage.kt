package de.ljz.questify.feature.onboarding.presentation.screens.onboarding.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.ljz.questify.R
import de.ljz.questify.feature.onboarding.presentation.components.TutorialRewardComponent

@Composable
fun TutorialRewardsPage(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .statusBarsPadding()
            .padding(top = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.tutorial_rewards_page_title),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = stringResource(R.string.tutorial_rewards_page_description),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            lineHeight = 28.sp,
            fontSize = 18.sp
        )

        TutorialRewardComponent(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.tutorial_rewards_page_points_title),
            description = stringResource(R.string.tutorial_rewards_page_points_description),
            icon = painterResource(R.drawable.coins)
        )

        TutorialRewardComponent(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.tutorial_rewards_page_xp_title),
            description = stringResource(R.string.tutorial_rewards_page_xp_description),
            icon = painterResource(R.drawable.ic_sparkles)
        )

        TutorialRewardComponent(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.tutorial_rewards_page_rank_title),
            description = stringResource(R.string.tutorial_rewards_page_rank_description),
            icon = painterResource(R.drawable.ic_trending_up)
        )
    }
}