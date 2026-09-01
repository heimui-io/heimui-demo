package io.heimui.demo.designsystem.tokens

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * The app's type scale.
 *
 * Overriding the Material scale rather than accepting the default is what makes a payload's
 * `"style": "titleLarge"` render in *your* brand instead of Google's. The SDK resolves style names
 * against whatever Typography the host installs, so this file is the whole mechanism.
 */
object DemoTypography {

    private val Default = Typography()

    val scale = Typography(
        displayLarge = Default.displayLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = (-1).sp),
        displayMedium = Default.displayMedium.copy(fontWeight = FontWeight.Bold),
        displaySmall = Default.displaySmall.copy(fontWeight = FontWeight.SemiBold),

        headlineLarge = Default.headlineLarge.copy(fontWeight = FontWeight.Bold),
        headlineMedium = Default.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
        headlineSmall = Default.headlineSmall.copy(fontWeight = FontWeight.SemiBold),

        titleLarge = Default.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        titleMedium = Default.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        titleSmall = Default.titleSmall.copy(fontWeight = FontWeight.Medium),

        bodyLarge = Default.bodyLarge.copy(lineHeight = 26.sp),
        bodyMedium = Default.bodyMedium.copy(lineHeight = 22.sp),
        bodySmall = Default.bodySmall.copy(lineHeight = 18.sp, letterSpacing = 0.2.sp),

        labelLarge = Default.labelLarge.copy(fontWeight = FontWeight.SemiBold),
        labelMedium = Default.labelMedium.copy(fontWeight = FontWeight.Medium),
        labelSmall = Default.labelSmall.copy(fontWeight = FontWeight.Medium, letterSpacing = 0.6.sp),
    )

    /**
     * Extra styles a payload can name that Material has no slot for.
     *
     * Registered as brand text tokens, so JSON can say `"style": "price"` and get a treatment the
     * design team owns. Without this, a payload wanting an oversized price has to fake it with a
     * display style that means something else.
     */
    val brandStyles: Map<String, TextStyle> = mapOf(
        "price" to Default.headlineMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp),
        "metric" to Default.displaySmall.copy(fontWeight = FontWeight.Bold),
        "caption_strong" to Default.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
        "legal" to Default.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
    )
}
