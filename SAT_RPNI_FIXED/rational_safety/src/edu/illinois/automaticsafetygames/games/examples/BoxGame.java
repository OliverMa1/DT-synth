package edu.illinois.automaticsafetygames.games.examples;

import edu.illinois.automaticsafetygames.games.IGame;
import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;

public class BoxGame implements IGame {

	@Override
	public int getAlphabetSize() {
		return 2;
	}

	@Override
	public Automaton getPlayer0Vertices() {
		return new RegExp("\u0000\u0000*\u0001\u0000*").toAutomaton();
	}

	@Override
	public Automaton getPlayer1Vertices() {
		return new RegExp("\u0001\u0000*\u0001\u0000*").toAutomaton();
	}

	@Override
	public Automaton getInitialVertices() {
		return new RegExp("\u0001\u0000\u0001\u0000").toAutomaton();
	}

	@Override
	public Automaton getSafeVertices() {
		return new RegExp("(\u0000|\u0001)\u0000{0,2}\u0001\u0000*")
				.toAutomaton();
	}

	@Override
	public Automaton getTransitions() {

		Automaton first = new RegExp(
				"\u0000*\u0001(\u0005|(\u0003\u0000*\u0002))").toAutomaton();
		Automaton second = new RegExp(
				"\u0000*\u0003(\u0007|(\u0001\u0000*\u0006))").toAutomaton();
		Automaton third = new RegExp("\u0000*\u0004\u0000*(\u0002|\u0006)")
				.toAutomaton();

		Automaton result = new RegExp("\u0003").toAutomaton().concatenate(
				first.union(second));
		result = result.union(new RegExp("\u0001").toAutomaton().concatenate(
				third));
		result.minimize();

		return result;
	}

}
