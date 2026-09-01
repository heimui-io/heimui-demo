package io.heimui.demo.data.session

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

    @Volatile
    private var token: String? = initialToken

    override fun authHeader(): String? = token?.let { "Bearer $it" }

    override fun signIn(token: String) {
        this.token = token
    }

    override fun signOut() {
        token = null
    }

    private companion object {
        /** Obviously fake: the showcase backend is a static file server and ignores it. */
        const val DEMO_TOKEN = "demo-session-token-not-a-real-credential"
    }
}
