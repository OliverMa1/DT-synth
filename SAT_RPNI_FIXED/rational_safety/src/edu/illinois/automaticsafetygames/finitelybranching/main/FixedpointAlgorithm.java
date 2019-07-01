package edu.illinois.automaticsafetygames.finitelybranching.main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import dk.brics.automaton.Automaton;
import edu.illinois.automaticsafetygames.finitelybranching.teacher.AutomatonTeacher;
import edu.illinois.automaticsafetygames.games.IGame;
import edu.illinois.automaticsafetygames.games.examples.FMCAD2015ExampleMixed;
import edu.illinois.automaticsafetygames.games.examples.*;
import edu.illinois.automaticsafetygames.tools.Tools;
public class FixedpointAlgorithm {

	public static Automaton computeFixedpoint(Automaton aut,
			Automaton player0Vertices, Automaton player1Vertices,
			Automaton transitions, int alphabetSize) {

		Automaton reversedTransitions = reverseTransitions(transitions,
				alphabetSize);

		Automaton last = null;
		Automaton next = Automaton.minimize(aut);

		// System.out.println(Tools.automatonToDot(next));

		@SuppressWarnings("unused")
		int i = 0;

		do {

			// System.out.println("i=" + ++i);

			last = next;
			next = next.intersection(predecessors(next, player0Vertices,
					player1Vertices, reversedTransitions, alphabetSize));

			next = Automaton.minimize(next);

			// System.out.println("\n" + Tools.automatonToDot(next));

		} while (!last.equals(next));

		// System.out.println("\n" + Tools.automatonToDot(next));

		return next;

	}

	public static Automaton predecessors(Automaton aut,
			Automaton player0Vertices, Automaton player1Vertices,
			Automaton reversedTransitions, int alphabetSize) {

		// Player 0
		// V0 \cap E^-1 (X)
		Automaton p0pred = AutomatonTeacher.computeImage(reversedTransitions,
				aut, alphabetSize).intersection(player0Vertices);

		// Player 1
		// V1 \ E^-1((V0 \cup V1) \ X)
		Automaton p1pred = player1Vertices.minus(AutomatonTeacher.computeImage(
				reversedTransitions,
				(player0Vertices.union(player1Vertices)).minus(aut),
				alphabetSize));

		return p0pred.union(p1pred);

	}

	/**
	 * Reverses the transition relation with respect to the given alphabet site.
	 */
	private static Automaton reverseTransitions(Automaton transitions,
			int alphabetSize) {

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

		// 2. Apply and return
		return transitions.subst(map);

	}

	public static void main(String[] args) {
                long startTime = System.nanoTime(); 

IGame game = new GridWorldSequence1D(200);

		Automaton result = computeFixedpoint(game.getSafeVertices(), game.getPlayer0Vertices(),
				game.getPlayer1Vertices(), game.getTransitions(),
				game.getAlphabetSize());
                System.out.println(TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS) + " ");



	}

}
