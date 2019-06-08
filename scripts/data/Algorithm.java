package edu.illinois.automaticsafetygames.finitelybranching.main;

import java.util.HashMap;
import java.util.Map.Entry;

import dk.brics.automaton.Automaton;
import edu.illinois.automaticsafetygames.finitelybranching.learner.ILearner;
import edu.illinois.automaticsafetygames.finitelybranching.learner.RPNILearner;
import edu.illinois.automaticsafetygames.finitelybranching.learner.Z3LearnerSAT;
import edu.illinois.automaticsafetygames.finitelybranching.teacher.AutomatonTeacher;
import edu.illinois.automaticsafetygames.finitelybranching.teacher.ITeacher;
import edu.illinois.automaticsafetygames.finitelybranching.teacher.ImplicationCounterexample;
import edu.illinois.automaticsafetygames.games.IGame;
import edu.illinois.automaticsafetygames.games.examples.*;
import edu.illinois.automaticsafetygames.tools.StopWatch;
import edu.illinois.automaticsafetygames.tools.Tools;

/**
 * Main Algorithm that implements the learning loop. Sets up the teacher and
 * learner and executes the learning. Also records statistics.
 * 
 * @author Daniel Neider
 *
 */
@SuppressWarnings("unused")
public class Algorithm {

	/**
	 * <p>
	 * Performs the learning.
	 * </p>
	 *
	 * <p>
	 * If <code>vertices</code> are given, a conjecture is intersected with this
	 * vertices before it is submitted to a query. This helps avoiding queries
	 * and counterexamples as the learner does not need to learn which words are
	 * valid vertices and which are not.
	 * </p>
	 * 
	 * @param teacher
	 *            the teacher
	 * @param learner
	 *            the learner
	 * @param vertices
	 *            an automaton accepting the set of all vertices of the game
	 * @param statistics
	 *            Statistics produced during the learning
	 * 
	 * @return Returns the learned automaton.
	 * 
	 * @throws Exception
	 *             Throws an exception if the learning was unsuccessful.
	 */
	public static Automaton learn(ITeacher teacher, ILearner learner,
			Automaton vertices, HashMap<String, Object> statistics)
			throws Exception {

		/*
		 * Some statistics
		 */
		int numberOfPositiveCounterexamples = 0;
		int numberOfNegativeCounterexamples = 0;
		int numberOfExistentialImplicationCounterexamples = 0;
		int numberOfUniversalImplicationCounterexamples = 0;
		int numberOfIterations = 0;
		StopWatch learnerWatch = new StopWatch();
		StopWatch teacherWatch = new StopWatch();
		StopWatch totalWatch = new StopWatch();

		// Start time measurement
		totalWatch.start();

		/*
		 * Start learning
		 */
		Automaton conjecture = Automaton.makeEmpty();
		boolean learned = false;

		while (!learned) {

			numberOfIterations += 1;

			// System.out.print(numberOfIterations % 25 != 0 ? "." : "|");
			// if(numberOfIterations % 50 == 0) {
			// System.out.println();
			// }

			String counterexample = null;
			ImplicationCounterexample implCounterexample = null;

			// Check initial
			if (counterexample == null) {

				teacherWatch.start();
				counterexample = teacher.containsInitial(conjecture);
				teacherWatch.stop();

				if (counterexample != null) {

					learner.addPositiveCounterexample(counterexample);
					numberOfPositiveCounterexamples += 1;

				}

			}

			// Check safe
			if (counterexample == null) {

				teacherWatch.start();
				counterexample = teacher.isSafe(conjecture);
				teacherWatch.stop();

				if (counterexample != null) {

					learner.addNegativeCounterexample(counterexample);
					numberOfNegativeCounterexamples += 1;

				}

			}

			// Check existential
			if (counterexample == null && implCounterexample == null) {

				teacherWatch.start();
				implCounterexample = teacher.isExistentiallyClosed(conjecture);
				teacherWatch.stop();

				if (implCounterexample != null) {

					learner.addExistentialImplicationCounterexample(implCounterexample);
					numberOfExistentialImplicationCounterexamples += 1;

				}

			}

			// Check universal
			if (counterexample == null && implCounterexample == null) {

				teacherWatch.start();
				implCounterexample = teacher.isUniversallyClosed(conjecture);
				teacherWatch.stop();

				if (implCounterexample != null) {

					learner.addUniversalImplicationCounterexample(implCounterexample);
					numberOfUniversalImplicationCounterexamples += 1;

				}

			}

			// Learn new or return
			if (counterexample == null && implCounterexample == null) {
				learned = true;
			} else {

				learnerWatch.start();
				conjecture = learner.deriveConjecture();
				learnerWatch.stop();

				if (vertices != null) {

					conjecture = conjecture.intersection(vertices);
					conjecture.minimize();

				}

			}

		}

		// Stop total time measurement
		totalWatch.stop();

		// Output statistics
		statistics.put("total", new Double((double) totalWatch.getElapsedTime()
				/ (double) StopWatch.IN_SECONDS));
		statistics.put("teacher",
				new Double((double) teacherWatch.getElapsedTime()
						/ (double) StopWatch.IN_SECONDS));
		statistics.put("learner",
				new Double((double) learnerWatch.getElapsedTime()
						/ (double) StopWatch.IN_SECONDS));
		statistics.put("size of result", conjecture.getNumberOfStates());
		statistics.put("iterations", numberOfIterations);
		statistics.put("positive counterexamples",
				numberOfPositiveCounterexamples);
		statistics.put("negative counterexamples",
				numberOfNegativeCounterexamples);
		statistics.put("existential implication counterexamples",
				numberOfExistentialImplicationCounterexamples);
		statistics.put("universal implication counterexamples",
				numberOfUniversalImplicationCounterexamples);

		return conjecture;

	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            command line arguments
	 * 
	 * @throws Exception
	 *             Throws an exception if the learning was unsuccessful.
	 */

	public static void main(String[] args) throws Exception {

		// Create game
		StopWatch gameWatch = new StopWatch(true);
		boolean[][] world = {{false,true,true,true,false,true,true,true,false},
		 {false,false,false,false,false,false,true,false,false},
		 {false,false,true,true,false,false,true,false,true},
		 {true,true,true,false,false,false,false,false,false},
		 {false,false,false,false,false,false,false,true,true},
		 {true,true,false,true,true,false,false,false,false},
		 {true,false,false,true,false,false,false,true,false},
		 {false,false,false,true,false,true,false,true,false},
		 {false,true,false,true,false,false,false,false,false}};


		IGame game = new GridWorldSequence1D(50);
		if (!AutomatonTeacher.isSane(game)) {
			throw new Error("Game is not sane");
		}

		// Create teacher
		AutomatonTeacher teacher = new AutomatonTeacher(
				game.getPlayer0Vertices(), game.getPlayer1Vertices(),
				game.getInitialVertices(), game.getSafeVertices(),
				game.getTransitions(), game.getAlphabetSize());

		// Create Learner
ILearner learner = new RPNILearner(game.getAlphabetSize());
		 //ILearner learner = new Z3LearnerSAT(game.getAlphabetSize());

		// Learn
		HashMap<String, Object> statistics = new HashMap<>();
		Automaton result = learn(teacher, learner, null, statistics);
		result.minimize();

		System.out.println("---------- Statistics ----------");
		System.out
				.println(String
						.format("Total learning time: %,.3f s%nTime teacher: %,.3f s%nTime learner: %,.3f s%nSize of solution: %d%nIterations: %d%nPositive Counterexamples: %d%nNegative counterexamples: %d%nExistential implication counterexamples: %d%nUniversal implications counterexamples: %d",
								(Double) statistics.get("total"),
								(Double) statistics.get("teacher"),
								(Double) statistics.get("learner"),
								(Integer) statistics.get("size of result"),
								(Integer) statistics.get("iterations"),
								(Integer) statistics
										.get("positive counterexamples"),
								(Integer) statistics
										.get("negative counterexamples"),
								(Integer) statistics
										.get("existential implication counterexamples"),
								(Integer) statistics
										.get("universal implication counterexamples")));

		 System.out.println(String.format(
		 "%n%n========== RESULT ==========%n%n%1$s",
		 Tools.automatonToDot(result)));
		System.out.println("" + (Double) statistics.get("total") + ", " +(Integer) statistics.get("size of result") + ", " + (Integer) statistics.get("positive counterexamples") + ", " + (Integer) statistics.get("negative counterexamples") + ", " + (Integer) statistics.get("existential implication counterexamples") + ", " + (Integer) statistics.get("universal implication counterexamples"));

	}

}
