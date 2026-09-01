package io.heimui.demo.designsystem

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.size
import io.heimui.core.presentation.designsystem.HeimIconProvider

/**
 * Draws icons named by the server, using Material Symbols.
 *
 * The SDK ships a small hand-drawn vector set so it carries no icon dependency of its own, but it
 * only knows a dozen generic names — anything else falls back to a neutral glyph and emits an
 * `IconMissing` telemetry event. That is why the showcase rendered "i" circles where a cart, a
 * bank and a restaurant were meant to be.
 *
 * Installing a provider is the fix, and it is also the point: **the icon vocabulary belongs to the
 * app, not the SDK**. A payload names an icon; the app decides what that name draws. The same
 * `"shopping_cart"` can be a Material symbol here and a brand asset elsewhere, with no server
 * change and no SDK release.
 *
 * Names follow the Material Symbols catalogue so a designer can pick from a public list rather
 * than an SDK-specific one. [vectorFor] exposes the same vocabulary to native app chrome, so the
 * bottom bar and a server-driven list can never drift into two different sets of icons.
 */
object MaterialHeimIconProvider : HeimIconProvider {

    private val icons: Map<String, ImageVector> = mapOf(
        // Verticals, as named by the hub payload.
        "shopping_cart" to Icons.Default.ShoppingCart,
        "account_balance" to Icons.Default.AccountBalance,
        "restaurant" to Icons.Default.Restaurant,
        "diamond" to Icons.Default.Diamond,
        "code" to Icons.Default.Code,
        // Section tabs.
        "local_fire_department" to Icons.Default.LocalFireDepartment,
        "category" to Icons.Default.Category,
        "list_alt" to Icons.AutoMirrored.Filled.ListAlt,
        "account_balance_wallet" to Icons.Default.AccountBalanceWallet,
        "security" to Icons.Default.Security,
        "local_pizza" to Icons.Default.LocalPizza,
        "two_wheeler" to Icons.Default.TwoWheeler,
        "location_on" to Icons.Default.LocationOn,
        "groups" to Icons.Default.Groups,
        "trending_up" to Icons.AutoMirrored.Filled.TrendingUp,
        "extension" to Icons.Default.Extension,
        "palette" to Icons.Default.Palette,
        // Actions.
        "send" to Icons.AutoMirrored.Filled.Send,
        "download" to Icons.Default.Download,
        "link" to Icons.Default.Link,
        // Generic.
        "headphones" to Icons.Default.Headphones,
        "widgets" to Icons.Default.Widgets,
        "bolt" to Icons.Default.Bolt,
    )

    /**
     * Resolves a name to a vector, or `null` when the vocabulary has no entry for it.
     *
     * Native chrome uses this directly; [RenderIcon] is the SDK-facing side of the same map.
     */
    fun vectorFor(name: String): ImageVector? = icons[name.lowercase().trim()]

    @Composable
    override fun RenderIcon(name: String, tint: Color, size: Dp, modifier: Modifier) {
        Icon(
            // An unknown name draws a question mark rather than something plausible: a wrong icon
            // that looks deliberate is far harder to spot than one that says "missing".
            imageVector = vectorFor(name) ?: Icons.AutoMirrored.Filled.HelpOutline,
            contentDescription = null, // Decorative; the payload carries its own accessibility text.
            tint = tint,
            modifier = modifier.size(size),
        )
    }
}
