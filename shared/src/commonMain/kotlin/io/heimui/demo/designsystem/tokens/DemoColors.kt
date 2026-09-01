package io.heimui.demo.designsystem.tokens

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * The app's colour palette.
 *
 * Two layers, and the distinction matters:
 *
 * - **[Palette]** — raw values. Nothing outside this file should reference them.
 * - **Semantic schemes** — what the rest of the app and the SDUI payloads actually use.
 *
 * A payload never names a raw colour. It names a *role* (`"brand_primary"`, `"surface_elevated"`),
 * and this file decides what that role looks like. That indirection is what lets a rebrand happen
 * here instead of across every JSON file on the server.
 */
object DemoColors {

    /** Raw values. Private by intent — reference the semantic roles below. */
    private object Palette {
        val Cyan500 = Color(0xFF00E5FF)
        val Cyan700 = Color(0xFF00B8D4)
        val Violet500 = Color(0xFFA855F7)
        val Ink900 = Color(0xFF0B0F19)
        val Ink800 = Color(0xFF161D2F)
        val Ink700 = Color(0xFF1E293B)
        val Slate400 = Color(0xFF94A3B8)
        val Slate200 = Color(0xFFE2E8F0)
        val White = Color(0xFFFFFFFF)
        val Emerald500 = Color(0xFF10B981)
        val Amber500 = Color(0xFFF59E0B)
        val Rose500 = Color(0xFFF43F5E)
    }

    val darkScheme = darkColorScheme(
        primary = Palette.Cyan500,
        onPrimary = Palette.Ink900,
        secondary = Palette.Violet500,
        onSecondary = Palette.White,
        background = Palette.Ink900,
        onBackground = Palette.White,
        surface = Palette.Ink800,
        onSurface = Palette.White,
        surfaceVariant = Palette.Ink700,
        onSurfaceVariant = Palette.Slate400,
        error = Palette.Rose500,
        onError = Palette.White,
        outline = Palette.Slate400,
        outlineVariant = Palette.Ink700,
    )

    val lightScheme = lightColorScheme(
        primary = Palette.Cyan700,
        secondary = Palette.Violet500,
        background = Palette.White,
        surface = Palette.Slate200,
        onSurface = Palette.Ink900,
    )

    /**
     * Roles a server payload may reference by name.
     *
     * Registered with the SDK as brand tokens, so JSON can say
     * `"background_color": "surface_elevated"` and get whatever this app decides that means.
     * Without registering them a payload can only use Material's own token names or raw hex —
     * and hex in a payload is how a brand ends up hardcoded on a server you do not control.
     */
    val brandRoles: Map<String, Color> = mapOf(
        "brand_primary" to Palette.Cyan500,
        "brand_secondary" to Palette.Violet500,
        "surface_base" to Palette.Ink900,
        "surface_elevated" to Palette.Ink800,
        "surface_overlay" to Palette.Ink700,
        "text_primary" to Palette.White,
        "text_secondary" to Palette.Slate400,
        "status_success" to Palette.Emerald500,
        "status_warning" to Palette.Amber500,
        "status_danger" to Palette.Rose500,
    )
}
