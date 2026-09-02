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
         * To iterate locally, serve the repo root and point this at the host machine:
         *
         * ```
         * python3 -m http.server 8080
         * sduiBaseUrl = "http://10.0.2.2:8080/sdui"   // 10.0.2.2 is the emulator's host loopback
         * ```
         *
         * Plain HTTP only reaches the app because `src/debug` carries a network security config
         * that exempts loopback addresses. Release builds still refuse cleartext.
         */
        var sduiBaseUrl: String =
            "http://10.0.2.2:8099/sdui"

        const val HUB_SCREEN_ID: String = "hub/hub_screen.json"

        private val VERTICALS = listOf(
            DemoVertical(
                id = "ecommerce",
                title = "E-Commerce & Deals",
                tabs = listOf(
                    DemoTab("Deals", "ecommerce/ecommerce_home.json", "local_fire_department"),
                    DemoTab("Catalog", "ecommerce/ecommerce_catalog.json", "category"),
                    DemoTab("Orders", "ecommerce/ecommerce_profile.json", "list_alt"),
                )
            ),
            DemoVertical(
                id = "fintech",
                title = "Fintech & Banking",
                tabs = listOf(
                    DemoTab("Balance", "fintech/fintech_dashboard.json", "account_balance_wallet"),
                    DemoTab("KYC Form", "fintech/fintech_kyc.json", "list_alt"),
                    DemoTab("Limits", "fintech/fintech_limits.json", "security"),
                )
            ),
            DemoVertical(
                id = "food",
                title = "Food Delivery",
                tabs = listOf(
                    DemoTab("Kitchens", "food/food_feed.json", "local_pizza"),
                    DemoTab("Live Order", "food/food_tracking.json", "two_wheeler"),
                    DemoTab("Locations", "food/food_account.json", "location_on"),
                )
            ),
            DemoVertical(
                id = "paywall",
                title = "SaaS & Paywall",
                tabs = listOf(
                    DemoTab("Plans", "paywall/paywall_plans.json", "diamond"),
                    DemoTab("Team", "paywall/paywall_seats.json", "groups"),
                    DemoTab("Usage", "paywall/paywall_usage.json", "trending_up"),
                )
            ),
            DemoVertical(
                id = "storybook",
                title = "Primitives Storybook",
                tabs = listOf(
                    DemoTab("Primitives", "storybook/storybook_primitives.json", "extension"),
                    DemoTab("Plugins", "storybook/storybook_custom.json", "widgets"),
                    DemoTab("Tokens", "storybook/storybook_tokens.json", "palette"),
                )
            ),
        )
    }
}
