package io.heimui.demo.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.heimui.core.domain.evaluator.HeimValidatorRegistry
import io.heimui.core.presentation.designsystem.HeimBrandTokens
import androidx.compose.ui.platform.LocalUriHandler
import io.heimui.core.presentation.action.HeimActionDispatcher
import io.heimui.core.presentation.designsystem.HeimIconProvider
import io.heimui.core.presentation.state.DriverBackedFormDraftStorage
import io.heimui.core.presentation.launcher.ComposeUriUrlLauncher
import io.heimui.core.presentation.telemetry.HeimTelemetryEvent
import io.heimui.demo.data.session.SettingsStorageDriver
import io.heimui.demo.designsystem.tokens.DemoShapes
import io.heimui.demo.domain.session.DemoSession
import io.heimui.demo.integration.DemoImageLoader
import io.heimui.demo.integration.DemoModalPresenter
import io.heimui.demo.integration.DemoUrlLauncher
import io.heimui.demo.integration.RequireSessionInterceptor
import io.heimui.core.presentation.designsystem.HeimTheme
import io.heimui.core.presentation.registry.HeimCustomComponentRegistry
import io.heimui.core.presentation.telemetry.HeimTelemetryObserver
import io.heimui.core.presentation.telemetry.NoOpHeimTelemetryObserver
import io.heimui.demo.designsystem.components.StockChartCard
import io.heimui.demo.designsystem.tokens.DemoColors
import io.heimui.demo.designsystem.tokens.DemoTypography

/**
 * Where the app tells the SDK what it looks like and how it behaves.
 *
 * This file is the whole of Level 1 and Level 2 of the extension model, in one place on purpose:
 * an integrator should be able to read one screenful and know every seam they own.
 *
 * ### Level 1 — tokens (no code, changes everything)
 * | Passed to `HeimTheme`          | What the payload does with it                        |
 * |--------------------------------|------------------------------------------------------|
 * | `colorScheme` / `typography`   | Names Material roles — `primary`, `titleLarge`        |
 * | `shapes`                       | Corner radii for cards, sheets, buttons               |
 * | [HeimBrandTokens]              | Names your design system has and Material does not    |
 *
 * ### Level 2 — providers (replace a behaviour, keep the components)
 * | Provider                       | What it decides here                                  |
 * |--------------------------------|-------------------------------------------------------|
 * | [HeimIconProvider]             | A payload names an icon, you supply the glyph          |
 * | [HeimImageLoader]              | Ask the CDN for the size actually drawn                |
 * | [HeimUrlLauncher] + policy     | Claim `heimui://` in-app; https-only for the rest      |
 * | [HeimModalPresenter]           | Dialogs and sheets in the app's shape                  |
 * | [HeimFormDraftStorage]         | A half-filled KYC survives process death               |
 * | [HeimActionDispatcher]         | Gate a submission before it reaches the network        |
 * | [HeimValidatorRegistry]        | Validators a payload names in a `CUSTOM` rule          |
 * | [HeimTelemetryObserver]        | Where SDK events go — see the `</>` panel              |
 *
 * ### Level 3 — custom components
 * [HeimCustomComponentRegistry] places your own composables by name: the server writes
 * `{"type": "custom", "name": "stock_chart"}` and gets a native chart the SDK has never heard of.
 *
 * ### Level 4 — the data layer
 * Not wired here, because it replaces the SDK's networking rather than configuring it. See
 * `OfflineRepositoryTest` for a `HeimScreenRepository` serving fixtures with no socket opened.
 *
 * The pattern underneath all of it: **a payload should never contain a hex colour, a glyph, or a
 * URL scheme decision.** It names a role and this file decides what the role means — which is
 * what makes a rebrand a client release rather than a migration across every JSON on the server.
 */
@Composable
fun DemoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    iconProvider: HeimIconProvider = MaterialHeimIconProvider,
    telemetryObserver: HeimTelemetryObserver = NoOpHeimTelemetryObserver,
    session: DemoSession? = null,
    onDeepLink: (String) -> Unit = {},
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

    // Ask the CDN for the size the image is drawn at instead of downloading the designer's hero
    // and scaling it down on the device that can least afford it.
    val imageLoader = remember { DemoImageLoader() }

    val modalPresenter = remember { DemoModalPresenter() }

    // Claims `heimui://` before it can bounce out to the OS and back through a cold start.
    val uriHandler = LocalUriHandler.current
    val urlLauncher = remember(uriHandler, telemetryObserver, onDeepLink) {
        DemoUrlLauncher(
            delegate = ComposeUriUrlLauncher(
                uriHandler = uriHandler,
                policy = DemoUrlLauncher.policy,
                onRefused = { url, reason ->
                    telemetryObserver.onEvent(HeimTelemetryEvent.UrlBlocked(url, reason))
                },
            ),
            onHandledInternally = onDeepLink,
        )
    }

    // Drafts outlive the process, so a KYC form interrupted by a phone call is still filled in
    // when the user comes back. Without a persistent driver this is an expensive no-op.
    val formDraftStorage = remember {
        DriverBackedFormDraftStorage(driver = SettingsStorageDriver())
    }

    // Runs before an action does, so a gate here stops the request rather than reporting it after
    // the fact.
    val actionDispatcher = remember(session, telemetryObserver) {
        HeimActionDispatcher.build {
            if (session != null) {
                addInterceptor(
                    RequireSessionInterceptor(
                        session = session,
                        onBlocked = { _, reason ->
                            telemetryObserver.onEvent(
                                HeimTelemetryEvent.SubmissionBlocked(
                                    endpoint = "(interceptor)",
                                    reason = reason,
                                )
                            )
                        },
                    )
                )
            }
        }
    }

    HeimTheme(
        darkTheme = darkTheme,
        colorScheme = if (darkTheme) DemoColors.darkScheme else DemoColors.lightScheme,
        iconProvider = iconProvider,
        typography = DemoTypography.scale,
        shapes = DemoShapes.shapes,
        brandTokens = brandTokens,
        imageLoader = imageLoader,
        modalPresenter = modalPresenter,
        urlLauncher = urlLauncher,
        urlPolicy = DemoUrlLauncher.policy,
        formDraftStorage = formDraftStorage,
        actionDispatcher = actionDispatcher,
        customComponentRegistry = componentRegistry,
        validatorRegistry = validatorRegistry,
        telemetryObserver = telemetryObserver,
        content = content,
    )
}
