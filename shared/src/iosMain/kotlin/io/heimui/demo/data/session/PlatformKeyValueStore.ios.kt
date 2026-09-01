package io.heimui.demo.data.session

import platform.Foundation.NSUserDefaults

internal actual object PlatformKeyValueStore {

    private const val KEY_PREFIX = "heimui_demo_"

    private val defaults: NSUserDefaults get() = NSUserDefaults.standardUserDefaults

    actual fun get(key: String): String? = defaults.stringForKey(KEY_PREFIX + key)

    actual fun put(key: String, value: String) {
        defaults.setObject(value, KEY_PREFIX + key)
    }

    actual fun delete(key: String) {
        defaults.removeObjectForKey(KEY_PREFIX + key)
    }

    actual fun clear() {
        // Only this app's own keys: `removePersistentDomainForName` would also wipe unrelated
        // preferences the host app stored in the same suite.
        defaults.dictionaryRepresentation()
            .keys
            .filterIsInstance<String>()
            .filter { it.startsWith(KEY_PREFIX) }
            .forEach { defaults.removeObjectForKey(it) }
    }
}
