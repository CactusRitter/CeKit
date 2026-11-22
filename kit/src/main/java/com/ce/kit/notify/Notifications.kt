package com.ce.kit.notify

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/** What every Ce app does about notification channels and the notification permission. */
object Notifications {
    /**
     * Creates the channel (Android applies [configure] only the first time: later the user's choices in the system win).
     * Safe to call at every start.
     */
    fun ensureChannel(
        context: Context,
        id: String,
        name: String,
        description: String? = null,
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT,
        configure: NotificationChannel.() -> Unit = {},
    ) {
        val channel = NotificationChannel(id, name, importance)
        description?.let { channel.description = it }
        channel.configure()
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    /** true if Android lets the app show notifications (the runtime permission exists from Android 13). */
    fun permissionGranted(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    /** false when the user switched the channel off in the system settings (a channel that does not exist counts as on). */
    fun channelOn(context: Context, id: String): Boolean =
        context.getSystemService(NotificationManager::class.java).getNotificationChannel(id)?.importance != NotificationManager.IMPORTANCE_NONE
}
