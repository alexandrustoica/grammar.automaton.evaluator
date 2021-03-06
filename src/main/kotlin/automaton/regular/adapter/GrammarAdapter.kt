package automaton.regular.adapter

import automaton.regular.domain.*


class GrammarAdapter(private val automaton: FiniteAutomaton) {

    fun toGrammar() =
            Grammar(automaton.alphabet, nonTerminals(),
                    convertTransitionsToRules(automaton.transitions),
                    automaton.startState.toNonTerminal())

    private fun convertTransitionsToRules(transitions: List<Transition>) =
            transitions.map { convertTransitionToRule(it) } +
                    convertEndTransitionsToRules()

    private fun convertEndTransitionsToRules() =
            automaton.endStates.flatMap {
                automaton.getTransitionsBasedOn(it)
                        .map { transition ->  Rule(listOf(
                                transition.start.toNonTerminal()),
                                listOf(transition.value)) } }

    private fun convertTransitionToRule(transition: Transition) =
            Rule(listOf(transition.start.toNonTerminal()),
                    listOf(transition.value) + filterTerminalState(transition))

    private fun filterTerminalState(transition: Transition) =
            if (isInSpecialCase(transition)) listOf()
            else listOf(transition.end.toNonTerminal())

    private fun isInSpecialCase(transition: Transition) =
            transition.end.value.matches(Regex("W\\d*")) ||
                    (transition.value == Terminal.EMPTY &&
                            transition.start == automaton.startState)

    private fun nonTerminals() =
            automaton.states.filterNot { it.value.matches(Regex("W\\d*")) }
                    .map { it.toNonTerminal() }

}
