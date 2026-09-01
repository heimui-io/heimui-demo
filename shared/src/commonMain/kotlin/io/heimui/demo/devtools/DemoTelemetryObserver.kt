package io.heimui.demo.devtools

import io.heimui.core.presentation.telemetry.HeimTelemetryEvent
import io.heimui.core.presentation.telemetry.HeimTelemetryObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Collects HeimUI telemetry so the showcase can display it.
 *
 * In a real app these events go to Crashlytics, Datadog or your analytics pipeline. Two of them
 * matter more than the rest and are the reason this is worth wiring on day one:
 *
 * - **[HeimTelemetryEvent.PayloadViolation]** — the client had to repair the payload it received:
 *   a clamped dimension, a duplicate id, an unknown component type. This is how a backend team
 *   learns it is emitting invalid SDUI *before* users report a broken screen.
 * - **[HeimTelemetryEvent.ScreenRefreshFailed]** — revalidation failed but cached content is
 *   still on screen. Nobody is blocked, so it should not page anyone, but it is the early signal
 *   that the network or the backend is degrading.
 */
class DemoTelemetryObserver : HeimTelemetryObserver {

    data class Entry(val label: String, val detail: String, val isWarning: Boolean)

    private val _events = MutableStateFlow<List<Entry>>(emptyList())
    val events: StateFlow<List<Entry>> = _events.asStateFlow()

    override fun onEvent(event: HeimTelemetryEvent) {
        val entry = when (event) {
            is HeimTelemetryEvent.ScreenViewed ->
                Entry("screen", "${event.screenId}${if (event.isStale) " (cache)" else ""}", false)

            is HeimTelemetryEvent.ActionExecuted ->
                Entry("action", "${event.actionType} · ${event.screenId}", false)

            is HeimTelemetryEvent.FormSubmitted ->
                Entry("submit", "${event.endpoint} · ${event.durationMs}ms", !event.success)

            is HeimTelemetryEvent.PayloadViolation ->
                Entry("payload", event.violations.joinToString("; "), true)

            is HeimTelemetryEvent.ScreenRefreshFailed ->
                Entry("stale", "${event.screenId}: ${event.reason}", true)

            is HeimTelemetryEvent.ScreenError ->
                Entry("error", "${event.screenId}: ${event.errorMessage}", true)

            is HeimTelemetryEvent.SubmissionBlocked ->
                Entry("blocked", "${event.endpoint}: ${event.reason}", true)

            is HeimTelemetryEvent.UrlBlocked ->
                Entry("url", "${event.url}: ${event.reason}", true)

            is HeimTelemetryEvent.ValidatorMissing ->
                Entry("validator", "unregistered: ${event.validatorName}", true)

            is HeimTelemetryEvent.IconMissing ->
                Entry("icon", "no glyph: ${event.iconName}", true)

            is HeimTelemetryEvent.TimeToRender ->
                Entry("render", "${event.screenId} · ${event.durationMs}ms", false)
        }
        record(entry)
    }

    /**
     * Records a business analytics event the payload declared.
     *
     * Deliberately a separate entry point from [onEvent]: SDK telemetry and product tracking are
     * different things with different owners, and collapsing them would hide which is which. In a
     * real app this method is where Amplitude or Segment would be called instead.
     */
    fun recordTracking(payload: Map<String, String>) {
        record(
            Entry(
                label = "track",
                detail = payload.entries.joinToString(" · ") { "${it.key}=${it.value}" },
                isWarning = false,
            )
        )
    }

    // Newest first, bounded: this is a debug panel, not a log store.
    private fun record(entry: Entry) {
        _events.update { (listOf(entry) + it).take(MAX_ENTRIES) }
    }

    private companion object {
        const val MAX_ENTRIES = 60
    }
}
