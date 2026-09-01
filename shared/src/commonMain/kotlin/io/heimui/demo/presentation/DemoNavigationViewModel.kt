package io.heimui.demo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.heimui.core.domain.model.action.DismissAction
import io.heimui.core.domain.model.action.HeimAction
import io.heimui.core.domain.model.action.NavigateAction
import io.heimui.core.domain.model.action.OpenUrlAction
import io.heimui.demo.domain.model.DemoDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Owns app navigation and translates HeimUI actions into destinations.
 *
 * ### Why a ViewModel here, when the SDK deliberately avoids one
 * `heimui-core` does **not** require a ViewModel: it is a library, and forcing a
 * `ViewModelStoreOwner` on every consumer would impose a lifecycle dependency that is awkward on
 * iOS. An **app** has no such constraint, and here a ViewModel is exactly right — this state must
 * outlive configuration changes and be testable without a Compose harness. Same reasoning,
 * opposite conclusion, because the constraints differ.
 *
 * ### Why navigation lives in the app, not the SDK
 * HeimUI never navigates on its own. It dispatches [NavigateAction] and the host decides what
 * that means, because only the host knows its own navigation graph. That translation is the
 * single most important integration seam, and it is [onHeimAction].
 *
 * ### Why there is a back stack
 * A single "current destination" was not enough. Going back from a product detail returned to the
 * hub rather than to the catalog it was opened from — the app had no memory of how it got there.
 * A real integration would use Navigation-Compose and inherit this for free; the stack here is
 * hand-rolled so the showcase stays readable without a navigation library in the way.
 */
class DemoNavigationViewModel : ViewModel() {

    private val _backStack = MutableStateFlow<List<DemoDestination>>(listOf(DemoDestination.Splash))

    /** The whole stack, exposed for tests and for anything that needs the history. */
    val backStack: StateFlow<List<DemoDestination>> = _backStack.asStateFlow()

    /** The destination currently on screen: the top of the stack. */
    val destination: StateFlow<DemoDestination> = _backStack
        .map { it.last() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, DemoDestination.Splash)

    /** True when there is somewhere to go back to. */
    val canNavigateBack: Boolean get() = _backStack.value.size > 1

    fun onSplashFinished() {
        // The splash is replaced, not stacked: nobody should be able to go back to it.
        _backStack.value = listOf(DemoDestination.Hub)
    }

    /**
     * Pops one level. Falls back to the hub when the stack is exhausted, so the app can never
     * end up with nothing on screen.
     */
    fun onBack() {
        _backStack.update { stack ->
            if (stack.size > 1) stack.dropLast(1) else listOf(DemoDestination.Hub)
        }
    }

    /**
     * Routes an action dispatched by a server-driven screen.
     *
     * Note what is *not* here: the SDK already executed `submit_form`, `open_url`, dialogs and
     * bottom sheets before this ran. The host only handles what it alone can decide — where to go.
     */
    fun onHeimAction(action: HeimAction) {
        when (action) {
            is NavigateAction -> navigateTo(action.screenId, action.params)

            // "Close this screen." The payload cannot know what is underneath, which is exactly
            // why the SDK forwards it instead of acting: only the host owns the stack.
            is DismissAction -> onBack()

            // Deep links are a second, equivalent way for a payload to request navigation. The
            // SDK refuses to hand an unknown scheme to the platform launcher — that is the
            // defence against intent:// and file:// — but still forwards the action so the app
            // can interpret its own links.
            // Handled by the app's HeimUrlLauncher before it reaches here — see [onDeepLink].
            is OpenUrlAction -> Unit

            else -> Unit
        }
    }

    /**
     * Interprets a destination sent by the server.
     *
     * `NavigateAction.screenId` is a *host-interpreted* destination, not a file the SDK fetches:
     * HeimUI hands it over and stops. This app reads it two ways, and the convention is the
     * app's own choice — a different integrator would map it to their own routes.
     *
     * - `"ecommerce"` → a vertical (a tabbed group). No file extension.
     * - `"ecommerce/product_detail.json"` → an actual screen, carrying [params] to the backend.
     */
    private fun navigateTo(destination: String, params: Map<String, String>) {
        if (destination.isBlank()) return
        val next = if (destination.endsWith(SCREEN_SUFFIX)) {
            DemoDestination.Detail(screenId = destination, params = params)
        } else {
            DemoDestination.Vertical(verticalId = destination)
        }
        // Re-navigating to the destination already on top is a no-op rather than a duplicate
        // entry, so a double tap does not need two presses of back to undo.
        if (_backStack.value.lastOrNull() != next) {
            _backStack.update { it + next }
        }
    }


    /**
     * Routes a deep link the app's own URL launcher claimed.
     *
     * Called instead of letting the platform open it: `heimui://showcase/fintech` handed to the
     * OS would leave the app and come back through a cold start, losing the back stack. The
     * launcher returns `true` to say it was handled, and this is where "handled" happens.
     */
    fun onDeepLink(url: String) {
        url.takeIf { it.startsWith(DEEP_LINK_PREFIX) }
            ?.removePrefix(DEEP_LINK_PREFIX)
            ?.let { navigateTo(it, emptyMap()) }
    }

    private companion object {
        const val DEEP_LINK_PREFIX = "heimui://showcase/"
        const val SCREEN_SUFFIX = ".json"
    }
}
