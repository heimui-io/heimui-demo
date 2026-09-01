package io.heimui.demo.data.session

import io.heimui.core.domain.port.HeimStorageDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Disk-backed storage for the SDUI cache and for form drafts.
 *
 * The SDK defaults to an in-memory cache, which dies with the process — so every cold start
 * refetches and there is no offline story at all. Plugging a driver is what turns
 * stale-while-revalidate into something a user actually notices: the previous screen appears
 * instantly while the network revalidates behind it.
 *
 * It matters more for [io.heimui.core.presentation.form.HeimFormDraftStorage]. A user halfway
 * through a KYC form who takes a phone call can have the app killed underneath them; with this
 * driver their answers are still there, and without it the form is empty and they start again.
 *
 * Backed by SharedPreferences on Android and NSUserDefaults on iOS — see [PlatformKeyValueStore].
 * A production app implements these four methods over DataStore, SQLDelight or the Keychain, and
 * over an **encrypted** store when payloads carry anything personal.
 */
class SettingsStorageDriver : HeimStorageDriver {

    // Serialises writes across coroutines. The platform stores are themselves thread-safe, but
    // read-modify-write from two screens at once would still interleave.
    private val mutex = Mutex()

    override suspend fun get(key: String): String? = withContext(Dispatchers.Default) {
        mutex.withLock { PlatformKeyValueStore.get(key) }
    }

    override suspend fun put(key: String, value: String): Unit = withContext(Dispatchers.Default) {
        mutex.withLock { PlatformKeyValueStore.put(key, value) }
    }

    override suspend fun delete(key: String): Unit = withContext(Dispatchers.Default) {
        mutex.withLock { PlatformKeyValueStore.delete(key) }
    }

    override suspend fun clear(): Unit = withContext(Dispatchers.Default) {
        mutex.withLock { PlatformKeyValueStore.clear() }
    }
}
