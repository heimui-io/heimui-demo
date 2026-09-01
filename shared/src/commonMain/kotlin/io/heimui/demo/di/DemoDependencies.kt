package io.heimui.demo.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.heimui.core.HeimUI
import io.heimui.core.data.datasource.local.DriverBackedHeimCacheDataSource
import io.heimui.core.data.datasource.remote.HeimAuthContext
import io.heimui.core.data.datasource.remote.HeimAuthTokenProvider
import io.heimui.core.di.HeimConfig
import io.heimui.demo.data.session.SettingsStorageDriver
import io.heimui.demo.devtools.SduiSourceInspector
import io.heimui.demo.data.StaticDemoCatalogRepository
import io.heimui.demo.data.session.InMemoryDemoSession
import io.heimui.demo.domain.session.DemoSession
import io.heimui.demo.devtools.DemoTelemetryObserver
import io.heimui.demo.domain.repository.DemoCatalogRepository

/**
 * Composition root: the one place that decides which implementation satisfies which contract.
 *
 * Everything downstream depends on interfaces, so pointing the demo at a real backend means
 * changing this file and nothing else. A real app would use Koin or Hilt here — this stays
 * hand-rolled because a showcase should be readable without knowing a DI framework first.
 */
class DemoDependencies(
    val catalog: DemoCatalogRepository = StaticDemoCatalogRepository(),
    val sourceInspector: SduiSourceInspector = SduiSourceInspector(),
    val session: DemoSession = InMemoryDemoSession(),
    val telemetry: DemoTelemetryObserver = DemoTelemetryObserver(),
) {
    /**
     * Flips the session between signed in and signed out.
     *
     * Exists so the showcase can reach the signed-out path: `RequireSessionInterceptor` refuses a
     * submission without a token, and an interceptor nobody can trigger is untestable by hand.
     */
    fun toggleSession() {
        (session as? InMemoryDemoSession)?.toggle()
    }

    /**
     * Initialises the SDK once per process.
     *
     * Screens resolve to `{baseUrl}/screens/{screenId}`, which is exactly how the static files
     * are laid out on GitHub — so the demo goes through the SDK's real repository and exercises
     * its cache, ETag revalidation, stale-while-revalidate, timeouts and circuit breaker instead
     * of a hand-rolled fetcher that would demonstrate none of them.
     */
    fun initializeSdk() {
        if (!HeimUI.isInitialized) {
            HeimUI.initialize(
                HeimConfig(
                    baseUrl = StaticDemoCatalogRepository.sduiBaseUrl,

                    // Screens and submissions are authenticated differently, which is the normal
                    // shape once payloads live on a CDN and writes go to your own API.
                    //
                    // Sending the token to raw.githubusercontent.com would break every screen:
                    // that host answers 404 — not 401 — to an Authorization header it cannot
                    // validate, so it never reveals whether a private repo exists. Beyond
                    // breaking the demo, handing a credential to a third-party CDN is simply
                    // wrong. Submissions go to our own endpoint and carry it.
                    //
                    // Called per request, so a rotated token needs no re-init. HeimUI never
                    // stores or refreshes it — token lifecycle stays the app's job.
                    authTokenProvider = HeimAuthTokenProvider { context ->
                        when (context) {
                            is HeimAuthContext.ScreenFetch -> null
                            is HeimAuthContext.FormSubmit -> session.authHeader()
                        }
                    },

                    // A submission carries the auth header, so the SDK refuses any host that is
                    // not the configured origin unless it is listed here. This is what stops a
                    // malicious payload from posting the user's token to a third party.
                    allowedSubmitHosts = setOf("httpbin.org"),

                    // Without a driver the cache is in-memory and dies with the process: every
                    // cold start refetches and there is no offline behaviour to show.
                    customCacheDataSource = DriverBackedHeimCacheDataSource(
                        driver = SettingsStorageDriver()
                    ),
                )
            )
        }
    }
}

@Composable
fun rememberDemoDependencies(): DemoDependencies =
    remember { DemoDependencies().apply { initializeSdk() } }
