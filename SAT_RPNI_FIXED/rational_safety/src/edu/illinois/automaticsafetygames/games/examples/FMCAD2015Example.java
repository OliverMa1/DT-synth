package edu.illinois.automaticsafetygames.games.examples;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import edu.illinois.automaticsafetygames.games.IGame;
import edu.illinois.automaticsafetygames.tools.Tools;

/**
 * <p>
 * Motion planning example as described in the FMCAD 2015 submission. The size
 * of the safe region is specified by two parameter <code>k</code> and
 * <code>kPrime</code>; <code>k</code> specifies the left edge of the safe
 * region while <code>kPrime</code> speifies the right edge. Another parameter
 * is <code>initial</code>, which specifies the starting position of the robot
 * </p>
 * 
 * <p>
 * Vertices of the game are of the form <em>(0 + 1)1*</em> where the first
 * symbol specifies the player that moves the robot next and the remaining word
 * encodes the position of the robot in unary.
 * </p>
 * 
 * @author Daniel Neider
 *
 */
public class FMCAD2015Example implements IGame {

	/**
	 * The position on which the safe region begins.
	 */
	private int k;

	/**
	 * Largest position of the safe region.
	 */
	private int kPrime;

	/**
	 * Initial position of the robot
	 */
	private int initial;

	/**
	 * Constructs the motion planning game described in the FMCAD 2015 paper
	 * with a finite safe region.
	 * 
	 * @param k
	 *            The starting point of the safe area.
	 * 
	 * @param kPrime
	 *            The max position of the safe region. Has to satisfy
	 *            <code>k <= kPrime</code>
	 * 
	 * @param initial
	 *            The initial position of the robot. Has to satisfy
	 *            <code>k <= initial</code> and <code>initial <= kPrime</code>.
	 * 
	 */
	public FMCAD2015Example(int k, int kPrime, int initial) {

		this.k = k;
		this.initial = initial;
		this.kPrime = kPrime;

		if (kPrime < k) {
			throw new RuntimeException(
					"kPrime has to be greater or equal than k");
		}

		if (initial < k || initial > kPrime) {
			throw new RuntimeException(
					"The initial position has to be inside the safe region (initial < k)");
		}

	}

	@Override
	public int getAlphabetSize() {
		return 2;
	}

	@Override
	public Automaton getPlayer0Vertices() {

		return Automaton.minimize(new RegExp("\u0000\u0001*").toAutomaton());

	}

	@Override
	public Automaton getPlayer1Vertices() {

		return Automaton.minimize(new RegExp("\u0001\u0001*").toAutomaton());

	}

	@Override
	public Automaton getInitialVertices() {
		return Automaton.minimize(new RegExp("\u0000").toAutomaton()
				.concatenate(
						new RegExp("\u0001").toAutomaton().repeat(initial,
								initial)));
	}

	@Override
	public Automaton getSafeVertices() {

		Automaton aux = new RegExp("\u0001").toAutomaton().repeat(k, kPrime);

		return Automaton.minimize(new RegExp("\u0000|\u0001").toAutomaton()
				.concatenate(aux));
	}

	@Override
	public Automaton getTransitions() {

		// Player 0
		Automaton p0 = new RegExp("\u0003\u0004*\u0005?").toAutomaton();

		// Player 1
		Automaton p1 = new RegExp("\u0001\u0004*\u0007?").toAutomaton();

		return Automaton.minimize(p0.union(p1));

	}

	/**
	 * Constructs a winning set of the game.
	 * 
	 * @return Returns a safe set of the game.
	 */
	public Automaton getWinningSet() {

		// Player 0 vertices that are ok
		Automaton p0 = new RegExp("\u0000").toAutomaton()
				.concatenate(
						new RegExp("\u0001").toAutomaton().repeat(initial,
								initial + 1));

		// Player 1 vertices that are ok
		Automaton p1 = new RegExp("\u0001").toAutomaton().concatenate(
				new RegExp("\u0001").toAutomaton().repeat(initial + 1,
						initial + 1));

		return Automaton.minimize(p0.union(p1));

	}

	/**
	 * Test the game.
	 * 
	 * @param args
	 *            Command line arguments
	 */
	public static void main(String[] args) {

		FMCAD2015Example game = new FMCAD2015Example(50000, 1, 1);

		System.out.println("Size is " + Tools.sizeOfGame(game));

		// AutomatonTeacher teacher = new AutomatonTeacher(
		// game.getPlayer0Vertices(), game.getPlayer1Vertices(),
		// game.getInitialVertices(), game.getSafeVertices(),
		// game.getTransitions(), game.getAlphabetSize());
		//
		// System.out.println("Size of solution is "
		// + game.getSafeSet().getNumberOfStates());
		//
		// System.out.println("Contains initial? "
		// + (teacher.containsInitial(game.getSafeSet()) == null));
		//
		// System.out.println("Is safe? "
		// + (teacher.isSafe(game.getSafeSet()) == null));
		//
		// ImplicationCounterexample ex = teacher.isExistentiallyClosed(game
		// .getSafeSet());
		// System.out.println("Is existentially closed? " + (ex == null));
		// if (ex != null) {
		// System.out.print(Tools.toReadableString(ex.antecedent) + " => ");
		// for (String consequent : ex.consequent) {
		// System.out.print("(" + Tools.toReadableString(consequent)
		// + ") ");
		// }
		// System.out.println();
		// }
		//
		// ImplicationCounterexample un = teacher.isUniversallyClosed(game
		// .getSafeSet());
		// System.out.println("Is universally closed? " + (un == null));
		// if (un != null) {
		// System.out.print(Tools.toReadableString(un.antecedent) + " => ");
		// for (String consequent : un.consequent) {
		// System.out.print("(" + Tools.toReadableString(consequent)
		// + ") ");
		// }
		// System.out.println();
		// }

	}
}
