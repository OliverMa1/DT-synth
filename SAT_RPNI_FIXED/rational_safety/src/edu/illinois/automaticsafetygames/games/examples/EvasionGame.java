package edu.illinois.automaticsafetygames.games.examples;

import edu.illinois.automaticsafetygames.games.IGame;

import java.util.HashMap;
import java.util.Map;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;

/**
 * Two robots move in alternation on a two-dimensional grid, from one cell to an
 * adjecent cell. Each robot is controlled by one player. Player 0's objective
 * is to stay with his robot within a distance of two cells (Manhattan distance)
 * from Player 1's robot.
 * 
 * @author Daniel Neider
 *
 */
public class EvasionGame implements IGame {

	public Map<Character, String> getEncoding() {

		HashMap<Character, String> encoding = new HashMap<>();
		encoding.put((char) 0, "(1,1)");
		encoding.put((char) 1, "(0,1)");
		encoding.put((char) 2, "(1,0)");
		encoding.put((char) 3, "#");
		encoding.put((char) 4, "_");

		return encoding;

	}

	@Override
	public int getAlphabetSize() {
		return 4;
	}

	@Override
	public Automaton getPlayer0Vertices() {
		return new RegExp(
				"\u0000\u0000*(\u0001*|\u0002*)\u0003\u0000*(\u0001*|\u0002*)")
				.toAutomaton();
	}

	@Override
	public Automaton getPlayer1Vertices() {
		return new RegExp(
				"\u0001\u0000*(\u0001*|\u0002*)\u0003\u0000*(\u0001*|\u0002*)")
				.toAutomaton();
	}

	@Override
	public Automaton getInitialVertices() {
		return new RegExp("\u0000\u0003\u0002").toAutomaton();
	}

	@Override
	public Automaton getSafeVertices() {
		Automaton safe1 = new RegExp(
				"(\u0000|\u0001)\u0000*(\u0001+|\u0002+)\u0003\u0000*(\u0001*|\u0002*)")
				.toAutomaton();

		Automaton safe2 = new RegExp(
				"(\u0000|\u0001)\u0000*(\u0001*|\u0002*)\u0003\u0000*(\u0001+|\u0002+)")
				.toAutomaton();

		return safe1.union(safe2);

	}

	private Automaton end2ndLonger() {

		// Part 1
		Automaton end21 = new RegExp((char) 19 + "").toAutomaton();

		// Part 2
		Automaton end22 = new RegExp((char) 15 + "\u0000*\u0004").toAutomaton();

		// Part 3
		Automaton end23 = new RegExp((char) 15 + "\u0000*\u0001\u0006*\u0009")
				.toAutomaton();

		// Part 4
		Automaton end24 = new RegExp((char) 15 + "\u0000*\u0002" + (char) 12
				+ "*" + (char) 14).toAutomaton();

		// Part 5
		Automaton end25 = new RegExp((char) 16 + "\u0006*\u0009").toAutomaton();

		// Part 6
		Automaton end26 = new RegExp((char) 17 + "" + (char) 12 + "*"
				+ (char) 14).toAutomaton();

		return end21.union(end22).union(end23).union(end24).union(end25)
				.union(end26);

	}

	private Automaton end1stLonger() {

		// Part 1
		Automaton end11 = new RegExp("" + (char) 23).toAutomaton();

		// Part 2
		Automaton end12 = new RegExp("\u0003\u0000*" + (char) 20).toAutomaton();

		// Part 3
		Automaton end13 = new RegExp("\u0003\u0000*" + (char) 10 + ""
				+ (char) 12 + "*" + (char) 22).toAutomaton();

		// Part 4
		Automaton end14 = new RegExp("\u0003\u0000*\u0005\u0006*" + (char) 21)
				.toAutomaton();

		// Part 5
		Automaton end15 = new RegExp("\u0008\u0006*" + (char) 21).toAutomaton();

		// Part 6
		Automaton end16 = new RegExp((char) 13 + "" + (char) 12 + "*"
				+ (char) 22).toAutomaton();

		return end11.union(end12).union(end13).union(end14).union(end15)
				.union(end16);

	}

