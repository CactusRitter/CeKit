package com.ce.kit.ui.update

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ce.kit.CeKit
import com.ce.kit.R
import com.ce.kit.log.AppLog
import com.ce.kit.log.CrashLog
import com.ce.kit.ui.settings.ChoiceChip
import com.ce.kit.ui.settings.SettingsCard
import com.ce.kit.ui.settings.SwitchRow
import kotlinx.coroutines.launch

/**
 * Settings → Updates, the same in every Ce app: the switch for looking by itself, the banner of a newer version, this
 * version with "Look now", and what each version brought ([changelog] is the app's bundled `res/raw/changelog`).
 */
@Composable
fun UpdatesPage(checkUpdates: Boolean, onCheckUpdates: (Boolean) -> Unit, changelog: String) {
    val updates = CeKit.updates
    val version = CeKit.config.versionName
    val scope = rememberCoroutineScope()
    // What "Look now" found: null = not asked yet; then "new version", "up to date" or "could not check".
    var result by remember { mutableStateOf<String?>(null) }
    var checking by remember { mutableStateOf(false) }
    val upToDate = stringResource(R.string.update_none, version)
    val failed = stringResource(R.string.update_check_failed)
    val foundText = stringResource(R.string.update_found)
    SwitchRow(stringResource(R.string.settings_updates_title), stringResource(R.string.settings_updates_body, CeKit.config.appName), checkUpdates, onCheckUpdates)
    UpdateBanner(allowLater = false)
    SettingsCard {
        Text(stringResource(R.string.settings_version, version), style = MaterialTheme.typography.titleSmall)
        result?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        ChoiceChip(stringResource(if (checking) R.string.update_checking else R.string.settings_updates_now), false, enabled = !checking) {
            if (checking) return@ChoiceChip
            checking = true
            scope.launch {
                result = try {
                    if (updates.check(force = true) != null) foundText else upToDate
                } catch (e: kotlinx.coroutines.CancellationException) {
                    throw e
                } catch (e: Exception) {
                    failed
                }
                checking = false
            }
        }
    }
    WhatsNew(changelog)
}

/** Settings → Diagnostics: the activity log can always be shared; the last crash shows only if there was one. */
@Composable
@OptIn(ExperimentalLayoutApi::class)
fun DiagnosticsPage() {
    val context = LocalContext.current
    var crash by remember { mutableStateOf(CrashLog.read(context)) }
    val copiedLabel = stringResource(R.string.copied)
    fun copy(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(copiedLabel, text))
    }
    SettingsCard {
        Text(stringResource(R.string.settings_log_title), style = MaterialTheme.typography.titleSmall)
        Text(stringResource(R.string.settings_log_body), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val shareTitle = stringResource(R.string.settings_log_share_title)
            ChoiceChip(stringResource(R.string.settings_log_share), false) { AppLog.share(context, shareTitle) }
            ChoiceChip(stringResource(R.string.copy), false) { copy(AppLog.text(context)) }
            ChoiceChip(stringResource(R.string.clear), false) {
                AppLog.clear(context)
                crash = null
            }
        }
    }
    crash?.let { text ->
        SettingsCard {
            Text(stringResource(R.string.settings_crash_title), style = MaterialTheme.typography.titleSmall)
            Text(stringResource(R.string.settings_crash_body), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 6, overflow = TextOverflow.Ellipsis)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ChoiceChip(stringResource(R.string.copy), false) { copy(text) }
                ChoiceChip(stringResource(R.string.clear), false) {
                    CrashLog.clear(context)
                    crash = null
                }
            }
        }
    }
}
