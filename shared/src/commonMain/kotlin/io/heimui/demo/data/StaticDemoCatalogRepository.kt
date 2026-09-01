package io.heimui.demo.data

import io.heimui.demo.domain.model.DemoTab
import io.heimui.demo.domain.model.DemoVertical
import io.heimui.demo.domain.repository.DemoCatalogRepository

/**
 * Catalog backed by a compiled-in list.
 *
 * This is the only place in the app that knows screen ids. They are paths relative to the SDUI
 * root, so the SDK's own repository resolves them with no custom networking:
 *
 * ```
 * {baseUrl}/screens/{screenId}
 *   → https://raw.githubusercontent.com/.../main/sdui/screens/hub/hub_screen.json
 * ```
 *
 * Adding a screen is a JSON file plus one line here.
 */
class StaticDemoCatalogRepository : DemoCatalogRepository {

    override fun hubScreenId(): String = HUB_SCREEN_ID

    override suspend fun verticals(): List<DemoVertical> = VERTICALS

    override suspend fun verticalById(id: String): DemoVertical? =
        VERTICALS.firstOrNull { it.id == id }

    companion object {
        /**
         * Root of the static SDUI files.
         *
         * GitHub raw serves an `ETag` and honours `If-None-Match`, so pointing the SDK here
         * exercises the real revalidation path (304 → serve cache) instead of refetching on
         * every open.
         *
         * To iterate locally: serve the `sdui/` folder with `python3 -m http.server 8080` and
         * override this with `http://10.0.2.2:8080` from the Android emulator.
         */
        var sduiBaseUrl: String =
            "https://raw.githubusercontent.com/heimui-io/heimui-demo/main/sdui"

        const val HUB_SCREEN_ID: String = "hub/hub_screen.json"

        private val VERTICALS = listOf(
            DemoVertical(
                id = "ecommerce",
                title = "🛒 E-Commerce & Deals",
                tabs = listOf(
                    DemoTab("Deals", "ecommerce/ecommerce_home.json", "🔥"),
                    DemoTab("Catalog", "ecommerce/ecommerce_catalog.json", "📦"),
                    DemoTab("Orders", "ecommerce/ecommerce_profile.json", "📜"),
                )
            ),
            DemoVertical(
                id = "fintech",
                title = "💳 Fintech & Banking",
                tabs = listOf(
                    DemoTab("Balance", "fintech/fintech_dashboard.json", "💳"),
                    DemoTab("KYC Form", "fintech/fintech_kyc.json", "📝"),
                    DemoTab("Limits", "fintech/fintech_limits.json", "🛡️"),
                )
            ),
            DemoVertical(
                id = "food",
                title = "🍔 Food Delivery",
                tabs = listOf(
                    DemoTab("Kitchens", "food/food_feed.json", "🍕"),
                    DemoTab("Live Order", "food/food_tracking.json", "🛵"),
                    DemoTab("Locations", "food/food_account.json", "📍"),
                )
            ),
            DemoVertical(
                id = "paywall",
                title = "💎 SaaS & Paywall",
                tabs = listOf(
                    DemoTab("Plans", "paywall/paywall_plans.json", "💎"),
                    DemoTab("Team", "paywall/paywall_seats.json", "👥"),
                    DemoTab("Usage", "paywall/paywall_usage.json", "📊"),
                )
            ),
            DemoVertical(
                id = "storybook",
                title = "⚡ Primitives Storybook",
                tabs = listOf(
                    DemoTab("Primitives", "storybook/storybook_primitives.json", "🧩"),
                    DemoTab("Plugins", "storybook/storybook_custom.json", "🔌"),
                    DemoTab("Tokens", "storybook/storybook_tokens.json", "🎨"),
                )
            ),
        )
    }
}
