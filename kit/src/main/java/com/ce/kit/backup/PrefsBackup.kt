package com.ce.kit.backup

import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

/**
 * Typed JSON of a SharedPreferences (key to one of b, i, l, f, s or set with its value): the settings format of every Ce
 * backup. Keys that look like secrets are never exported nor imported; [exclude] drops bookkeeping of the device.
 */
object PrefsBackup {
    private val SecretPattern = Regex("pass(word|phrase)?|secret|token|credential|api.?key|private.?key|master|auth|salt|pin$", RegexOption.IGNORE_CASE)

    fun isSecretKey(key: String) = SecretPattern.containsMatchIn(key)

    fun export(prefs: SharedPreferences, exclude: (String) -> Boolean = { false }): JSONObject {
        val values = JSONObject()
        prefs.all.forEach { (key, value) ->
            if (isSecretKey(key) || exclude(key)) return@forEach
            val typed = when (value) {
                is Boolean -> JSONObject().put("b", value)
                is Int -> JSONObject().put("i", value)
                is Long -> JSONObject().put("l", value)
                is Float -> JSONObject().put("f", value.toDouble())
                is String -> JSONObject().put("s", value)
                is Set<*> -> JSONObject().put("set", JSONArray(value.filterIsInstance<String>()))
                else -> null
            }
            if (typed != null) values.put(key, typed)
        }
        return values
    }

    /** Applies typed values over the current ones (it merges: keys the file lacks stay). Badly typed entries are skipped. */
    fun import(prefs: SharedPreferences, values: JSONObject, exclude: (String) -> Boolean = { false }, commit: Boolean = false) {
        val edit = prefs.edit()
        values.keys().forEach { key ->
            if (isSecretKey(key) || exclude(key)) return@forEach
            val typed = values.optJSONObject(key) ?: return@forEach
            runCatching {
                when {
                    typed.has("b") -> edit.putBoolean(key, typed.getBoolean("b"))
                    typed.has("i") -> edit.putInt(key, typed.getInt("i"))
                    typed.has("l") -> edit.putLong(key, typed.getLong("l"))
                    typed.has("f") -> edit.putFloat(key, typed.getDouble("f").toFloat())
                    typed.has("s") -> edit.putString(key, typed.getString("s"))
                    typed.has("set") -> typed.getJSONArray("set").let { list ->
                        edit.putStringSet(key, (0 until list.length()).map { list.getString(it) }.toSet())
                    }
                }
            }
        }
        if (commit) edit.commit() else edit.apply()
    }

    /** True when [text] is a settings file of [app] (a JSON with that app and a settings object), checked before touching anything. */
    fun looksLikeSettings(text: String, app: String): Boolean {
        val o = runCatching { JSONObject(text) }.getOrNull() ?: return false
        return o.optString("app").equals(app, ignoreCase = true) && o.optJSONObject("settings") != null
    }
}
