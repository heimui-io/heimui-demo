package io.heimui.demo.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.heimui.core.domain.evaluator.HeimValidatorRegistry
import io.heimui.core.presentation.designsystem.HeimBrandTokens
import io.heimui.core.presentation.designsystem.HeimIconProvider
import io.heimui.core.presentation.designsystem.HeimTheme
import io.heimui.core.presentation.registry.HeimCustomComponentRegistry
import io.heimui.core.presentation.telemetry.HeimTelemetryObserver
import io.heimui.core.presentation.telemetry.NoOpHeimTelemetryObserver
import io.heimui.demo.designsystem.components.StockChartCard
import io.heimui.demo.designsystem.tokens.DemoColors
import io.heimui.demo.designsystem.tokens.DemoShapes
import io.heimui.demo.designsystem.tokens.DemoTypography

/**
 * The app's design system, and the single place where it is handed to the SDK.
 *
 * Worth reading as the template for a real integration, because it answers the question every
 * team asks first: *how does my brand reach screens I do not control?*
 *
 * | Extension point                | What a payload can then do                       |
 * |--------------------------------|--------------------------------------------------|
 * | `colorScheme` / `typography`   | Use Material role names — `primary`, `titleLarge` |
 * | [HeimBrandTokens]              | Use *your* names — `brand_primary`, `price`       |
 * | [HeimCustomComponentRegistry]  | Place your native composables by name             |
 * | [HeimValidatorRegistry]        | Reference your validators in a `CUSTOM` rule      |
 * | [HeimIconProvider]             | Name an icon and get *your* icon set              |
 *
 * The pattern that matters: a payload should never contain a hex colour. It names a role, and
 * this file decides what the role looks like. That is what makes a rebrand a client release
 * rather than a migration across every JSON on the server.
 */
@Composable
fun DemoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    iconProvider: HeimIconProvider = MaterialHeimIconProvider,
    telemetryObserver: HeimTelemetryObserver = NoOpHeimTelemetryObserver,
    content: @Composable () -> Unit,
) {
    val brandTokens = remember {
        HeimBrandTokens(
            colors = DemoColors.brandRoles,
            textStyles = DemoTypography.brandStyles,
        )
    }

    val componentRegistry = remember {
        HeimCustomComponentRegistry().apply {
            register("stock_chart") { component, _, _, modifier ->
                StockChartCard(component = component, modifier = modifier)
            }
        }
    }

    // Validators a payload may name in a CUSTOM rule. An unregistered name fails the field
    // closed — silently skipping it would let an older client accept data a newer server rejects.
    val validatorRegistry = remember {
        HeimValidatorRegistry().apply {
            register("COLOMBIAN_NIT") { value, _ ->
                value.filter { it.isDigit() }.length == 9
            }
        }
    }

    HeimTheme(
        darkTheme = darkTheme,
        colorScheme = if (darkTheme) DemoColors.darkScheme else DemoColors.lightScheme,
        iconProvider = iconProvider,
        typography = DemoTypography.scale,
        brandTokens = brandTokens,
        customComponentRegistry = componentRegistry,
        validatorRegistry = validatorRegistry,
        telemetryObserver = telemetryObserver,
        content = content,
    )
}
