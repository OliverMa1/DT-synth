package edu.illinois.automaticsafetygames.games.examples;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import edu.illinois.automaticsafetygames.games.IGame;

/**
 * <p>
 * A finitely branching version of the <em>program repair game</em> described by
 * Beyene et al. [1].
 * </p>
 * 
 * <p>
 * Configuration of the program are triples <code>(pc, lock, got_lock)</code>
 * consisting of a program counter <code>pc</code> (integer value between 0 and
 * 4), a boolean variable <code>lock</code> representing a lock, and a
 * non-negative integer variable <code>got_lock</code>.
 * </p>
 * 
 * <p>
 * A configurations is encoded as word in the following way:
 * <ol>
 * <li>the program counter is encoded by a symbol in the set
 * <em>{0, ..., 4}</em>;</li>
 * <li>the is encoded in binary by either the symbol 0 or 1; and</li>
 * <li>the value of the variable <code>got_lock</code> is encoded in unary as a
 * finite (potentially empty) sequence of 1s.</li>
 * </ol>
 * </p>
 * 
 * <p>
 * In the game as formulated by Beyene et al., the variable
 * <code>got_lock</code> can be nondeterministically be set to an arbitrary
 * value, which results in an infinitely-branching. We turn the game into a
 * finitely-branching version by only allowing to change this value by at most
 * <code>max</code>, which can be specified when instantiating the game.
 * </p>
 * 
 * <ul>
 * <li>[1] Tewodros A. Beyene, Swarat Chaudhuri, Corneliu Popeea, and Andrey
 * Rybalchenko. A Constraint-Based Approach to Solving Games on Infinite Graphs.</li>
 * </ul>
 * 
 * @author Daniel Neider
 *
 */
public class ProgramRepairGame implements IGame {

	/**
	 * Default value for parameter <code>max</code>.
	 */
	public static final int MAX_DEFAULT = 10;

	/**
	 * Maximal number that the program variable <em>got_lock</em> can be
	 * increase or decreased.
	 */
	private int max;

	/**
	 * Creates a new instance of the repair game.
	 */
	public ProgramRepairGame() {
		max = MAX_DEFAULT;
	}

	/**
	 * Creates a new instance of the repair game.
	 * 
	 * @param max
	 *            maximal number that the program variable <em>got_lock</em> can
	 *            be increase or decreased
	 */
	public ProgramRepairGame(int max) {
		this.max = max;
	}

	@Override
	public int getAlphabetSize() {
		return 5;
	}

	/**
	 * Computes the convoluted 2-component symbol, given symbols for the first
	 * and second component.
	 * 
	 * @param first
	 *            Symbol for the first component
	 * 
	 * @param second
	 *            Symbol for the second component
	 * 
	 * @return Return the convoluted symbol.
	 */
	private char conv(int first, int second) {
		return (char) ((getAlphabetSize() + 1) * second + first);
	}

	@Override
	public Automaton getPlayer0Vertices() {

		return Automaton.minimize(Automaton.makeCharRange('\u0000', '\u0004')
				.concatenate(Automaton.makeCharRange('\u0000', '\u0001'))
				.concatenate(Automaton.makeChar('\u0001').repeat()));
	}

	@Override
	public Automaton getPlayer1Vertices() {
		return Automaton.makeEmpty();
	}

	@Override
	public Automaton getInitialVertices() {
		return Automaton.makeChar('\u0000').repeat(2, 2);
	}

	@Override
	public Automaton getSafeVertices() {

		Automaton t1 = new RegExp("\u0001\u0001\u0001*").toAutomaton();
		Automaton t2 = new RegExp("\u0003\u0000\u0001*").toAutomaton();

		return getPlayer0Vertices().minus(t1.union(t2));
	}

	@Override
	public Automaton getTransitions() {

		Automaton[] r = new Automaton[7];

		// Rho 0
		r[0] = new RegExp(conv(0, 1) + "(" + conv(0, 0) + "|" + conv(1, 1)
				+ ")" + conv(1, 1) + "*").toAutomaton();

		// Rho 1
		r[1] = new RegExp(conv(0, 2) + "(" + conv(0, 0) + "|" + conv(1, 1)
				+ ")" + conv(1, 1) + "*").toAutomaton();

		// Rho 2
		r[2] = new RegExp(conv(1, 2) + "" + conv(0, 1) + conv(1, 1) + "*("
				+ conv(1, 5) + "{0," + max + "}|" + conv(5, 1) + "{0," + max
				+ "})").toAutomaton();

		// Rho 3
		r[3] = new RegExp(conv(2, 3) + "(" + conv(0, 0) + "|" + conv(1, 1)
				+ ")" + conv(1, 1) + "+").toAutomaton();

		// Rho 4
		r[4] = new RegExp(conv(2, 4) + "(" + conv(0, 0) + "|" + conv(1, 1)
				+ ")").toAutomaton();

		// Rho 5
		r[5] = new RegExp(conv(3, 4) + "" + conv(1, 0) + "" + conv(1, 1) + "*")
				.toAutomaton();

		// Rho 6
		r[6] = new RegExp(conv(4, 0) + "(" + conv(0, 0) + "|" + conv(1, 1)
				+ ")" + conv(1, 1) + "*(" + conv(1, 5) + "{0," + max + "}|"
				+ conv(5, 1) + "{0," + max + "})").toAutomaton();

		// Union
		Automaton union = Automaton.makeEmpty();
		for (int i = 0; i < r.length; i++) {
			union = union.union(r[i]);
		}

		return Automaton.minimize(union);

	}

}
