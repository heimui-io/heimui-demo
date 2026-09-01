package io.heimui.demo.data.session

/**
 * The smallest persistent key-value store each platform already provides.
 *
 * `expect`/`actual` rather than a multiplatform storage library on purpose: this is the seam a
 * real integration has to fill anyway, and showing it with no dependency makes it obvious that
 * `HeimStorageDriver` asks for four trivial methods, not a framework.
 *
 * Not encrypted. A production app storing anything personal in an SDUI cache — a balance, a name,
 * a document number — wants EncryptedSharedPreferences or the Keychain here instead.
 */
internal expect object PlatformKeyValueStore {
    fun get(key: String): String?
    fun put(key: String, value: String)
    fun delete(key: String)
    fun clear()
}
