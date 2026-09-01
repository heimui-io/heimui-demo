package io.heimui.demo.integration

import io.heimui.core.domain.model.action.HeimAction
import io.heimui.core.domain.model.action.SubmitFormAction
import io.heimui.core.presentation.action.HeimActionInterceptor
import io.heimui.core.presentation.state.HeimStateManager
import io.heimui.demo.domain.session.DemoSession

/**
 * Refuses to submit a form while the user is signed out.
 *
 * The check belongs here rather than in the payload. A server-driven screen cannot know whether
 * this device still holds a valid session — the token may have expired since the JSON was
 * authored, and `visible_if` evaluates against form state, not against auth. Putting the gate in
 * an interceptor means every screen inherits it, including screens written after this code.
 *
 * Not calling `next` stops the chain: the request never reaches the network. That is the point of
 * a chain-of-responsibility here rather than a check inside one screen's handler.
 */
class RequireSessionInterceptor(
    private val session: DemoSession,
    private val onBlocked: (HeimAction, String) -> Unit,
) : HeimActionInterceptor {

    override suspend fun intercept(
        action: HeimAction,
        stateManager: HeimStateManager,
        next: suspend (HeimAction) -> Unit,
    ) {
        if (action is SubmitFormAction && session.authHeader().isNullOrBlank()) {
            onBlocked(action, "signed out")
            return
        }
        next(action)
    }
}

/**
 * Records every action before it runs.
 *
 * Distinct from the SDK's own `ActionExecuted` telemetry, which fires *after* the action has been
 * handled. An interceptor sees actions that never complete — the ones a gate above stopped, or
 * that threw — and that gap is usually the interesting one when a funnel drops.
 */
class AnalyticsInterceptor(
    private val onAction: (actionType: String) -> Unit,
) : HeimActionInterceptor {

    override suspend fun intercept(
        action: HeimAction,
        stateManager: HeimStateManager,
        next: suspend (HeimAction) -> Unit,
    ) {
        onAction(action::class.simpleName ?: "unknown")
        next(action)
    }
}
