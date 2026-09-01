package io.heimui.demo.integration

import io.heimui.core.presentation.launcher.HeimUrlLauncher
import io.heimui.core.presentation.launcher.HeimUrlPolicy

/**
 * Keeps the app's own deep links inside the app, and lets the SDK police everything else.
 *
 * A payload can request navigation two ways: `navigate`, or an `open_url` carrying the app's own
 * scheme. The second is what a marketing team writes when the same link has to work from an email
 * and from inside the app. Handing `heimui://showcase/fintech` to the platform would bounce out
 * to the OS and back in through a cold start, losing the back stack on the way — so it is claimed
 * here instead and the host's navigation handles it.
 *
 * Returning `true` tells the SDK the URL was dealt with. The action is still forwarded to
 * `onAction` afterwards, which is where [io.heimui.demo.presentation.DemoNavigationViewModel]
 * turns it into a destination.
 *
 * Everything else goes to [delegate], which enforces [HeimUrlPolicy]. That policy is an
 * allow-list, not a deny-list, and deliberately so: `intent://` on Android can reach unexported
 * components and `file://` discloses local storage, and a deny-list will always miss a scheme
 * someone has not thought of yet.
 */
class DemoUrlLauncher(
    private val delegate: HeimUrlLauncher,
    private val onHandledInternally: (String) -> Unit = {},
) : HeimUrlLauncher {

    override fun openUrl(url: String): Boolean {
        if (url.startsWith(INTERNAL_SCHEME_PREFIX)) {
            onHandledInternally(url)
            return true
        }
        return delegate.openUrl(url)
    }

    companion object {
        const val INTERNAL_SCHEME_PREFIX: String = "heimui://"

        /**
         * `https` only for anything leaving the app.
         *
         * The app's own scheme is deliberately **not** listed: it never reaches the platform
         * launcher, because [DemoUrlLauncher] claims it first. Adding it here would let the SDK
         * hand it to the OS whenever this launcher is not installed — which is exactly the
         * cold-start bounce the class exists to avoid.
         */
        val policy: HeimUrlPolicy = HeimUrlPolicy(allowedSchemes = setOf("https"))
    }
}
