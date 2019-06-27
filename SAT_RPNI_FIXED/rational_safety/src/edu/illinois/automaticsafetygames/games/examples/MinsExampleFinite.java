package edu.illinois.automaticsafetygames.games.examples;

import edu.illinois.automaticsafetygames.games.IGame;
import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;

public class MinsExampleFinite implements IGame {

	/**
	 * The number of fields in the x dimension
	 */
	private int xDimension;

	/**
	 * The number of fields in the y dimension
	 */
	private int yDimension;

	public MinsExampleFinite(int xDimension, int yDimension) {
		this.xDimension = xDimension;
		this.yDimension = yDimension;
	}

	@Override
	public int getAlphabetSize() {
		return 2;
	}

	@Override
	public Automaton getPlayer0Vertices() {
		return new RegExp(
				"\u0000\u0000*\u0001\u0000*\u0001\u0000*\u0001\u0000*")
				.toAutomaton();
	}

	@Override
	public Automaton getPlayer1Vertices() {
		return new RegExp(
				"\u0001\u0000*\u0001\u0000*\u0001\u0000*\u0001\u0000*")
				.toAutomaton();
	}

	@Override
	public Automaton getInitialVertices() {
		return new RegExp("\u0001\u0000\u0001\u0000\u0001\u0001").toAutomaton();
	}

	@Override
	public Automaton getSafeVertices() {

		Automaton a = Automaton.makeEmpty();

		Automaton z = Automaton.makeChar('\u0000');
		Automaton o = Automaton.makeChar('\u0001');

		for (int p0x = 0; p0x < xDimension; p0x++) {
			for (int p0y = 0; p0y < yDimension; p0y++) {
				for (int p1x = 0; p1x < xDimension; p1x++) {
					for (int p1y = 0; p1y < yDimension; p1y++) {

						if (!(p0x == p1x && p0y == p1y)) {
							a = a.union(z.repeat(p0x, p0x).concatenate(o)
									.concatenate(z.repeat(p0y, p0y))
									.concatenate(o)
									.concatenate(z.repeat(p1x, p1x))
									.concatenate(o)
									.concatenate(z.repeat(p1y, p1y)));
						}

					}
				}
			}
		}

		a = new RegExp("\u0000|\u0001").toAutomaton().concatenate(a);
		a.minimize();

		return a;

	}

	@Override
	public Automaton getTransitions() {

		// Minus
		Automaton minus = new RegExp("(\u0001\u0000*\u0003)|\u0004")
				.toAutomaton();

		// Minus end
		Automaton minusEnd = new RegExp("(\u0001\u0000*\u0006)|\u0007")
				.toAutomaton();

		// Plus
		Automaton plus = new RegExp("(\u0003\u0000*\u0001)|\u0004")
				.toAutomaton();

		// Plus end
		Automaton plusEnd = new RegExp("(\u0003\u0000*\u0002)|\u0005")
				.toAutomaton();

		// P0 left
		Automaton p0left = new RegExp("\u0000*\u0003").toAutomaton()
				.concatenate(minus.repeat(2, 2)).concatenate(minusEnd);

		// P0 right
		Automaton p0right = new RegExp("\u0000*\u0001").toAutomaton()
				.concatenate(plus.repeat(2, 2)).concatenate(plusEnd);

		// P0 down
		Automaton p0down = new RegExp("\u0000*\u0004\u0000*\u0003")
				.toAutomaton().concatenate(minus).concatenate(minusEnd);

		// P0 up
		Automaton p0up = new RegExp("\u0000*\u0004\u0000*\u0001").toAutomaton()
				.concatenate(plus).concatenate(plusEnd);

		// P1 left
		Automaton p1left = new RegExp("\u0000*\u0004\u0000*\u0004\u0000*\u0003")
				.toAutomaton().concatenate(minusEnd);

		// P1 right
		Automaton p1right = new RegExp(
				"\u0000*\u0004\u0000*\u0004\u0000*\u0001").toAutomaton()
				.concatenate(plusEnd);

		// P1 down
		Automaton p1down = new RegExp(
				"\u0000*\u0004\u0000*\u0004\u0000*\u0004\u0000*\u0006")
				.toAutomaton();

		// P1 up
		Automaton p1up = new RegExp(
				"\u0000*\u0004\u0000*\u0004\u0000*\u0004\u0000*\u0002")
				.toAutomaton();

		// Combination
		Automaton p0 = Automaton.makeChar('\u0003').concatenate(
				p0left.union(p0right).union(p0down).union(p0up));

		Automaton p1 = Automaton.makeChar('\u0001').concatenate(
				p1left.union(p1right).union(p1down).union(p1up));

		Automaton combined = p0.union(p1);
		combined.minimize();

		return combined;
	}

}
