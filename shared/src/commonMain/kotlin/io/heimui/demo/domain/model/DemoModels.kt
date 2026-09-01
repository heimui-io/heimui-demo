package io.heimui.demo.domain.model

/**
 * A tab inside a vertical. [screenId] is the path the SDK resolves against the SDUI base URL,
 * so it is the only thing that ties this catalog to the remote payloads.
 */
data class DemoTab(
    val title: String,
    val screenId: String,
    val icon: String,
)

/** A showcase vertical — a themed group of screens (e-commerce, fintech, …). */
data class DemoVertical(
    val id: String,
    val title: String,
    val tabs: List<DemoTab>,
)

/**
 * Where the app is.
 *
 * Modelled as a sealed type rather than a string so an unhandled destination is a compile error.
 * Note that this is the *host app's* navigation: HeimUI never navigates on its own, it only
 * dispatches `NavigateAction` and lets the app decide what that means.
 */
sealed interface DemoDestination {
    data object Splash : DemoDestination
    data object Hub : DemoDestination

    /** A showcase vertical: a group of tabbed screens. */
    data class Vertical(val verticalId: String) : DemoDestination

    /**
     * A detail screen fetched with parameters.
     *
     * This is the case that makes the whole model click: the payload says *which* screen and
     * *which entity*, and [params] travel to the backend as query parameters so the server can
     * return the detail for that specific item.
     *
     * ```json
     * { "type": "navigate",
     *   "screen_id": "ecommerce/product_detail.json",
     *   "params": { "product_id": "sku_neural_x1" } }
     * ```
     */
    data class Detail(
        val screenId: String,
        val params: Map<String, String> = emptyMap(),
    ) : DemoDestination
}
