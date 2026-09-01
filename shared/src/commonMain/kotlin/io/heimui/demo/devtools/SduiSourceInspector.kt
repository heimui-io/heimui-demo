package io.heimui.demo.devtools

import io.heimui.demo.data.StaticDemoCatalogRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException

/**
 * Showcase-only tool: fetches the raw JSON of a screen **so the demo can display it as text**.
 *
 * ### This is NOT how screens are rendered
 *
 * Read this before copying anything from this file. Screens are fetched and rendered by the SDK
 * and nothing else:
 *
 * ```
 * HeimScreen(screenId)  →  SDK  →  GET the payload  →  parse  →  render
 * ```
 *
 * A real integration never fetches SDUI itself. Doing so would bypass the cache, the ETag
 * revalidation, the payload guard, the signature verification and the circuit breaker — every
 * resilience feature the SDK exists to provide.
 *
 * This class exists for exactly one reason: the `</>` button in the showcase shows developers the
 * payload behind what they are looking at. It requests the same file a second time, purely to
 * print it. That duplicate request is acceptable for a teaching tool and would be a bug anywhere
 * else.
 *
 * It lives under `devtools/` rather than `data/` so nobody mistakes it for the app's data layer.
 */
class SduiSourceInspector(
    private val baseUrl: String = StaticDemoCatalogRepository.sduiBaseUrl,
    private val httpClient: HttpClient = HttpClient(),
) {
    /** Returns the payload text for [screenId], or a failure the sheet can render. */
    suspend fun sourceOf(screenId: String): Result<String> = try {
        val response = httpClient.get("${baseUrl.trimEnd('/')}/screens/$screenId")
        if (response.status.isSuccess()) {
            Result.success(response.bodyAsText())
        } else {
            Result.failure(IllegalStateException("HTTP ${response.status.value} for '$screenId'"))
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.failure(e)
    }
}
