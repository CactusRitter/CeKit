package com.ce.kit

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** The shared texts must have the same format placeholders in en, es and de, and none may be malformed (a bad one crashes the app). */
class StringsTest {
    private val dirs = listOf("values", "values-es", "values-de")
    private val entry = Regex("""<string name="([^"]+)"[^>]*>(.*?)</string>""")
    private val bad = Regex("""%(?!\d+\$[sd]|[sd]|%|n)""")
    private val ph = Regex("""%\d+\$[sd]|%[sd]""")

    private fun load(dir: String): Map<String, String> {
        val file = File("src/main/res/$dir/strings.xml")
        return entry.findAll(file.readText()).associate { it.groupValues[1] to it.groupValues[2] }
    }

    @Test
    fun noMalformedPlaceholders() {
        for (d in dirs) for ((k, v) in load(d)) assertTrue("$d/$k has a malformed %: $v", !bad.containsMatchIn(v))
    }

    @Test
    fun sameKeysAndPlaceholdersInEveryLanguage() {
        val en = load("values")
        for (d in dirs.drop(1)) {
            val other = load(d)
            for ((k, v) in en) {
                val o = other[k] ?: continue // a key without translation (not translatable) is allowed
                assertEquals("$d/$k placeholders", ph.findAll(v).map { it.value }.sorted().toList(), ph.findAll(o).map { it.value }.sorted().toList())
            }
        }
    }
}
