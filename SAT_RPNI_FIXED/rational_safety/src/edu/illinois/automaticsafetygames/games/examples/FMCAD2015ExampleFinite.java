package edu.illinois.automaticsafetygames.games.examples;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import edu.illinois.automaticsafetygames.games.IGame;
import edu.illinois.automaticsafetygames.tools.Tools;

/**
 * <p>
 * Finite version of the motion planning example described in the FMCAD 2015
 * submission. The size of the grid is specified by the parameter
 * <code>max</code> (i.e., the size of the grid is <code>max + 1</code>). Other
 * parameters are <code>k</code> and <code>initial</code>, which specify the
 * beginning of the safe region and the starting position of the robot,
 * respectively.
 * </p>
 * 
 * <p>
 * This class can construct both a finite version and an infinite version of the
 * game. To construct an infinite version, specify <code>max = 0</code> when
 * instantiating the game.
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
public class FMCAD2015ExampleFinite implements IGame {

	/**
	 * The position on which the safe region begins.
	 */
	private int k;

	/**
	 * Initial position of the robot
	 */
	private int initial;

	/**
	 * Largest index of the grid (i.e., size of the grid - 1).
	 */
	private int max;

	/**
	 * Constructs the motion planning game described in the FMCAD 2015 paper.
	 * 
	 * @param max
	 *            The max position of the grid. If <code>max < 0</code>, then
	 *            the grid is of infinite max
	 * @param k
	 *            The starting point of the safe area. If <code>max => 0</code>,
	 *            then <code>k <= max</code>
	 * @param initial
	 *            The initial position of the robot. Has to satisfy
	 *            <code>k <= initial</code> and <code>initial <= max</code> if
	 *            <code>max => 0</code>
	 * 
	 */
	public FMCAD2015ExampleFinite(int max, int initialPosition, int k) {

		this.k = k;
		this.initial = initialPosition;
		this.max = max;

		if (initialPosition < k) {
			throw new RuntimeException(
					"The initial position has to be inside the safe region (initial < k)");
		}

		if (max >= 0 && initial > max) {
			throw new RuntimeException(
					"if max >= 0, initial has to be less than or equal to max");
		}

	}

	@Override
	public int getAlphabetSize() {
		return 2;
	}

	@Override
	public Automaton getPlayer0Vertices() {

		Automaton aux = max >= 0 ? new RegExp("\u0001").toAutomaton().repeat(0,
				max) : new RegExp("\u0001*").toAutomaton();

		return Automaton.minimize(new RegExp("\u0000").toAutomaton()
				.concatenate(aux));
	}

	@Override
	public Automaton getPlayer1Vertices() {

		Automaton aux = max >= 0 ? new RegExp("\u0001").toAutomaton().repeat(0,
				max) : new RegExp("\u0001*").toAutomaton();

		return Automaton.minimize(new RegExp("\u0001").toAutomaton()
				.concatenate(aux));

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

		Automaton aux = max >= 0 ? new RegExp("\u0001").toAutomaton().repeat(k,
				max) : new RegExp("\u0001").toAutomaton().repeat(k);

		return Automaton.minimize(new RegExp("\u0000|\u0001").toAutomaton()
				.concatenate(aux));
	}

	/**
	 * Constructs the transition relation for the case that the game graph is
	 * infinite.
	 * 
	 * @return Returns the transitions relation.
	 */
	public Automaton getTransitionsInfiniteCase() {

		// Player 0
		Automaton p0 = new RegExp("\u0003\u0004*\u0005?").toAutomaton();

		// Player 1
		Automaton p1 = new RegExp("\u0001\u0004*\u0007?").toAutomaton();

		return Automaton.minimize(p0.union(p1));
	}

	/**
	 * Constructs the transition relation for the case that the game graph is
	 * finite.
	 * 
	 * @return Returns the transitions relation.
	 */
	public Automaton getTransitionsFiniteCase() {

		// Player 0
		Automaton p0 = Automaton.makeChar('\u0004').repeat(0, max);
		if (max >= 1) {
			p0 = p0.union(Automaton.makeChar('\u0004').repeat(0, max - 1)
					.concatenate(Automaton.makeChar('\u0005')));
		}
		p0 = Automaton.makeChar('\u0003').concatenate(p0);

		// Player 1
		Automaton p1 = Automaton.makeChar('\u0004').repeat(0, max);
		if (max >= 1) {
			p1 = p1.union(Automaton.makeChar('\u0004').repeat(0, max - 1)
					.concatenate(Automaton.makeChar('\u0007')));
		}
		p1 = Automaton.makeChar('\u0001').concatenate(p1);

		return Automaton.minimize(p0.union(p1));

	}

	@Override
	public Automaton getTransitions() {
		return max >= 0 ? getTransitionsFiniteCase()
				: getTransitionsInfiniteCase();
	}

	/**
	 * Constructs a winning set of the game.
	 * 
	 * @return Returns a winning set of the game.
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

		FMCAD2015ExampleFinite game = new FMCAD2015ExampleFinite(50000, 1, 1);

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
