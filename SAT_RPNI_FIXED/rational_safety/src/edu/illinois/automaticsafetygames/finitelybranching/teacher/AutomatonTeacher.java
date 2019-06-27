package edu.illinois.automaticsafetygames.finitelybranching.teacher;

import edu.illinois.automaticsafetygames.games.IGame;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.illinois.automaticsafetygames.tools.Tools;
import dk.brics.automaton.Automaton;
import dk.brics.automaton.BasicAutomata;

public class AutomatonTeacher implements ITeacher {

	/**
	 * Decides whether to output intermediate results
	 */
	private static final boolean debugOutput = false;

	/**
	 * Automaton accepting the vertices of Player 0
	 */
	private Automaton player0Vertices;

	/**
	 * Automaton accepting the vertices of Player 1
	 */
	private Automaton player1Vertices;

	/**
	 * Automaton accepting the initial vertices
	 */
	private Automaton initialVertices;

	/**
	 * Automaton accepting the safe vertices
	 */
	private Automaton safeVertices;

	/**
	 * Automaton defining the transitions
	 */
	private Automaton transitions;

	/**
	 * Automaton defining the padded reversed transitions
	 */
	private Automaton reversedTransitions = null;

	/**
	 * The alphabet size over which the automata work (the automaton defining
	 * the transitions works over an alphabet of pairs).
	 * 
	 * Note that the padding symbol is assumed to be
	 * <code>(char)alphabetSize</code>.
	 */
	private int alphabetSize;

	/**
	 * Creates a new rational safety game edu.illinois.automaticsafetygames.finitelybranching.teacher that answers queries with
	 * respect to the given automata.
	 * 
	 * @param player0Vertices
	 *            An automaton accepting the vertices of Player 0
	 * @param player1Vertices
	 *            An automaton accepting the vertices of Player 1
	 * @param initialVertices
	 *            An automaton accepting the initial vertices
	 * @param safeVertices
	 *            An automaton accepting the safe vertices
	 * @param transitions
	 *            An automaton defining the edge relation
	 * @param alphabetSize
	 *            The alphabet size assumed when computing reversals,
	 *            complements, etc.
	 */
	public AutomatonTeacher(Automaton player0Vertices,
			Automaton player1Vertices, Automaton initialVertices,
			Automaton safeVertices, Automaton transitions, int alphabetSize) {

		// Check parameter
		if (player0Vertices == null) {
			throw new IllegalArgumentException(
					"player0Vertices must not be null");
		}
		if (player1Vertices == null) {
			throw new IllegalArgumentException(
					"player1Vertices must not be null");
		}
		if (initialVertices == null) {
			throw new IllegalArgumentException(
					"initialVertices must not be null");
		}
		if (safeVertices == null) {
			throw new IllegalArgumentException("safeVertices must not be null");
		}
		if (transitions == null) {
			throw new IllegalArgumentException("transitions must not be null");
		}
		if (alphabetSize <= 0) {
			throw new IllegalArgumentException(
					"alphabetSize must be greater than 0");
		}

		// Store arguments
		this.player0Vertices = player0Vertices;
		this.player1Vertices = player1Vertices;
		this.initialVertices = initialVertices;
		this.safeVertices = safeVertices;
		this.transitions = transitions;
		this.alphabetSize = alphabetSize;

		// Reverse transducer to obtain E^-1
		reverseTransitions();

		// Minimize
		this.player0Vertices.minimize();
		this.player1Vertices.minimize();
		this.initialVertices.minimize();
		this.safeVertices.minimize();
		// transducer.minimize();
		this.reversedTransitions.minimize();

	}

	@Override
	public String containsInitial(Automaton conjecture) {

		Automaton tmp = initialVertices.minus(conjecture);

		if (tmp.isEmpty()) {
			return null;
		} else {
			return tmp.getShortestExample(true);
		}

	}

	@Override
	public String isSafe(Automaton conjecture) {

		Automaton tmp = conjecture.minus(safeVertices);

		if (tmp.isEmpty()) {
			return null;

		} else {
			return tmp.getShortestExample(true);
		}

	}

