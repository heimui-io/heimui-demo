package io.heimui.demo.data

import io.heimui.core.data.repository.MockHeimScreenRepository
import io.heimui.core.domain.model.HeimValue
import io.heimui.core.domain.repository.HeimScreenRepository
import io.heimui.core.domain.repository.HeimScreenResult
import io.heimui.core.domain.repository.HeimSubmitResult
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class HeimRemoteRepositoryProvider(
    private val baseUrl: String = "https://raw.githubusercontent.com/heimui-io/heimui-demo/main/sdui/"
) : HeimScreenRepository {

    private val httpClient = HttpClient()

    private fun resolveRelativePath(screenId: String): String {
        return when (screenId) {
            "hub_screen" -> "hub/hub_screen.json"
            "ecommerce_home" -> "ecommerce/ecommerce_home.json"
            "ecommerce_catalog" -> "ecommerce/ecommerce_catalog.json"
            "ecommerce_profile" -> "ecommerce/ecommerce_profile.json"
            "fintech_dashboard" -> "fintech/fintech_dashboard.json"
            "fintech_kyc" -> "fintech/fintech_kyc.json"
            "fintech_limits" -> "fintech/fintech_limits.json"
            "food_feed" -> "food/food_feed.json"
            "food_tracking" -> "food/food_tracking.json"
            "food_account" -> "food/food_account.json"
            "paywall_plans" -> "paywall/paywall_plans.json"
            "paywall_seats" -> "paywall/paywall_seats.json"
            "paywall_usage" -> "paywall/paywall_usage.json"
            "storybook_primitives" -> "storybook/storybook_primitives.json"
            "storybook_custom" -> "storybook/storybook_custom.json"
            "storybook_tokens" -> "storybook/storybook_tokens.json"
            else -> "$screenId.json"
        }
    }

    suspend fun fetchRawJson(screenId: String): String {
        val path = resolveRelativePath(screenId)
        val fullUrl = "$baseUrl$path"
        println("📡 [HeimUI Remote] ─── GET Screen Request ─────────────────")
        println("📡 [HeimUI Remote] Screen ID: $screenId")
        println("📡 [HeimUI Remote] Endpoint:  $fullUrl")
        
        val response = httpClient.get(fullUrl)
        val text = response.bodyAsText()
        
        println("📦 [HeimUI Remote] HTTP Status: ${response.status.value} ${response.status.description}")
        println("📦 [HeimUI Remote] Payload Size: ${text.length} characters")
        println("──────────────────────────────────────────────────────────")
        return text
    }

    override fun getScreen(
        screenId: String,
        queryParams: Map<String, String>
    ): Flow<HeimScreenResult> = flow {
        try {
            val rawJson = fetchRawJson(screenId)
            val mockDelegate = MockHeimScreenRepository(jsonProvider = { rawJson })
            mockDelegate.getScreen(screenId, queryParams).collect { result ->
                when (result) {
                    is HeimScreenResult.Success -> {
                        println("✅ [HeimUI Engine] Screen '$screenId' parsed & rendered successfully.")
                    }
                    is HeimScreenResult.Error -> {
                        println("❌ [HeimUI Engine] Error rendering screen '$screenId': ${result.message}")
                    }
                    else -> Unit
                }
                emit(result)
            }
        } catch (e: Throwable) {
            println("❌ [HeimUI Remote] Network Failure for screen '$screenId': ${e.message}")
            emit(HeimScreenResult.Error(message = "Failed to fetch remote screen: ${e.message}", throwable = e))
        }
    }.flowOn(Dispatchers.Default)

    override suspend fun submitForm(
        endpoint: String,
        method: String,
        payload: Map<String, HeimValue>?
    ): HeimSubmitResult {
        println("📤 [HeimUI Remote] Form Submit -> Endpoint: $endpoint, Method: $method, Payload: $payload")
        return HeimSubmitResult.Success(message = "Submitted to $endpoint successfully!")
    }
}
