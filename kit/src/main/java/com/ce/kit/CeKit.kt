package com.ce.kit

import android.content.Context
import com.ce.kit.log.AppLog
import com.ce.kit.log.CrashLog
import com.ce.kit.update.Updates

/**
 * What the kit needs to know about the app it runs in. [githubRepo] is "owner/name" of the repository whose latest
 * release carries the APK; [apkPrefix] names the downloaded file (`<apkPrefix>-<version>.apk`).
 */
data class CeAppConfig(
    val appName: String,
    val versionName: String,
    val versionCode: Int,
    val githubRepo: String,
    val apkPrefix: String = appName,
)

/** Entry point: call [init] once from `Application.onCreate`, before anything else of the kit is used. */
object CeKit {
    /** Versión del kit (sube al cambiar algo que las apps deban notar). */
    const val VERSION = "0.2.0"

    /** Identity of the running app; a neutral placeholder until [init] runs (unit tests never call it). */
    var config: CeAppConfig = CeAppConfig("Ce", "0", 0, "")
        private set

    private lateinit var appContext: Context

    /** The app's update checker/downloader (one per process). */
    val updates: Updates by lazy { Updates(appContext) }

    /** Sets the app's identity, starts the activity log and the crash log. */
    fun init(context: Context, config: CeAppConfig) {
        this.config = config
        appContext = context.applicationContext
        CrashLog.install(appContext)
        AppLog.init(appContext)
    }

    /** Authority of the kit's FileProvider (update APK and shared logs): `<applicationId>.kit`. */
    fun fileAuthority(context: Context): String = "${context.packageName}.kit"
}
