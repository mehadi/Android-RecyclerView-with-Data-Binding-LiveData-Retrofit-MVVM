package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userdetail

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.R
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.components.UserAvatar
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.theme.MotionDuration
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.theme.MotionEasing
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.theme.Spacing

@Composable
fun UserDetailRoute(
    onBack: () -> Unit,
    viewModel: UserDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    UserDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onToggleFavorite = viewModel::onToggleFavorite,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    uiState: UserDetailUiState,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val user = uiState.user

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.user_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                actions = {
                    if (user != null) {
                        val favoriteLabel = stringResource(
                            if (user.isFavorite) R.string.remove_from_favorites else R.string.add_to_favorites,
                            user.displayName,
                        )
                        IconButton(onClick = onToggleFavorite) {
                            Icon(
                                imageVector = if (user.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                contentDescription = favoriteLabel,
                                tint = if (user.isFavorite) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            )
                        }
                    }
                },
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                user == null -> Text(
                    text = stringResource(R.string.user_not_found),
                    modifier = Modifier.align(Alignment.Center).padding(Spacing.xl),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                else -> {
                    val visibleState = remember(user.id) { MutableTransitionState(false).apply { targetState = true } }
                    AnimatedVisibility(
                        visibleState = visibleState,
                        enter = fadeIn(
                            animationSpec = tween(MotionDuration.Long, easing = MotionEasing.StandardDecelerate),
                        ) + slideInVertically(
                            initialOffsetY = { fullHeight -> fullHeight / 8 },
                            animationSpec = tween(MotionDuration.Long, easing = MotionEasing.StandardDecelerate),
                        ),
                    ) {
                        UserDetailContent(
                            user = user,
                            onEmailClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = "mailto:${user.email}".toUri()
                                }
                                context.startActivity(intent)
                            },
                            onPhoneClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = "tel:${user.phone}".toUri()
                                }
                                context.startActivity(intent)
                            },
                            onWebsiteClick = {
                                val url = if (user.website.startsWith("http://") || user.website.startsWith("https://")) {
                                    user.website
                                } else {
                                    "https://${user.website}"
                                }
                                val intent = Intent(Intent.ACTION_VIEW).apply { data = url.toUri() }
                                context.startActivity(intent)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserDetailContent(
    user: User,
    onEmailClick: () -> Unit,
    onPhoneClick: () -> Unit,
    onWebsiteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        // Hero section.
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            UserAvatar(initials = user.initials, size = 96.dp)
            Spacer(modifier = Modifier.size(Spacing.md))
            Text(
                text = user.displayName,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.size(Spacing.xs))
            Text(
                text = "@${user.username}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        InfoCard(title = stringResource(R.string.user_detail_section_contact)) {
            InfoRow(
                icon = Icons.Outlined.Email,
                label = stringResource(R.string.email_icon_label),
                value = user.email,
                onClick = onEmailClick,
            )
            if (user.phone.isNotBlank()) {
                InfoRow(
                    icon = Icons.Outlined.Phone,
                    label = stringResource(R.string.phone_icon_label),
                    value = user.phone,
                    onClick = onPhoneClick,
                )
            }
        }

        if (user.website.isNotBlank()) {
            InfoCard(title = stringResource(R.string.user_detail_section_web)) {
                InfoRow(
                    icon = Icons.Outlined.Language,
                    label = stringResource(R.string.website_icon_label),
                    value = user.website,
                    onClick = onWebsiteClick,
                )
            }
        }

        if (user.company.isNotBlank()) {
            InfoCard(title = stringResource(R.string.user_detail_section_company)) {
                InfoRow(
                    icon = Icons.Outlined.Business,
                    label = stringResource(R.string.company_icon_label),
                    value = user.company,
                    onClick = null,
                )
            }
        }
    }
}

/** A titled, tonal card that groups related [InfoRow]s together. */
@Composable
private fun InfoCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            content()
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            // Ensures the tappable email/phone/website rows meet the 48dp minimum touch target
            // even though their icon+two-line-text content is naturally shorter than that.
            .heightIn(min = 48.dp)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.size(Spacing.sm))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
