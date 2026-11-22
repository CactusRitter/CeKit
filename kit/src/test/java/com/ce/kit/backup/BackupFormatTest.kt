package com.ce.kit.backup

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class BackupFormatTest {
    @Test fun readLimitedReturnsWhatFits() {
        val data = ByteArray(1000) { it.toByte() }
        assertArrayEquals(data, BackupFormat.readLimited(ByteArrayInputStream(data), 1000))
    }

    @Test fun readLimitedRejectsABomb() {
        try {
            BackupFormat.readLimited(ByteArrayInputStream(ByteArray(2000)), 1000)
            fail("a bigger entry must be rejected")
        } catch (_: NotABackupException) {
        }
    }

    @Test fun copyLimitedStopsAtTheLimit() {
        val out = ByteArrayOutputStream()
        try {
            BackupFormat.copyLimited(ByteArrayInputStream(ByteArray(5000)), out, 1000)
            fail("a bigger entry must be rejected")
        } catch (_: IOException) {
        }
    }

    @Test fun tooManyEntriesIsNotABackup() {
        BackupFormat.checkEntryCount(BackupFormat.MAX_ENTRIES)
        try {
            BackupFormat.checkEntryCount(BackupFormat.MAX_ENTRIES + 1)
            fail("too many entries")
        } catch (_: NotABackupException) {
        }
    }

    @Test fun safeNamesHaveNoSeparators() {
        assertTrue(BackupFormat.SafeName.matches("photo-1_2.jpg"))
        assertFalse(BackupFormat.SafeName.matches("../x.jpg"))
        assertFalse(BackupFormat.SafeName.matches("a/b.jpg"))
        assertFalse(BackupFormat.SafeName.matches("a\\b.jpg"))
    }

    @Test fun secretLookingKeysAreRecognised() {
        assertTrue(PrefsBackup.isSecretKey("imap_password"))
        assertTrue(PrefsBackup.isSecretKey("api_key"))
        assertTrue(PrefsBackup.isSecretKey("refresh_token"))
        assertFalse(PrefsBackup.isSecretKey("accent"))
        assertFalse(PrefsBackup.isSecretKey("transparency"))
    }
}
