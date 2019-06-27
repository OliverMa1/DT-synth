package edu.illinois.automaticsafetygames.games.examples;

import edu.illinois.automaticsafetygames.games.IGame;
import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;

public class SimpleGame implements IGame {

	@Override
	public int getAlphabetSize() {
		return 2;
	}

	@Override
	public Automaton getPlayer0Vertices() {
		return new RegExp("\u0001*").toAutomaton();
	}

	@Override
	public Automaton getPlayer1Vertices() {
		return Automaton.makeEmpty();
	}

	@Override
	public Automaton getInitialVertices() {
		return new RegExp("\u0001\u0001").toAutomaton().union(Automaton.makeEmptyString());
	}

	@Override
	public Automaton getSafeVertices() {
		return new RegExp("()|\u0001|\u0001\u0001|\u0001\u0001\u0001")
				.toAutomaton();
	}

	@Override
	public Automaton getTransitions() {
		return new RegExp("\u0004*(\u0005|\u0007)").toAutomaton();
	}

}
