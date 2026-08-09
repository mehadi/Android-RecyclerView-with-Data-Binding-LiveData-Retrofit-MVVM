package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userdetail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.PhotoAlbum
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.R
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.Album
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.Post
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.Todo
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.components.UserAvatar
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.theme.MotionDuration
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.theme.MotionEasing
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.theme.Spacing
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userlist.components.UserListError

private const val CONTENT_SKELETON_CARD_COUNT = 3
private const val POST_BODY_MAX_LINES = 3

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
        onRetryContent = viewModel::retryContent,
        onContentErrorShown = viewModel::onContentErrorShown,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    uiState: UserDetailUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onToggleFavorite: () -> Unit = {},
    onRetryContent: () -> Unit = {},
    onContentErrorShown: () -> Unit = {},
) {
    val context = LocalContext.current
    val user = uiState.user
    val snackbarHostState = remember { SnackbarHostState() }

    // A content refresh failure is shown as a dismissible snackbar when cached content is still
    // on screen; only an empty content cache escalates to the inline error section below.
    val contentErrorMessage = uiState.contentErrorRes?.let { stringResource(it) }
    LaunchedEffect(contentErrorMessage, uiState.hasContent) {
        if (contentErrorMessage != null && uiState.hasContent) {
            snackbarHostState.showSnackbar(contentErrorMessage)
            onContentErrorShown()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                        IconButton(onClick = { launchShareIntent(context, user) }) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = stringResource(R.string.detail_share, user.displayName),
                            )
                        }
                        val favoriteLabel =
                            stringResource(
                                if (user.isFavorite) R.string.remove_from_favorites else R.string.add_to_favorites,
                                user.displayName,
                            )
                        IconButton(onClick = onToggleFavorite) {
                            Icon(
                                imageVector = if (user.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                contentDescription = favoriteLabel,
                                tint =
                                    if (user.isFavorite) {
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

                user == null ->
                    Text(
                        text = stringResource(R.string.user_not_found),
                        modifier = Modifier.align(Alignment.Center).padding(Spacing.xl),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                else -> {
                    val visibleState = remember(user.id) { MutableTransitionState(false).apply { targetState = true } }
                    AnimatedVisibility(
                        visibleState = visibleState,
                        enter =
                            fadeIn(
                                animationSpec = tween(MotionDuration.LONG, easing = MotionEasing.StandardDecelerate),
                            ) +
                                slideInVertically(
                                    initialOffsetY = { fullHeight -> fullHeight / 8 },
                                    animationSpec = tween(MotionDuration.LONG, easing = MotionEasing.StandardDecelerate),
                                ),
                    ) {
                        UserDetailContent(
                            uiState = uiState,
                            user = user,
                            onEmailClick = {
                                val intent =
                                    Intent(Intent.ACTION_SENDTO).apply {
                                        data = "mailto:${user.email}".toUri()
                                    }
                                context.startActivity(intent)
                            },
                            onPhoneClick = {
                                val intent =
                                    Intent(Intent.ACTION_DIAL).apply {
                                        data = "tel:${user.phone}".toUri()
                                    }
                                context.startActivity(intent)
                            },
                            onWebsiteClick = {
                                val url =
                                    if (user.website.startsWith("http://") || user.website.startsWith("https://")) {
                                        user.website
                                    } else {
                                        "https://${user.website}"
                                    }
                                val intent = Intent(Intent.ACTION_VIEW).apply { data = url.toUri() }
                                context.startActivity(intent)
                            },
                            onAddressClick =
                                if (user.address.hasCoordinates) {
                                    { launchMapIntent(context, user) }
                                } else {
                                    null
                                },
                            onAddToContactsClick = { launchAddToContactsIntent(context, user) },
                            onRetryContent = onRetryContent,
                        )
                    }
                }
            }
        }
    }
}

/** Opens a maps app at the user's coordinates, pinned and labeled with their display name. */
private fun launchMapIntent(
    context: Context,
    user: User,
) {
    val latitude = user.address.latitude
    val longitude = user.address.longitude
    val label = Uri.encode(user.displayName)
    val intent =
        Intent(Intent.ACTION_VIEW).apply {
            data = "geo:$latitude,$longitude?q=$latitude,$longitude($label)".toUri()
        }
    context.startActivity(intent)
}

/** Shares the user's contact details as plain text, skipping any blank fields. */
private fun launchShareIntent(
    context: Context,
    user: User,
) {
    val shareText =
        listOf(user.displayName, user.email, user.phone, user.website)
            .filter { it.isNotBlank() }
            .joinToString(separator = "\n")
    val intent =
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
    context.startActivity(Intent.createChooser(intent, null))
}

/** Opens the contacts app's "new contact" form pre-filled with this user's details. */
private fun launchAddToContactsIntent(
    context: Context,
    user: User,
) {
    val intent =
        Intent(ContactsContract.Intents.Insert.ACTION).apply {
            type = ContactsContract.RawContacts.CONTENT_TYPE
            putExtra(ContactsContract.Intents.Insert.NAME, user.displayName)
            if (user.email.isNotBlank()) putExtra(ContactsContract.Intents.Insert.EMAIL, user.email)
            if (user.phone.isNotBlank()) putExtra(ContactsContract.Intents.Insert.PHONE, user.phone)
        }
    context.startActivity(intent)
}

@Composable
private fun UserDetailContent(
    uiState: UserDetailUiState,
    user: User,
    onEmailClick: () -> Unit,
    onPhoneClick: () -> Unit,
    onWebsiteClick: () -> Unit,
    onAddressClick: (() -> Unit)?,
    onAddToContactsClick: () -> Unit,
    onRetryContent: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        // Hero section.
        item {
            Column(
                modifier = Modifier.padding(bottom = Spacing.sm),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
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
        }

        item {
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
                ActionRow(
                    icon = Icons.Outlined.PersonAdd,
                    label = stringResource(R.string.detail_add_to_contacts),
                    onClick = onAddToContactsClick,
                )
            }
        }

        if (user.website.isNotBlank()) {
            item {
                InfoCard(title = stringResource(R.string.user_detail_section_web)) {
                    InfoRow(
                        icon = Icons.Outlined.Language,
                        label = stringResource(R.string.website_icon_label),
                        value = user.website,
                        onClick = onWebsiteClick,
                    )
                }
            }
        }

        if (user.company.isNotBlank()) {
            item {
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

        if (user.address.singleLine.isNotBlank()) {
            item {
                InfoCard(title = stringResource(R.string.user_detail_section_address)) {
                    InfoRow(
                        icon = Icons.Outlined.Place,
                        label = stringResource(R.string.location_icon_label),
                        value = user.address.singleLine,
                        onClick = onAddressClick,
                    )
                }
            }
        }

        activityContent(uiState = uiState, onRetryContent = onRetryContent)
    }
}

/** Posts/todos/albums sections, with a compact skeleton on first load and an inline error
 *  with Retry when the refresh failed and nothing is cached — mirroring the list screen. */
private fun LazyListScope.activityContent(
    uiState: UserDetailUiState,
    onRetryContent: () -> Unit,
) {
    val contentErrorRes = uiState.contentErrorRes
    when {
        uiState.isContentLoading && !uiState.hasContent ->
            item {
                UserContentLoading(modifier = Modifier.fillMaxWidth())
            }

        !uiState.hasContent && contentErrorRes != null ->
            item {
                UserListError(
                    message = stringResource(contentErrorRes),
                    onRetry = onRetryContent,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

        else -> {
            if (uiState.posts.isNotEmpty()) {
                item {
                    SectionHeader(title = stringResource(R.string.detail_posts_header, uiState.posts.size))
                }
                items(uiState.posts, key = { "post-${it.id}" }) { post ->
                    PostCard(post = post)
                }
            }

            if (uiState.todos.isNotEmpty()) {
                item {
                    SectionHeader(
                        title =
                            stringResource(
                                R.string.detail_todos_header,
                                uiState.todos.count { it.isCompleted },
                                uiState.todos.size,
                            ),
                    )
                }
                items(uiState.todos, key = { "todo-${it.id}" }) { todo ->
                    TodoRow(todo = todo)
                }
            }

            if (uiState.albums.isNotEmpty()) {
                item {
                    SectionHeader(title = stringResource(R.string.detail_albums_header, uiState.albums.size))
                }
                items(uiState.albums, key = { "album-${it.id}" }) { album ->
                    AlbumRow(album = album)
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = Spacing.sm),
    )
}

@Composable
private fun PostCard(
    post: Post,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = post.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = POST_BODY_MAX_LINES,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun TodoRow(
    todo: Todo,
    modifier: Modifier = Modifier,
) {
    // The check icon is decorative; completion state is exposed to accessibility services
    // through the row's stateDescription instead.
    val stateLabel =
        stringResource(
            if (todo.isCompleted) R.string.detail_todo_completed else R.string.detail_todo_not_completed,
        )
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .semantics { stateDescription = stateLabel },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = if (todo.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint =
                if (todo.isCompleted) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
        )
        Spacer(modifier = Modifier.size(Spacing.sm))
        Text(
            text = todo.title,
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null,
            color =
                if (todo.isCompleted) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    Color.Unspecified
                },
        )
    }
}

@Composable
private fun AlbumRow(
    album: Album,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.PhotoAlbum,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.size(Spacing.sm))
        Text(text = album.title, style = MaterialTheme.typography.bodyLarge)
    }
}

/** Compact placeholder for the activity sections, matching the list screen's skeleton style. */
@Composable
private fun UserContentLoading(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "detail_skeleton_shimmer")
    // As in UserListLoading: pass the State handle down and read it only inside graphicsLayer
    // so the shimmer animates in the draw phase without recomposing the skeleton cards.
    val shimmerAlpha =
        transition.animateFloat(
            initialValue = 0.35f,
            targetValue = 0.85f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = MotionDuration.EXTRA_LONG, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
            label = "detail_skeleton_shimmer_alpha",
        )
    val shimmerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.24f)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        repeat(CONTENT_SKELETON_CARD_COUNT) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    ShimmerLine(width = 0.5f, color = shimmerColor, alpha = shimmerAlpha)
                    ShimmerLine(width = 0.8f, color = shimmerColor, alpha = shimmerAlpha)
                }
            }
        }
    }
}

@Composable
private fun ShimmerLine(
    width: Float,
    color: Color,
    alpha: State<Float>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth(fraction = width)
                .height(12.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .graphicsLayer { this.alpha = alpha.value }
                .background(color),
    )
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
            modifier =
                Modifier
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
        modifier =
            modifier
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

/** A single-line tappable action inside an [InfoCard], e.g. "Add to contacts". */
@Composable
private fun ActionRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .clickable(onClick = onClick)
                .padding(vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.size(Spacing.sm))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
