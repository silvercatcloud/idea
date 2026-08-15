package com.silvercat.sparkcards.data

import com.silvercat.sparkcards.data.model.CardCategory
import com.silvercat.sparkcards.data.source.CardContentSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class CardContentSourceTest {

    @Test
    fun `parse decodes a minimal valid card`() {
        val json = """
            [{"id":"t1","category":"TERMINOLOGY","title":"熵","hook":"h","detail":"d"}]
        """.trimIndent()

        val cards = CardContentSource.parse(json)

        assertEquals(1, cards.size)
        assertEquals("t1", cards[0].id)
        assertEquals(CardCategory.TERMINOLOGY, cards[0].category)
        assertEquals(null, cards[0].source)
    }

    @Test
    fun `parse ignores unknown keys`() {
        val json = """
            [{"id":"t1","category":"MOVIE","title":"x","hook":"h","detail":"d","unknownField":123}]
        """.trimIndent()

        val cards = CardContentSource.parse(json)

        assertEquals(1, cards.size)
    }

    @Test
    fun `bundled cards:json is valid and matches the seeded content plan`() {
        val file = findCardsJson()
        val cards = CardContentSource.parse(file.readText())

        assertEquals(150, cards.size)

        val ids = cards.map { it.id }
        assertEquals("card ids must be unique", ids.size, ids.toSet().size)

        cards.forEach { card ->
            assertTrue("title blank for ${card.id}", card.title.isNotBlank())
            assertTrue("hook blank for ${card.id}", card.hook.isNotBlank())
            assertTrue("detail blank for ${card.id}", card.detail.isNotBlank())
        }

        val counts = cards.groupingBy { it.category }.eachCount()
        assertEquals(40, counts[CardCategory.TERMINOLOGY])
        assertEquals(30, counts[CardCategory.MOVIE])
        assertEquals(30, counts[CardCategory.BOOK])
        assertEquals(25, counts[CardCategory.POETRY])
        assertEquals(25, counts[CardCategory.PERSON])
    }

    /** Unit tests run with the module dir as the working dir; assets aren't on the JVM classpath. */
    private fun findCardsJson(): File {
        val candidate = File("src/main/assets/cards.json")
        check(candidate.exists()) { "cards.json not found at ${candidate.absolutePath}" }
        return candidate
    }
}
