package io.heimui.demo.integration

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.heimui.core.presentation.imageloader.HeimImageLoader
import io.heimui.core.presentation.imageloader.CoilHeimImageLoader
import androidx.compose.ui.layout.ContentScale

/**
 * Wraps the SDK's Coil loader to ask the CDN for an image the size it will actually be drawn at.
 *
 * This is the usual reason to replace the loader, and it is not cosmetic. A payload written by a
 * content team names a source image; nothing in the JSON knows the device is 360dp wide. Left
 * alone, a phone downloads the 2000px hero the designer uploaded and scales it down — paid for in
 * bytes, decode time and memory, on the exact devices least able to spare them.
 *
 * The rewrite is deliberately narrow: it appends sizing parameters only for hosts known to honour
 * them, and returns every other URL untouched. Rewriting a URL you do not control is how images
 * silently start 404ing.
 *
 * The other reason to be here — not shown, because the showcase has no gateway — is auth: images
 * behind a signed CDN need headers, and only the app knows them.
 */
class DemoImageLoader(
    private val delegate: HeimImageLoader = CoilHeimImageLoader(),
) : HeimImageLoader {

    @Composable
    override fun RenderImage(
        url: String,
        contentDescription: String?,
        blurHash: String?,
        cornerRadius: Int,
        height: Int?,
        aspectRatio: Float?,
        contentScale: ContentScale,
        modifier: Modifier,
    ) {
        delegate.RenderImage(
            url = sizedUrl(url, height),
            contentDescription = contentDescription,
            blurHash = blurHash,
            cornerRadius = cornerRadius,
            height = height,
            aspectRatio = aspectRatio,
            contentScale = contentScale,
            modifier = modifier,
        )
    }

    private fun sizedUrl(url: String, height: Int?): String {
        if (height == null || height <= 0) return url
        val host = url.substringAfter("://", "").substringBefore('/').lowercase()
        if (host !in RESIZING_HOSTS) return url
        // Already carries sizing: the payload asked for something specific, so leave it alone.
        if ("w=" in url || "h=" in url) return url
        val separator = if ('?' in url) '&' else '?'
        return "$url${separator}h=${height * DENSITY_HEADROOM}"
    }

    private companion object {
        /** Hosts whose query API is known. Anything else is returned untouched. */
        val RESIZING_HOSTS = setOf("images.unsplash.com", "picsum.photos")

        /** Ask for a little more than the layout height so a 3x screen is not upscaled. */
        const val DENSITY_HEADROOM = 3
    }
}
