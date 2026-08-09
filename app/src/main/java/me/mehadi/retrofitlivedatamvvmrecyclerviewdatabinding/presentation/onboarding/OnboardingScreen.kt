package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.launch
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.R
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.theme.MotionDuration
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.theme.MotionEasing
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.theme.Spacing
import kotlin.math.absoluteValue

@Composable
fun OnboardingRoute(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    OnboardingScreen(
        onFinished = {
            viewModel.onOnboardingFinished()
            onFinished()
        },
    )
}

private data class OnboardingPageUi(
    val icon: ImageVector,
    val headlineRes: Int,
    val bodyRes: Int,
)

private val onboardingPages =
    listOf(
        OnboardingPageUi(
            icon = Icons.Outlined.Groups,
            headlineRes = R.string.onboarding_page1_headline,
            bodyRes = R.string.onboarding_page1_body,
        ),
        OnboardingPageUi(
            icon = Icons.Outlined.FavoriteBorder,
            headlineRes = R.string.onboarding_page2_headline,
            bodyRes = R.string.onboarding_page2_body,
        ),
        OnboardingPageUi(
            icon = Icons.Outlined.CloudOff,
            headlineRes = R.string.onboarding_page3_headline,
            bodyRes = R.string.onboarding_page3_body,
        ),
    )

/**
 * Stateless first-run onboarding flow. Manages its own pager position as transient UI state;
 * the only thing it reports outward is [onFinished], fired once the user skips or completes it.
 */
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == onboardingPages.lastIndex

    Column(modifier = modifier.fillMaxSize()) {
        SkipButton(
            visible = !isLastPage,
            onSkip = onFinished,
            modifier = Modifier.fillMaxWidth(),
        )

        HorizontalPager(
            state = pagerState,
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
        ) { page ->
            OnboardingPageContent(
                page = onboardingPages[page],
                modifier =
                    Modifier.graphicsLayer {
                        val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                        val fraction = pageOffset.absoluteValue.coerceIn(0f, 1f)
                        alpha = 1f - (fraction * 0.6f)
                        val scale = 1f - (fraction * 0.15f)
                        scaleX = scale
                        scaleY = scale
                    },
            )
        }

        PageIndicator(
            pageCount = onboardingPages.size,
            currentPage = pagerState.currentPage,
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = Spacing.lg),
        )

        Button(
            onClick = {
                if (isLastPage) {
                    onFinished()
                } else {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            page = pagerState.currentPage + 1,
                            animationSpec = tween(MotionDuration.MEDIUM, easing = MotionEasing.Standard),
                        )
                    }
                }
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg)
                    .padding(bottom = Spacing.xl),
        ) {
            AnimatedContent(
                targetState = isLastPage,
                transitionSpec = {
                    fadeIn(tween(MotionDuration.SHORT)) togetherWith fadeOut(tween(MotionDuration.SHORT))
                },
                label = "onboardingButtonLabel",
            ) { lastPage ->
                Text(
                    text =
                        if (lastPage) {
                            stringResource(R.string.onboarding_get_started)
                        } else {
                            stringResource(R.string.onboarding_next)
                        },
                )
            }
        }
    }
}

/**
 * Pulled out into its own composable (rather than left inline inside [OnboardingScreen]'s
 * [Column]) so this call resolves to the plain, receiver-less `AnimatedVisibility` overload
 * instead of colliding with the `ColumnScope`-scoped one visible through the enclosing `Column`.
 */
@Composable
private fun SkipButton(
    visible: Boolean,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                // A minimum rather than exact height, so the "Skip" label doesn't get squeezed or
                // clipped when the system font scale is increased.
                .heightIn(min = 56.dp)
                .padding(end = Spacing.md),
        contentAlignment = Alignment.CenterEnd,
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(MotionDuration.SHORT)),
            exit = fadeOut(tween(MotionDuration.SHORT)),
        ) {
            TextButton(onClick = onSkip) {
                Text(stringResource(R.string.onboarding_skip))
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPageUi,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        OnboardingIcon(icon = page.icon)
        Spacer(modifier = Modifier.height(Spacing.xxl))
        Text(
            text = stringResource(page.headlineRes),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        Text(
            text = stringResource(page.bodyRes),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun OnboardingIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            // Decorative: the headline and body text right below fully convey the page's meaning.
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Composable
private fun PageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    // The dots are a purely visual cue; without this, TalkBack users get no information at all
    // about onboarding progress. liveRegion re-announces this description as the page changes,
    // even though the indicator itself never takes accessibility focus.
    val pageDescription = stringResource(R.string.onboarding_page_indicator, currentPage + 1, pageCount)
    Row(
        modifier =
            modifier
                .semantics(mergeDescendants = true) {
                    contentDescription = pageDescription
                    liveRegion = LiveRegionMode.Polite
                },
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val selected = index == currentPage
            val width by animateDpAsState(
                targetValue = if (selected) 24.dp else 8.dp,
                animationSpec = tween(MotionDuration.MEDIUM, easing = MotionEasing.Standard),
                label = "indicatorWidth",
            )
            val color by animateColorAsState(
                targetValue =
                    if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                animationSpec = tween(MotionDuration.MEDIUM, easing = MotionEasing.Standard),
                label = "indicatorColor",
            )
            Box(
                modifier =
                    Modifier
                        .height(8.dp)
                        .width(width)
                        .clip(CircleShape)
                        .background(color),
            )
        }
    }
}
