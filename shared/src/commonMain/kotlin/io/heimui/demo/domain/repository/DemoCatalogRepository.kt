package io.heimui.demo.domain.repository

import io.heimui.demo.domain.model.DemoVertical

/**
 * Source of the showcase catalog.
 *
 * An interface for one static implementation may look like ceremony, but it marks the seam that
 * matters: in a real integration this catalog comes from your backend, feature flags, or a
 * remote-config service — not from a constant. Swapping the implementation is the whole point,
 * and the presentation layer never learns which one it got.
 */
interface DemoCatalogRepository {
    suspend fun verticals(): List<DemoVertical>
    suspend fun verticalById(id: String): DemoVertical?
    fun hubScreenId(): String
}
