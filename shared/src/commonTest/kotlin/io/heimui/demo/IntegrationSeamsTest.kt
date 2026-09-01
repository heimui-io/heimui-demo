package io.heimui.demo

import io.heimui.core.domain.model.HeimValue
import io.heimui.core.domain.model.action.HeimAction
import io.heimui.core.domain.model.action.NavigateAction
import io.heimui.core.domain.model.action.SubmitFormAction
import io.heimui.core.presentation.launcher.ComposeUriUrlLauncher
import io.heimui.core.presentation.launcher.HeimUrlLauncher
import io.heimui.core.presentation.state.HeimStateManager
import io.heimui.demo.domain.session.DemoSession
import io.heimui.demo.integration.DemoUrlLauncher
import io.heimui.demo.integration.RequireSessionInterceptor
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * The Level 2 seams that have no pixels to look at.
 *
 * A theme change is visible the moment you run the app; an interceptor that refuses a submission
 * and a launcher that claims a scheme are invisible when they work, which is exactly why they
 * need tests rather than a screenshot.
 */
class IntegrationSeamsTest {

    private class FakeSession(private var token: String?) : DemoSession {
        override fun authHeader(): String? = token
        override fun signIn(token: String) { this.token = token }
        override fun signOut() { token = null }
    }

    private fun submitAction() = SubmitFormAction(
        endpoint = "https://httpbin.org/post",
        method = "POST",
        payload = mapOf("legal_name" to HeimValue.Str("{{state.legal_name}}")),
    )

    @Test
    fun `a submission is refused while signed out and never reaches the network`() = runTest {
        var reached = false
        var blockedReason: String? = null
        val interceptor = RequireSessionInterceptor(
            session = FakeSession(token = null),
            onBlocked = { _, reason -> blockedReason = reason },
        )

        interceptor.intercept(submitAction(), HeimStateManager(screenId = "kyc")) { reached = true }

        // Not calling `next` is what stops the chain — the request is never built, let alone sent.
        assertFalse(reached)
        assertEquals("signed out", blockedReason)
    }

    @Test
    fun `a signed in user submits normally`() = runTest {
        var reached = false
        val interceptor = RequireSessionInterceptor(
            session = FakeSession(token = "Bearer real"),
            onBlocked = { _, _ -> },
        )

        interceptor.intercept(submitAction(), HeimStateManager(screenId = "kyc")) { reached = true }

        assertTrue(reached)
    }

    @Test
    fun `the gate applies only to submissions`() = runTest {
        var reached = false
        val interceptor = RequireSessionInterceptor(
            session = FakeSession(token = null),
            onBlocked = { _, _ -> },
        )

        // Navigating while signed out is normal — a paywall or a login screen is itself a
        // destination. Gating every action would trap the user with no way forward.
        interceptor.intercept(
            NavigateAction(screenId = "paywall"),
            HeimStateManager(screenId = "hub"),
        ) { reached = true }

        assertTrue(reached)
    }

    private class RecordingLauncher : HeimUrlLauncher {
        val opened = mutableListOf<String>()
        override fun openUrl(url: String): Boolean {
            opened += url
            return true
        }
    }

    @Test
    fun `the app scheme is claimed in-app and never handed to the platform`() {
        val delegate = RecordingLauncher()
        val handled = mutableListOf<String>()
        val launcher = DemoUrlLauncher(delegate = delegate, onHandledInternally = { handled += it })

        assertTrue(launcher.openUrl("heimui://showcase/fintech"))

        // Handing this to the OS would leave the app and re-enter through a cold start, losing
        // the back stack on the way.
        assertEquals(listOf("heimui://showcase/fintech"), handled)
        assertTrue(delegate.opened.isEmpty())
    }

    @Test
    fun `everything else goes to the platform launcher under the scheme policy`() {
        val delegate = RecordingLauncher()
        val handled = mutableListOf<String>()
        val launcher = DemoUrlLauncher(delegate = delegate, onHandledInternally = { handled += it })

        launcher.openUrl("https://heimui.io/terms")

        assertEquals(listOf("https://heimui.io/terms"), delegate.opened)
        assertTrue(handled.isEmpty())
    }

    @Test
    fun `the scheme policy is an allow-list so an unforeseen scheme is refused`() {
        val policy = DemoUrlLauncher.policy

        assertTrue(policy.isAllowed("https://heimui.io"))

        // The classics: intent:// reaches unexported components on Android, file:// and
        // content:// disclose local storage, javascript: runs in whatever renders it. A deny-list
        // would have to enumerate these and would still miss the next one.
        assertFalse(policy.isAllowed("intent://scan/#Intent;scheme=zxing;end"))
        assertFalse(policy.isAllowed("file:///data/data/io.heimui.demo/databases/app.db"))
        assertFalse(policy.isAllowed("content://com.android.contacts/contacts"))
        assertFalse(policy.isAllowed("javascript:alert(1)"))
        assertFalse(policy.isAllowed("http://heimui.io"))

        // The app's own scheme is not in the policy either: DemoUrlLauncher claims it before the
        // policy is ever consulted, so listing it would only matter if that launcher were absent.
        assertFalse(policy.isAllowed("heimui://showcase/fintech"))
    }
}
