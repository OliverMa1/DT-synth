package edu.illinois.automaticsafetygames.games;

import dk.brics.automaton.Automaton;

/**
 * A game defined by externally given automata.
 * 
 * @author Daniel Neider
 *
 */
public class GenericGame implements IGame {

	/**
	 * The alphabet size of the game
	 */
	int alphabetSize;

	/**
	 * Automaton constituting Player 0's vertices
	 */
	private Automaton player0Vertices;

	/**
	 * Automaton constituting Player 1's vertices
	 */
	private Automaton player1Vertices;

	/**
	 * Automaton constituting the initial vertices
	 */
	private Automaton initialVertices;

	/**
	 * Automaton constituting the safe vertices
	 */
	private Automaton safeVertices;

	/**
	 * Automaton constituting the transitions
	 */
	private Automaton transitions;

	public GenericGame(int alphabetSize, Automaton player0Vertices,
			Automaton player1Vertices, Automaton initialVertices,
			Automaton safeVertices, Automaton transitions) {

		super();

		this.alphabetSize = alphabetSize;
		this.player0Vertices = player0Vertices;
		this.player1Vertices = player1Vertices;
		this.initialVertices = initialVertices;
		this.safeVertices = safeVertices;
		this.transitions = transitions;

	}

	@Override
	public int getAlphabetSize() {
		return alphabetSize;
	}

	@Override
	public Automaton getPlayer0Vertices() {
		return player0Vertices;
	}

	@Override
	public Automaton getPlayer1Vertices() {
		return player1Vertices;
	}

	@Override
	public Automaton getInitialVertices() {
		return initialVertices;
	}

	@Override
	public Automaton getSafeVertices() {
		return safeVertices;
	}

	@Override
	public Automaton getTransitions() {
		return transitions;
	}

}
