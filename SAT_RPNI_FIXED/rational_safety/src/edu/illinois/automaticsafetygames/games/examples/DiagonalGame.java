package edu.illinois.automaticsafetygames.games.examples;

import edu.illinois.automaticsafetygames.games.IGame;

import java.util.HashMap;
import java.util.Map;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;

public class DiagonalGame implements IGame {

	public static final int DEFAULT_WIDTH = 2;
	
	private int width;

	public DiagonalGame() {
		this(DEFAULT_WIDTH);
	}
	
	public DiagonalGame(int width) {

		if (width <= 0) {
			throw new IllegalArgumentException(
					"width has to be greater than than 0");
		}

		this.width = width;

	}

	public Map<Character, String> getEncoding() {

		HashMap<Character, String> encoding = new HashMap<>();
		encoding.put((char) 0, "(1,1)");
		encoding.put((char) 1, "(0,1)");
		encoding.put((char) 2, "(1,0)");
		encoding.put((char) 3, "_");

		return encoding;

	}

	@Override
	public int getAlphabetSize() {
		return 3;
	}

	@Override
	public Automaton getPlayer0Vertices() {
		return new RegExp("\u0000\u0000*(\u0001*|\u0002*)").toAutomaton();
	}

	@Override
	public Automaton getPlayer1Vertices() {
		return new RegExp("\u0001\u0000*(\u0001*|\u0002*)").toAutomaton();
	}

	@Override
	public Automaton getInitialVertices() {
		return new RegExp("\u0000\u0000\u0000\u0000\u0000\u0000\u0002")
				.toAutomaton();
	}

	@Override
	public Automaton getSafeVertices() {
		return new RegExp("(\u0000|\u0001)\u0000*(\u0001{0," + width
				+ "}|\u0002{0," + width + "})").toAutomaton();
	}

	@Override
	public Automaton getTransitions() {

		// Player 0 left
		Automaton p0left = new RegExp("\u0000*(\u0001\u0005*|" + (char) 10
				+ "*" + (char) 11 + ")").toAutomaton();

		// Player 0 right
		Automaton p0right = new RegExp("\u0000*(\u0004\u0005*|" + (char) 10
				+ "*" + (char) 14 + ")").toAutomaton();

		// Player 0
		Automaton p0 = new RegExp("\u0004").toAutomaton().concatenate(
				p0left.union(p0right));

		// Player 1 up
		Automaton p1up = new RegExp("\u0000*(\u0005*\u0007|\u0002" + (char) 10
				+ "*)").toAutomaton();

		// Player 1 down
		Automaton p1down = new RegExp("\u0000*(\u0005*" + (char) 13 + "|\u0008"
				+ (char) 10 + "*)").toAutomaton();

		// Player 1
		Automaton p1 = new RegExp("\u0001").toAutomaton().concatenate(
				p1up.union(p1down));

		return Automaton.minimize(p0.union(p1));
	}

}
