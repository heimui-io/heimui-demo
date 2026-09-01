package io.heimui.demo

import androidx.compose.runtime.Composable
import io.heimui.demo.designsystem.DemoTheme
import io.heimui.demo.di.rememberDemoDependencies
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

    DemoTheme(telemetryObserver = dependencies.telemetry) {
        DemoNavHost(dependencies = dependencies)
    }
}
