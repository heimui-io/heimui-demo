package io.heimui.demo.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.heimui.demo.di.DemoDependencies
import io.heimui.demo.domain.model.DemoDestination
import io.heimui.demo.presentation.DemoNavigationViewModel
import io.heimui.demo.ui.DetailScreen
import io.heimui.demo.ui.HubScreen
import io.heimui.demo.ui.MainVerticalScreen
import io.heimui.demo.ui.SplashScreen

/**
 * Maps destinations to screens.
 *
 * A hand-rolled host rather than Navigation-Compose, deliberately: the point of this demo is the
 * SDUI integration, and a third-party navigation library would put its own concepts between the
 * reader and that. A production app should use one — the seam is [DemoNavigationViewModel], which
 * would not change.
 *
 * The important detail for anyone integrating HeimUI: **navigation is the host's job**. The SDK
 * dispatches `NavigateAction` and stops there, because only the app knows its own graph. That
 * translation happens in [DemoNavigationViewModel.onHeimAction], not here and not in the SDK.
 */
@Composable
fun DemoNavHost(dependencies: DemoDependencies) {
    val navigationViewModel: DemoNavigationViewModel = viewModel { DemoNavigationViewModel() }
    val destination by navigationViewModel.destination.collectAsStateWithLifecycle()

    when (val current = destination) {
        is DemoDestination.Splash ->
            SplashScreen(onFinish = navigationViewModel::onSplashFinished)

        is DemoDestination.Hub -> HubScreen(
            hubScreenId = dependencies.catalog.hubScreenId(),
            onAction = navigationViewModel::onHeimAction,
        )

        is DemoDestination.Detail -> DetailScreen(
            screenId = current.screenId,
            params = current.params,
            onBack = navigationViewModel::onBack,
            onAction = navigationViewModel::onHeimAction,
        )

        is DemoDestination.Vertical -> MainVerticalScreen(
            verticalId = current.verticalId,
            catalog = dependencies.catalog,
            sourceInspector = dependencies.sourceInspector,
            onBack = navigationViewModel::onBack,
            onAction = navigationViewModel::onHeimAction,
        )
    }
}
