package io.heimui.demo

import androidx.compose.runtime.Composable
import io.heimui.demo.designsystem.DemoTheme
import io.heimui.demo.di.rememberDemoDependencies
import androidx.lifecycle.viewmodel.compose.viewModel
import io.heimui.demo.presentation.DemoNavigationViewModel
import io.heimui.demo.presentation.navigation.DemoNavHost

/**
 * Application entry point.
 *
 * Deliberately three lines. Everything it used to do lives somewhere that names it:
 *
 * | Responsibility            | Now lives in                                    |
 * |---------------------------|-------------------------------------------------|
 * | Wiring implementations    | `di/DemoDependencies`                            |
 * | SDK initialisation        | `di/DemoDependencies.initializeSdk()`            |
 * | Palette and brand tokens  | `designsystem/DemoTheme`                         |
 * | Native custom components  | `designsystem/components/`                       |
 * | Navigation state          | `presentation/DemoNavigationViewModel`           |
 * | Destination → screen       | `presentation/navigation/DemoNavHost`            |
 *
 * Not architecture for its own sake: each of those changes for a different reason and is owned by
 * a different person. A designer touching the palette should never open the navigation code.
 */
@Composable
fun App() {
    val dependencies = rememberDemoDependencies()
    val navigationViewModel: DemoNavigationViewModel = viewModel { DemoNavigationViewModel() }

    DemoTheme(
        telemetryObserver = dependencies.telemetry,
        // Lets the action pipeline refuse a submission while signed out, before it reaches the
        // network rather than after.
        session = dependencies.session,
        // `heimui://` links are claimed by the app's launcher and routed here, instead of being
        // handed to the OS and re-entering through a cold start.
        onDeepLink = navigationViewModel::onDeepLink,
    ) {
        DemoNavHost(
            dependencies = dependencies,
            navigationViewModel = navigationViewModel,
        )
    }
}