	private static Automaton endEqualLength() {
		return new RegExp("\u0000*(\u0006*|" + (char) 12 + "*)").toAutomaton();
	}

	private static Automaton beginningEqualLength() {
		return new RegExp("\u0000*(\u0006*|" + (char) 12 + "*)" + (char) 18)
				.toAutomaton();
	}

	@Override
	public Automaton getTransitions() {

		/*
		 * Player 0 right
		 */
		// 1. case
		Automaton p0right1 = new RegExp("\u0000*\u0001\u0006*" + (char) 18)
				.toAutomaton();
		p0right1 = p0right1.concatenate(endEqualLength());

		// 2. case
		Automaton p0right2 = new RegExp("\u0000*" + (char) 12 + "*" + (char) 13)
				.toAutomaton();
		p0right2 = p0right2.concatenate(end2ndLonger());

		// Combination
		Automaton p0right = p0right1.union(p0right2);

		/*
		 * Player 0 left
		 */
		// 1. case
		Automaton p0left1 = new RegExp("\u0000*\u0005\u0006*" + (char) 18)
				.toAutomaton();
		p0left1 = p0left1.concatenate(endEqualLength());

		// 2. case
		Automaton p0left2 = new RegExp("\u0000*" + (char) 12 + "*" + (char) 17)
				.toAutomaton();
		p0left2 = p0left2.concatenate(end1stLonger());

		// Combination
		Automaton p0left = p0left1.union(p0left2);

		/*
		 * Player 0 up
		 */
		Automaton p0up = new RegExp("\u0000*(\u0001\u0006*|" + (char) 12 + "*"
				+ (char) 14 + ")").toAutomaton();
		p0up = beginningEqualLength().concatenate(p0up);

		/*
		 * Player 0 down
		 */
		Automaton p0down = new RegExp("\u0000*(\u0005\u0006*|" + (char) 12
				+ "*" + (char) 22 + ")").toAutomaton();
		p0down = beginningEqualLength().concatenate(p0down);

		/*
		 * Player 1 right
		 */
		// 1. case
		Automaton p1right1 = new RegExp("\u0000*\u0006*\u0008").toAutomaton();
		p1right1 = p1right1.concatenate(end2ndLonger());

		// 2. case
		Automaton p1right2 = new RegExp("\u0000*\u0002" + (char) 12 + "*"
				+ (char) 18).toAutomaton();
		p1right2 = p1right2.concatenate(endEqualLength());

		// Combination
		Automaton p1right = p1right1.union(p1right2);

		/*
		 * Player 1 left
		 */
		Automaton p1left1 = new RegExp("\u0000*\u0006*" + (char) 16)
				.toAutomaton();
		p1left1 = p1left1.concatenate(end1stLonger());

		// 2. case
		Automaton p1left2 = new RegExp("\u0000*" + (char) 10 + "" + (char) 12
				+ "*" + (char) 18).toAutomaton();
		p1left2 = p1left2.concatenate(endEqualLength());

		// Combination
		Automaton p1left = p1left1.union(p1left2);

		/*
		 * Player 1 up
		 */
		Automaton p1up = new RegExp("\u0000*(\u0002" + (char) 12
				+ "*|\u0006*\u0009)").toAutomaton();
		p1up = beginningEqualLength().concatenate(p1up);

		/*
		 * Player 1 down
		 */
		Automaton p1down = new RegExp("\u0000*(\u0006*" + (char) 21 + "|"
				+ (char) 10 + "" + (char) 12 + "*)").toAutomaton();
		p1down = beginningEqualLength().concatenate(p1down);

		/*
		 * Combination
		 */
		Automaton p0 = p0left.union(p0right).union(p0up).union(p0down);
		p0 = Automaton.makeChar('\u0005').concatenate(p0);
		Automaton p1 = p1left.union(p1right).union(p1up).union(p1down);
		p1 = Automaton.makeChar('\u0001').concatenate(p1);
		Automaton transitions = p0.union(p1);

		return Automaton.minimize(transitions);

	}
}
