package com.ce.kit.update

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdatesLogicTest {
    @Test fun newerComparesNumberByNumber() {
        assertTrue(Versions.isNewer("2.10.0", "2.9.9"))
        assertTrue(Versions.isNewer("v2.7.0", "2.6.1"))
        assertFalse(Versions.isNewer("2.7.0", "2.7.0"))
        assertFalse(Versions.isNewer("2.7", "2.7.0"))
        assertTrue(Versions.isNewer("2.7.1", "2.7"))
        assertFalse(Versions.isNewer("beta", "2.7.0"))
    }

    @Test fun releaseNotesPicksTheLanguageThenEnglishThenAll() {
        val body = "## en\n- one\n## es\n- uno\n## de\n- eins\n"
        assertEquals("- uno", ReleaseNotes.forLanguage(body, "es"))
        assertEquals("- eins", ReleaseNotes.forLanguage(body, "de"))
        assertEquals("- one", ReleaseNotes.forLanguage(body, "fr"))
        assertEquals("plain text", ReleaseNotes.forLanguage("plain text", "es"))
    }

    @Test fun bundledChangelogSectionsByVersion() {
        val text = "## 2.7.0\n- a\n- b\n\n## 2.6.1\n- c\n"
        assertEquals("- a\n- b", ReleaseNotes.forVersion(text, "2.7.0"))
        assertNull(ReleaseNotes.forVersion(text, "9.9.9"))
        assertEquals(listOf("2.7.0", "2.6.1"), ReleaseNotes.all(text).map { it.first })
    }
}
