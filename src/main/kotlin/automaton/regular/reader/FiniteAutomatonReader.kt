package automaton.regular.reader

import automaton.regular.builder.FiniteAutomatonBuilder
import automaton.regular.domain.FiniteAutomaton
import automaton.regular.domain.State
import automaton.regular.domain.Terminal
import com.beust.klaxon.*
import java.io.File


class FiniteAutomatonReader : Reader<FiniteAutomaton>, FileReader<FiniteAutomaton> {

    companion object AutomatonJsonExtension {
        private val toAutomaton = fun JsonObject.(): FiniteAutomaton = FiniteAutomatonBuilder()
                .setStates(this.array<String>("states")?.value?.toList().orEmpty().map { State(it) })
                .setStartState(State(this.string("startState").orEmpty()))
                .setEndStates(this.array<String>("endStates")?.value?.toList().orEmpty().map { State(it) })
                .setAlphabet(this.array<String>("alphabet")?.value?.toList().orEmpty().map { Terminal(it) })
                .setTransitionsFromStrings(this.array<String>("transitions")?.value?.toList().orEmpty())
                .build()
    }

    override fun readFromFile(source: File): FiniteAutomaton =
            getJsonParserFor(source).toAutomaton()

    fun readMultipleFromFile(source: File): List<FiniteAutomaton> =
            getJsonParserForFileList(source).map {it.toAutomaton()}

    private fun getJsonParserFor(source: File): JsonObject =
            Parser().parse(StringBuilder(source.readText())) as JsonObject

    private fun getJsonParserForFileList(source: File): JsonArray<JsonObject> =
            Parser().parse(StringBuilder(source.readText())) as JsonArray<JsonObject>

    override fun read(): FiniteAutomaton = FiniteAutomatonBuilder()
            .setStates(readStates("States"))
            .setStartState(readStartState())
            .setEndStates(readStates("End States"))
            .setAlphabet(readAlphabet())
            .setTransitionsFromStrings(readTransitions())
            .build()

    private fun readAlphabet(): List<Terminal> =
            readElementsBasedOn("Alphabet: ", ", ").map { Terminal(it) }

    private fun readStartState(): State =
            State(print("Start Symbol: ")
                    .let { readLine()?.replace(" ", "") ?: "" })

    private fun readStates(message: String): List<State> =
            readElementsBasedOn("$message: ", ",").map { State(it) }

    private fun readElementsBasedOn(message: String, delimiters: String): List<String> =
            print("$message: ").let { readLine()?.replace(" ", "")
                    ?.split(delimiters) ?: listOf() }

    private fun readTransitions(): List<String> =
            print("Number of Transitions: "). let {readTransitions(0,
                    mutableListOf(), readLine()?.toInt() ?: 0) }

    private fun readTransitions(
            indexTransition: Int,
            accumulator: MutableList<String>, total: Int): List<String> =
            if (indexTransition == total) accumulator.toList()
            else print("Transition#${indexTransition + 1}: ").let {
                addToAccumulatorAndContinue(indexTransition + 1, accumulator, total) }

    private fun addToAccumulatorAndContinue(
            indexTransition: Int,
            accumulator: MutableList<String>, total: Int): List<String> =
            readTransitions(indexTransition, accumulator
                    .add(readLine().orEmpty()).let { accumulator }, total)
}