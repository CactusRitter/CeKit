package com.ce.kit.auth

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.ce.kit.log.AppLog
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

/** Where an account's picture can come from. */
enum class PhotoProvider { GOOGLE, MICROSOFT }

/**
 * The picture of each account: by default the one the account has at Google or Microsoft (fetched once and checked
 * again every week), or one the user picked from the gallery, which then stays. Kept as a small square JPEG in the
 * app's files; [version] changes whenever any picture does, so the screens redraw. Accounts are told apart by an
 * app-chosen [key] (letters and digits). [tokenFor] gives the access token of the account (null: none, nothing is fetched).
 */
class AccountPhotos(
    private val context: Context,
    private val client: OkHttpClient,
    private val tokenFor: suspend (key: String, provider: PhotoProvider) -> String?,
) {
    private val _version = MutableStateFlow(0)
    val version: StateFlow<Int> = _version

    fun file(key: String): File = File(context.filesDir, "avatars/${key.filter { it.isLetterOrDigit() }}.jpg")

    private fun prefs() = context.getSharedPreferences("avatars", Context.MODE_PRIVATE)

    /** true when the user chose the picture (the provider's is then never fetched over it). */
    fun isCustom(key: String) = prefs().getBoolean("custom_$key", false)

    /** Uses a picture from the gallery. false if it could not be read. */
    suspend fun setFromUri(key: String, uri: Uri): Boolean = withContext(Dispatchers.IO) {
        val bytes = try {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        } catch (e: Exception) {
            AppLog.e("photos", "could not read the picked picture", e)
            null
        } ?: return@withContext false
        save(key, bytes).also { ok -> if (ok) prefs().edit().putBoolean("custom_$key", true).apply() }
    }

    /** Goes back to the provider's picture (fetched now; without one, the initial of the account). */
    suspend fun useProvider(key: String, provider: PhotoProvider?): Boolean {
        prefs().edit().remove("custom_$key").apply()
        file(key).delete()
        _version.value++
        return provider != null && fetch(key, provider)
    }

    /** Fetches the provider's picture if there is no chosen one and it was not checked lately. */
    suspend fun refreshIfStale(key: String, provider: PhotoProvider?) {
        if (provider == null || isCustom(key)) return
        val checked = prefs().getLong(checkedKey(key), 0L)
        if (System.currentTimeMillis() - checked < RECHECK_MILLIS && (checked > 0 || file(key).exists())) return
        fetch(key, provider)
    }

    /** Deletes the picture of a removed account. */
    fun remove(key: String) {
        file(key).delete()
        prefs().edit().remove("custom_$key").remove(checkedKey(key)).apply()
        _version.value++
    }

    private suspend fun fetch(key: String, provider: PhotoProvider): Boolean = withContext(Dispatchers.IO) {
        val bytes = try {
            val token = tokenFor(key, provider) ?: return@withContext false
            if (provider == PhotoProvider.GOOGLE) googlePicture(token) else microsoftPicture(token)
        } catch (e: Exception) {
            AppLog.e("photos", "picture of account ...${key.takeLast(4)} not fetched", e)
            return@withContext false // offline, say: tried again next time
        }
        // Found: look again in a week. Not found (a refusal or a failed answer, not just "no picture"): try again in a few hours.
        val now = System.currentTimeMillis()
        prefs().edit().putLong(checkedKey(key), if (bytes != null) now else now - (RECHECK_MILLIS - RETRY_MILLIS)).apply()
        AppLog.i("photos", "picture of account ...${key.takeLast(4)}: ${if (bytes != null) "${bytes.size} bytes" else "none"}")
        bytes != null && save(key, bytes)
    }

    private fun googlePicture(token: String): ByteArray? {
        val info = get("https://openidconnect.googleapis.com/v1/userinfo", token) ?: return null
        val url = JSONObject(String(info)).optString("picture").takeIf { it.isNotBlank() } ?: return null
        // Google's picture addresses end with the size ("=s96-c"): a bigger one looks sharp on large screens.
        return get(url.replace(Regex("=s\\d+(-c)?$"), "=s$SIZE-c"), null)
    }

    private fun microsoftPicture(token: String): ByteArray? =
        get("https://graph.microsoft.com/v1.0/me/photos/240x240/\$value", token)
            ?: get("https://graph.microsoft.com/v1.0/me/photo/\$value", token)

    /** The body, or null when the server says there is nothing (404 and the like). Network failures throw. */
    private fun get(url: String, token: String?): ByteArray? {
        val request = Request.Builder().url(url).apply { token?.let { header("Authorization", "Bearer $it") } }.build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) AppLog.i("photos", "${request.url.host}: ${response.code}")
            return if (response.isSuccessful) response.body.bytes() else null
        }
    }

    /** Crops the picture to a centred square, shrinks it and keeps it as JPEG. */
    private fun save(key: String, bytes: ByteArray): Boolean {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return false
        var sample = 1
        while (minOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= SIZE) sample *= 2
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, BitmapFactory.Options().apply { inSampleSize = sample })
            ?: return false
        val side = minOf(bitmap.width, bitmap.height)
        val square = Bitmap.createBitmap(bitmap, (bitmap.width - side) / 2, (bitmap.height - side) / 2, side, side)
        val scaled = if (side > SIZE) Bitmap.createScaledBitmap(square, SIZE, SIZE, true) else square
        val target = file(key)
        target.parentFile?.mkdirs()
        val temp = File(target.parentFile, target.name + ".tmp")
        temp.outputStream().use { scaled.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        temp.renameTo(target)
        _version.value++
        return true
    }

    private companion object {
        const val SIZE = 256
        const val RECHECK_MILLIS = 7L * 24 * 3600 * 1000
        const val RETRY_MILLIS = 6L * 3600 * 1000

        /** "v2": a failed check of an older version could look like a success, so every account is looked at once more. */
        fun checkedKey(key: String) = "checked_v2_$key"
    }
}
