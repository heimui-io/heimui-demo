package io.heimui.demo.data.session

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

import io.heimui.demo.domain.session.DemoSession
import kotlin.concurrent.Volatile

/**
 * Session held in memory, seeded with a fake token so the showcase has something to send.
 *
 * A real app reads this from encrypted storage and refreshes it. What matters for the demo is the
 * *shape*: HeimUI never stores or refreshes tokens — it calls [authHeader] on every request and
 * uses whatever comes back. Token lifecycle stays entirely the app's business.
 */
class InMemoryDemoSession(
    initialToken: String? = DEMO_TOKEN,
) : DemoSession {

    private val _token = MutableStateFlow(initialToken)

    override val isSignedIn: StateFlow<Boolean> = _token
        .map { !it.isNullOrBlank() }
        .stateIn(
            // `GlobalScope` because the session outlives every screen and every ViewModel: it is
            // process-scoped state, and tying it to a screen's scope would end the flow the first
            // time the user navigated away. A real app scopes this to its application graph.
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
            started = SharingStarted.Eagerly,
            initialValue = !initialToken.isNullOrBlank(),
        )

    override fun authHeader(): String? = _token.value?.let { "Bearer $it" }

    override fun signIn(token: String) {
        _token.value = token
    }

    override fun signOut() {
        _token.value = null
    }

    /** Flips between the two states, so the showcase can demonstrate the signed-out path. */
    fun toggle() {
        if (isSignedIn.value) signOut() else signIn(DEMO_TOKEN)
    }

    private companion object {
        /**
         * Stand-in for a real credential.
         *
         * Safe to seed because the app authenticates **submissions only** — see
         * `DemoDependencies`. Sending it to raw.githubusercontent.com would break every screen:
         * that host answers 404, not 401, to an Authorization header it cannot validate, so it
         * never reveals whether a private repo exists.
         */
        const val DEMO_TOKEN = "demo-session-token-not-a-real-credential"
    }
}