	@Override
	public ImplicationCounterexample isExistentiallyClosed(Automaton conjecture) {

		/*
		 * 1) T1 = E^-1(L(C))
		 */
		Automaton t1 = computeImage(reversedTransitions, conjecture,
				alphabetSize);

		if (debugOutput) {
			System.out.println(String.format("T1 = E^-1(L(C))%n%1$s",
					Tools.automatonToDot(t1)));
		}

		/*
		 * 2) T2 = V0 \ T1
		 */
		Automaton t2 = player0Vertices.minus(t1);

		if (debugOutput) {
			System.out.println(String.format("T2 = V0 \\ T1%n%1$s",
					Tools.automatonToDot(t2)));
		}

		/*
		 * 3) T3 = L(C) \cap T2
		 */
		Automaton t3 = conjecture.intersection(t2);

		if (debugOutput) {
			System.out.println(String.format("T3 = L(C) \\cap T2%n%1$s",
					Tools.automatonToDot(t3)));
		}

		/*
		 * Check for counterexample
		 */
		if (t3.isEmpty()) {
			return null;
		} else {

			// Extract a antecedent of a counterexample
			String antecedent = t3.getShortestExample(true);

			// Extract consequent for the given antecedent
			Automaton tmp1 = BasicAutomata.makeString(antecedent);
			Automaton tmp2 = computeImage(transitions, tmp1, alphabetSize);

			if (!tmp2.isFinite()) {
				throw new RuntimeException(
						"Exitentially closed: result is not finite");
			}

			return new ImplicationCounterexample(antecedent,
					tmp2.getFiniteStrings());

		}

	}

	@Override
	// TODO: Test this method (has not been done so far)
	public ImplicationCounterexample isUniversallyClosed(Automaton conjecture) {

		/*
		 * 1) T1 = (V0 \cup V1) \ L(C)
		 */
		// TODO: Move computation of V0 \cup V1 out
		Automaton t1 = (player0Vertices.union(player1Vertices))
				.minus(conjecture);

		if (debugOutput) {
			System.out.println(String.format(
					"Universal closedness: T1 = (V0 \\cup V1) \\ L(C)%n%1$s",
					Tools.automatonToDot(t1)));
		}

		/*
		 * 2) T2 = E^-1(T1)
		 */
		Automaton t2 = computeImage(reversedTransitions, t1, alphabetSize);

		if (debugOutput) {
			System.out.println(String.format(
					"Universal closedness: T2 = E^-1(T1)%n%1$s",
					Tools.automatonToDot(t2)));
		}

		/*
		 * 3) T3 = V1 \cap L(C) \cap T2
		 */
		Automaton t3 = player1Vertices.intersection(conjecture)
				.intersection(t2);

		if (debugOutput) {
			System.out.println(String.format(
					"Universal closedness: T3 = V1 \\cap L(C) \\cap T2%n%1$s",
					Tools.automatonToDot(t3)));
		}

		/*
		 * Check for counterexample
		 */
		if (t3.isEmpty()) {
			return null;
		} else {

			// Extract a antecedent of a counterexample
			String antecedent = t3.getShortestExample(true);

			// Extract consequent for the given antecedent
			Automaton tmp1 = BasicAutomata.makeString(antecedent);
			Automaton tmp2 = computeImage(transitions, tmp1, alphabetSize);

			if (!tmp2.isFinite()) {
				throw new RuntimeException(
						"Universally closed: result is not finite");
			}

			return new ImplicationCounterexample(antecedent,
					tmp2.getFiniteStrings());

		}

	}

	@Override
	public int getAlphabetSize() {

		return alphabetSize;

	}

