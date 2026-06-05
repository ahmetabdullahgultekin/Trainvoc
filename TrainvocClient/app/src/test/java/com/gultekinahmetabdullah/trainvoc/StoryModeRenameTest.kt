package com.gultekinahmetabdullah.trainvoc

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Regression guard for issue #168 ("Story Mode" had no story content).
 *
 * The feature was renamed from "Story Mode" / "Learn through stories" to an honest
 * level-based label, because the screen is a CEFR level picker that launches a normal
 * quiz — it never told a narrative. These assertions read the real `strings.xml`
 * resources straight off disk (pure JVM, no Android/Robolectric, no emulator) and fail
 * if anyone reintroduces a narrative promise on the shipped `story_mode*` strings
 * without actually building story content.
 *
 * Note: this intentionally checks only the `story_mode` and `story_mode_subtitle`
 * resources. The separate, unshipped premium roadmap flag `INTERACTIVE_STORIES`
 * (FeatureFlag.kt) may legitimately mention stories — it describes a *future* feature.
 */
class StoryModeRenameTest {

    private val narrativeTermsEn = listOf("story", "stories")
    private val narrativeTermsTr = listOf("hikaye", "hikâye", "öykü", "masal")

    private fun resFile(relative: String): File {
        // Tests run with the module dir (app/) as the working directory under Gradle.
        val candidates = listOf(
            File(relative),
            File("app/$relative"),
            File("TrainvocClient/app/$relative"),
        )
        return candidates.firstOrNull { it.exists() }
            ?: error("Could not locate $relative from ${File("").absolutePath}")
    }

    /** Extracts the text of <string name="..."> from a strings.xml file. */
    private fun stringValue(xml: File, name: String): String {
        val regex = Regex("<string name=\"$name\"[^>]*>(.*?)</string>", RegexOption.DOT_MATCHES_ALL)
        val match = regex.find(xml.readText())
            ?: error("string '$name' not found in ${xml.path}")
        return match.groupValues[1].trim()
    }

    @Test
    fun englishStoryModeLabelDoesNotPromiseNarrative() {
        val en = resFile("src/main/res/values/strings.xml")
        val title = stringValue(en, "story_mode").lowercase()
        val subtitle = stringValue(en, "story_mode_subtitle").lowercase()

        narrativeTermsEn.forEach { term ->
            assertFalse(
                "English story_mode title still promises narrative ('$term'): \"$title\". " +
                    "Either ship real story content or keep the rename (#168).",
                title.contains(term),
            )
            assertFalse(
                "English story_mode_subtitle still promises narrative ('$term'): \"$subtitle\". " +
                    "Either ship real story content or keep the rename (#168).",
                subtitle.contains(term),
            )
        }
        assertTrue("English story_mode title should be non-empty", title.isNotBlank())
        assertTrue("English story_mode_subtitle should be non-empty", subtitle.isNotBlank())
    }

    @Test
    fun turkishStoryModeLabelDoesNotPromiseNarrative() {
        val tr = resFile("src/main/res/values-tr/strings.xml")
        val title = stringValue(tr, "story_mode").lowercase()
        val subtitle = stringValue(tr, "story_mode_subtitle").lowercase()

        narrativeTermsTr.forEach { term ->
            assertFalse(
                "Turkish story_mode title still promises narrative ('$term'): \"$title\". (#168)",
                title.contains(term),
            )
            assertFalse(
                "Turkish story_mode_subtitle still promises narrative ('$term'): \"$subtitle\". (#168)",
                subtitle.contains(term),
            )
        }
        assertTrue("Turkish story_mode title should be non-empty", title.isNotBlank())
        assertTrue("Turkish story_mode_subtitle should be non-empty", subtitle.isNotBlank())
    }
}
