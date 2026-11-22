package com.ce.kit.backup

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import org.json.JSONArray
import org.json.JSONObject

/** The file is not a backup of this app (another app's zip, no manifest, too many entries, a bomb). */
class NotABackupException : Exception()

/** The backup comes from a newer container format than this version understands. */
class TooNewException : Exception()

/**
 * The container shared by every Ce backup: a zip with `manifest.json` at the root (`app`, `container`, `appVersion`,
 * `createdAt`, `contents`) and one entry (or folder) per section. Only the safe low-level pieces live here; what each
 * section holds and how it is restored stays in the app.
 */
object BackupFormat {
    const val CONTAINER = 1
    const val MANIFEST = "manifest.json"
    const val SETTINGS = "settings.json"
    const val MAX_JSON_BYTES = 64 * 1024 * 1024
    const val MAX_ENTRY_BYTES = 64L * 1024 * 1024
    const val MAX_ENTRIES = 20_000

    /** Names allowed for files of a folder inside the zip (images...): no path separators, so no path traversal. */
    val SafeName = Regex("""^[\w.-]+$""")

    fun manifest(app: String, appVersion: String, contents: List<String>): JSONObject = JSONObject()
        .put("app", app)
        .put("container", CONTAINER)
        .put("appVersion", appVersion)
        .put("createdAt", System.currentTimeMillis())
        .put("contents", JSONArray(contents))

    fun putText(zip: ZipOutputStream, name: String, text: String) {
        zip.putNextEntry(ZipEntry(name))
        zip.write(text.toByteArray(Charsets.UTF_8))
        zip.closeEntry()
    }

    fun putJson(zip: ZipOutputStream, name: String, json: JSONObject) = putText(zip, name, json.toString(2))

    /** Reads at most [max] bytes of an entry; a bigger one is not a backup (guards against zip bombs). */
    fun readLimited(input: InputStream, max: Int = MAX_JSON_BYTES): ByteArray {
        val out = ByteArrayOutputStream()
        val buffer = ByteArray(16 * 1024)
        while (true) {
            val n = input.read(buffer)
            if (n < 0) break
            if (out.size() + n > max) throw NotABackupException()
            out.write(buffer, 0, n)
        }
        return out.toByteArray()
    }

    /** Copies an entry of at most [max] bytes (images). */
    fun copyLimited(input: InputStream, out: OutputStream, max: Long = MAX_ENTRY_BYTES) {
        val buffer = ByteArray(16 * 1024)
        var total = 0L
        while (true) {
            val n = input.read(buffer)
            if (n < 0) break
            total += n
            if (total > max) throw IOException("Entry too big")
            out.write(buffer, 0, n)
        }
    }

    /** Checks the manifest of [app]: another app's (or none) is [NotABackupException]; a newer container is [TooNewException]. */
    fun checkManifest(manifest: JSONObject?, app: String) {
        val m = manifest ?: throw NotABackupException()
        if (!m.optString("app").equals(app, ignoreCase = true)) throw NotABackupException()
        if (m.optInt("container", 0) > CONTAINER) throw TooNewException()
    }

    /** Call it while reading a zip with the number of entries seen so far: more than [MAX_ENTRIES] is not a backup. */
    fun checkEntryCount(count: Int) {
        if (count > MAX_ENTRIES) throw NotABackupException()
    }
}
