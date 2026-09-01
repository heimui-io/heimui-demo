package io.heimui.demo.data.session

import io.heimui.core.domain.port.HeimStorageDriver
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Disk-backed storage for the SDUI cache.
 *
 * The SDK defaults to an in-memory cache, which dies with the process — so every cold start
 * refetches and there is no offline story at all. Plugging a driver is what turns
 * stale-while-revalidate into something a user actually notices: the previous screen appears
 * instantly while the network revalidates behind it.
 *
 * This implementation is a placeholder that still lives in memory, kept deliberately small so the
 * *seam* is obvious. A production app implements these four methods over DataStore, SQLDelight or
 * NSUserDefaults — and over an **encrypted** store when payloads carry anything personal.
 */
class SettingsStorageDriver : HeimStorageDriver {
    private val entries = mutableMapOf<String, String>()
    private val mutex = Mutex()

    override suspend fun get(key: String): String? = mutex.withLock { entries[key] }
    override suspend fun put(key: String, value: String) { mutex.withLock { entries[key] = value } }
    override suspend fun delete(key: String) { mutex.withLock { entries.remove(key) } }
    override suspend fun clear() { mutex.withLock { entries.clear() } }
}
