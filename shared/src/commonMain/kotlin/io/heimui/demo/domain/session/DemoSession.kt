package io.heimui.demo.domain.session

/**
 * The user's session.
 *
 * Every real integration needs this: HeimUI attaches the token to each screen request and each
 * form submission, so screens can be personalised and submissions authenticated. It is the first
 * thing an integrator must wire and the demo had no example of it.
 */
interface DemoSession {
    /**
     * Current auth header value, including its scheme (`"Bearer eyJ…"`), or null when signed out.
     *
     * Read on every request rather than captured once, so a rotated token takes effect without
     * reinitialising the SDK.
     */
    fun authHeader(): String?

    fun signIn(token: String)
    fun signOut()
}
