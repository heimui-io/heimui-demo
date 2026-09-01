package io.heimui.demo.data.session

import android.content.Context
import android.content.SharedPreferences

internal actual object PlatformKeyValueStore {

    private const val PREFS_NAME = "heimui_demo_store"

    /**
     * Set once from the Application/Activity before the first store access.
     *
     * A `Context` cannot reach common code, and a showcase does not need androidx.startup to make
     * that point. A real app injects it rather than parking it in a static.
     */
    var context: Context? = null

    private val prefs: SharedPreferences?
        get() = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    actual fun get(key: String): String? = prefs?.getString(key, null)

    actual fun put(key: String, value: String) {
        // `commit`, not `apply`: the SDUI cache is written as a screen renders, and process death
        // during an async write is exactly the case this store exists to survive.
        prefs?.edit()?.putString(key, value)?.commit()
    }

    actual fun delete(key: String) {
        prefs?.edit()?.remove(key)?.commit()
    }

    actual fun clear() {
        prefs?.edit()?.clear()?.commit()
    }
}

/**
 * Hands the app's context to storage. Call once from `Application` or the launcher Activity,
 * before the first screen composes.
 *
 * This exists so the store itself can stay internal: the app module needs to supply a context,
 * not to reach into the storage implementation.
 */
fun initializeDemoStorage(context: Context) {
    PlatformKeyValueStore.context = context.applicationContext
}
