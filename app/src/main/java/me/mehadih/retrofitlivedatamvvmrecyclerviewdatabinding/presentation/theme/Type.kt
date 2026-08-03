package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight

/**
 * Roboto (the platform default) is the recommended MD3 font pairing for Android, so no custom
 * font resources are bundled — only weight emphasis is tuned on top of the default M3 type scale.
 */
private val defaults = Typography()

val AppTypography = Typography(
    headlineMedium = defaults.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
    titleLarge = defaults.titleLarge.copy(fontWeight = FontWeight.SemiBold),
    titleMedium = defaults.titleMedium.copy(fontWeight = FontWeight.Medium),
    labelLarge = defaults.labelLarge.copy(fontWeight = FontWeight.Medium),
)