	/**
	 * Given a transducer defining a relation E and an automaton accepting a
	 * language L, computes a deterministic automaton accepting the image of L
	 * under E (i.e., E(L)).
	 * 
	 * @param transducer
	 * @param aut
	 * @param alphabetSize
	 * @return
	 */
	public static Automaton computeImage(Automaton transducer, Automaton aut,
			int alphabetSize) {

		/*
		 * 1) Paddify automaton
		 */
		aut = aut.concatenate(BasicAutomata.makeChar((char) alphabetSize)
				.repeat());

		if (debugOutput) {
			System.out.println(String.format("Padding%n%1$s",
					Tools.automatonToDot(aut)));
		}

		/*
		 * 2) Cylindrify automaton
		 */
		// Create map for substitution
		// TODO Move outside
		HashMap<Character, Set<Character>> map = new HashMap<Character, Set<Character>>(
				2 * (alphabetSize + 1));

		for (int i = 0; i <= alphabetSize; i++) {

			HashSet<Character> entry = new HashSet<Character>(
					2 * (alphabetSize + 1));

			for (int j = 0; j <= alphabetSize; j++) {
				entry.add((char) (j * (alphabetSize + 1) + i));
			}

			map.put((char) i, entry);

		}

		/*
		 * for (Entry<Character, Set<Character>> e : map.entrySet()) {
		 * 
		 * System.out.print("Mapping " + (int) e.getKey() + " to ["); for (int k
		 * : e.getValue()) { System.out.print(k + " "); }
		 * System.out.println("]");
		 * 
		 * }
		 */

		// Perform substitution
		aut = aut.subst(map);

		if (debugOutput) {
			System.out.println(String.format("Cylindrification%n%1$s",
					Tools.transducerToDot(aut, alphabetSize)));
		}

		/*
		 * 3) Compute intersection with transducer
		 */
		Automaton result = aut.intersection(transducer);

		if (debugOutput) {
			System.out.println(String.format("Intersection%n%1$s",
					Tools.transducerToDot(result, alphabetSize)));
		}

		/*
		 * 4) Projection on second component
		 */
		// Create substitution map
		// TODO Move outside
		map.clear();

		for (int i = 0; i <= alphabetSize; i++) {
			for (int j = 0; j <= alphabetSize; j++) {

				HashSet<Character> entry = new HashSet<Character>(1);
				entry.add((char) j);
				map.put((char) (j * (alphabetSize + 1) + i), entry);

			}
		}

		/*
		 * for (Entry<Character, Set<Character>> e : map.entrySet()) {
		 * 
		 * System.out.print("Mapping " + (int) e.getKey() + " to ["); for (int k
		 * : e.getValue()) { System.out.print(k + " "); }
		 * System.out.println("]");
		 * 
		 * }
		 */

		// Substitute
		result = result.subst(map);

		if (debugOutput) {
			System.out.println(String.format("Projection%n%1$s",
					Tools.automatonToDot(result)));
		}

		/*
		 * 5) Removal of padding symbol
		 */
		result = result.subst((char) alphabetSize, "");

		/*
		 * 6) Final stuff
		 */
		result.minimize();

		if (debugOutput) {
			System.out.println(String.format("Image%n%1$s",
					Tools.automatonToDot(result)));
		}

		return result;

	}

	/**
	 * Reverses the transition relation with respect to the given alphabet site.
	 */
	private void reverseTransitions() {

		// 1. Create mapping to inverse stuff
		HashMap<Character, Set<Character>> map = new HashMap<Character, Set<Character>>(
				2 * (alphabetSize + 1));
		for (int i = 0; i <= alphabetSize; i++) {
			for (int j = 0; j <= alphabetSize; j++) {

				HashSet<Character> entry = new HashSet<>(1);
				entry.add((char) (i * (alphabetSize + 1) + j));
				map.put((char) (j * (alphabetSize + 1) + i), entry);

			}
		}

		// for (Entry<Character, Set<Character>> e : map.entrySet()) {
		//
		// System.out.print("Mapping " + (int) e.getKey() + " to [");
		// for (int k : e.getValue()) {
		// System.out.print(k + " ");
		// }
		// System.out.println("]");
		//
		// }

		// 2. Apply
		reversedTransitions = transitions.subst(map);

	}

	public static boolean isSane(IGame game) {

		if (!game.getPlayer0Vertices().intersection(game.getPlayer1Vertices())
				.isEmpty()) {

			System.err.println("V0 \\cap V1 != \\emptyset");
			return false;

		}

//		if (!game.getSafeVertices().subsetOf(
//				game.getPlayer0Vertices().union(game.getPlayer1Vertices()))) {
//
//			System.err.println("F \\not\\subseteq (V0 \\cup V1)");
//			return false;
//
//		}

		if (!game.getInitialVertices().subsetOf(game.getSafeVertices())) {

			System.err.println("I \\not\\subseteq F");
			return false;

		}

		if (!computeImage(game.getTransitions(), game.getPlayer0Vertices(),
				game.getAlphabetSize()).subsetOf(
				game.getPlayer0Vertices().union(game.getPlayer1Vertices()))) {

			System.err.println("E(V0) \\not\\subseteq (V0 \\cup V1)");
			return false;

		}

		if (!computeImage(game.getTransitions(), game.getPlayer1Vertices(),
				game.getAlphabetSize()).subsetOf(
				game.getPlayer0Vertices().union(game.getPlayer1Vertices()))) {

			System.err.println("E(V1) \\not\\subseteq (V0 \\cup V1)");
			return false;
		}

		return true;

	}

}