package automaton.regular.adapter

import automaton.regular.domain.Grammar
import automaton.regular.reader.GrammarReader
import com.natpryce.hamkrest.anyElement
import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.isIn
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.io.File

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

internal class GrammarAdapterTest {

    companion object GrammarAssertTesting {
        private val isEqual = fun Grammar.(other: Grammar) =
                assertAll(Executable { assertEquals(terminals, other.terminals) },
                        Executable { assertEquals(nonTerminals, other.nonTerminals) },
                        Executable { assertEquals(startSymbol, other.startSymbol) },
                        Executable { assert.that(rules, anyElement(isIn(other.rules))) })
    }

    @Test
    fun isConvertingToGrammar() {
        val actual =
                GrammarReader().readMultipleFromFile(File("testing_grammars.json"))
        actual.forEach { it.isEqual(it.toAutomaton().toGrammar()) }
    }
}