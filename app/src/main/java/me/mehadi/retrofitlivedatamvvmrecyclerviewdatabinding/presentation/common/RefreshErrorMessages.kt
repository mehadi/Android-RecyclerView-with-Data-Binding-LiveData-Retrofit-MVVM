package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.common

import androidx.annotation.StringRes
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.R
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.RefreshError

/**
 * Maps a refresh failure to the user-facing message resource for it. Shared by every
 * ViewModel that surfaces [RefreshError]s so the wording stays consistent across screens.
 */
@StringRes
internal fun Throwable.toMessageRes(): Int =
    when (this) {
        is RefreshError.NoConnection -> R.string.error_no_connection
        is RefreshError.Timeout -> R.string.error_timeout
        is RefreshError.Server -> R.string.error_server
        else -> R.string.error_refresh_generic
    }
