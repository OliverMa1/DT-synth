package edu.illinois.automaticsafetygames.finitelybranching.teacher;

import dk.brics.automaton.Automaton;

/**
 * This interface defines how information is to be exchanged with a edu.illinois.automaticsafetygames.finitelybranching.teacher for
 * an automatic safety game.
 * 
 * @author Daniel Neider
 *
 */
public interface ITeacher {

	/**
	 * Checks whether the given conjecture accepts all initial vertices.
	 * 
	 * @param conjecture
	 *            The conjecture to check (must not be <code>null</code>)
	 * @return Returns <code>null</code> if the conjecture accepts all initial
	 *         vertices; otherwise, returns a positive counterexample
	 */
	public String containsInitial(Automaton conjecture);

	/**
	 * Checks whether the given conjecture accepts only safe vertices.
	 * 
	 * @param conjecture
	 *            The conjecture to check (must not be <code>null</code>)
	 * @return Returns <code>null</code> if the conjecture accepts only safe
	 *         vertices; otherwise, returns a negative counterexample
	 */
	public String isSafe(Automaton conjecture);

	/**
	 * Checks whether the given conjecture is existentially closed.
	 * 
	 * @param conjecture
	 *            The conjecture to check (must not be <code>null</code>)
	 * @return Returns <code>null</code> if the conjecture is existentially
	 *         closed; otherwise, returns an existential implication
	 *         counterexample
	 */
	public ImplicationCounterexample isExistentiallyClosed(Automaton conjecture);

	/**
	 * Checks whether the given conjecture is universally closed.
	 * 
	 * @param conjecture
	 *            The conjecture to check (must not be <code>null</code>)
	 * @return Returns <code>null</code> if the conjecture is universally
	 *         closed; otherwise, returns an universal implication
	 *         counterexample.
	 */
	public ImplicationCounterexample isUniversallyClosed(Automaton conjecture);

	/**
	 * Returns the alphabet size assumed to be used by all automata.
	 * 
	 * @return Returns the alphabet size (has to be greater than <code>0</code>
	 *         ).
	 */
	public int getAlphabetSize();

}
