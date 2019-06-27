package edu.illinois.automaticsafetygames.tools;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;


import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import edu.illinois.automaticsafetygames.games.IGame;
import edu.illinois.automaticsafetygames.games.examples.BoxGame;

public class AutomatonParser {

	public static Automaton parse(BufferedReader reader) throws IOException,
			AutomatonParserException {

		/*
		 * Prepare data structures
		 */
		int initialState = 0;
		boolean foundInitialState = false;
		HashMap<Integer, State> states = new HashMap<>();

		/*
		 * Parse
		 */
		String line;
		while ((line = reader.readLine()) != null) {

			// Trim line
			line = line.trim().toLowerCase();

			// Skip comments and empty lines
			if (line.isEmpty() || line.startsWith("#")) {
				continue;
			}

			// // Parse alphabet size
			// if (line.startsWith("alphabetsize:")) {
			//
			// if (alphabetSize > 0) {
			// throw new AutomatonParserException(
			// "Multiple declaration of alphabet size not allowed");
			// }
			//
			// // Remove prefix
			// line = line.replaceFirst("alphabetsize:", "").trim();
			//
			// // Awaiting a single number
			// try {
			// alphabetSize = Integer.parseInt(line);
			// } catch (NumberFormatException e) {
			// throw new AutomatonParserException("Invalid alphabet size");
			// }
			//
			// }

			// Parse initial state
			else if (line.startsWith("initial:")) {

				if (foundInitialState == true) {
					throw new AutomatonParserException(
							"Multiple declaration of initial state not allowed");
				}

				// Remove prefix
				line = line.replaceFirst("initial:", "").trim();

				// Awaiting a single number
				try {

					initialState = Integer.parseInt(line);

					if (initialState < 0) {
						throw new AutomatonParserException(
								"Invalid initial state");
					}

					if (states.containsKey(initialState)) {
						states.put(initialState, new State());
					}

					foundInitialState = true;

				} catch (NumberFormatException e) {
					throw new AutomatonParserException("Invalid initial state");
				}

			}

			// Parse initial state
			else if (line.startsWith("final:")) {

				// Remove prefix
				line = line.replaceFirst("final:", "").trim();

				String[] split = line.split(",");

				try {

					for (int i = 0; i < split.length; i++) {

						int q = Integer.parseInt(split[i].trim());

						if (!states.containsKey(q)) {
							states.put(q, new State());
						}

						states.get(q).setAccept(true);

					}

				} catch (NumberFormatException e) {
					throw new AutomatonParserException("Invalid final state");
				}

			}

			// Parse transition
			else {

				String[] split = line.split(",");

				// Number of entries in line
				if (split.length != 3) {
					throw new AutomatonParserException(
							"Invalid transition encountered");
				}

				try {

					boolean success = true;

					// Source state
					int source = Integer.parseInt(split[0].trim());
					if (source < 0) {
						success = false;
					}

					// Label state
					int label = Integer.parseInt(split[1].trim());
					if (label < 0) {
						success = false;
					}

					// Destination state
					int dest = Integer.parseInt(split[2].trim());
					if (dest < 0) {
						success = false;
					}

					// Check parsing result
					if (!success) {
						throw new AutomatonParserException(
								"Could not parse transition");
					}

					// Create states if necessary
					if (!states.containsKey(source)) {
						states.put(source, new State());
					}
					if (!states.containsKey(dest)) {
						states.put(dest, new State());
					}

					// Add transition
					states.get(source).addTransition(
							new Transition((char) label, states.get(dest)));

				} catch (NumberFormatException e) {
					throw new AutomatonParserException(
							"Could not parse transition");
				}

			}

		}

		// Check initial state
		if (!foundInitialState) {
			throw new AutomatonParserException("No initial state found");
		}

		/*
		 * Construct automaton
		 */
		Automaton a = new Automaton();
		a.setInitialState(states.get(initialState));
		a.restoreInvariant();

		return a;

	}

	/**
	 * Returns the largest symbol occurring in a transition or 0 if there is no
	 * transition.
	 * 
	 * @param a
	 *            the automaton to check
	 * 
	 * @return Returns the largest symbol occurring in a transition or 0 if
	 *         there is no transition.
	 */
	public static char findLargestSymbol(Automaton a) {

		char largest = (char) 0;

		HashSet<State> visited = new HashSet<>();
		LinkedList<State> worklist = new LinkedList<>();

		worklist.add(a.getInitialState());

		State q;
		while ((q = worklist.pollFirst()) != null) {

			visited.add(q);

			for (Transition t : q.getTransitions()) {

				if (t.getMax() > largest) {
					largest = t.getMax();
				}

				if (!visited.contains(t.getDest())) {
					worklist.addLast(t.getDest());
				}

			}

		}

		return largest;

	}

	public static String serialize(Automaton a) {

		if (a == null) {
			throw new IllegalArgumentException("Parameter 'a' must not be null");
		}

		String newLine = System.getProperty("line.separator");

		// Create IDs for states
		HashMap<State, Integer> IDs = new HashMap<>();
		LinkedList<State> worklist = new LinkedList<>();
		HashSet<State> finalStates = new HashSet<>();

		worklist.add(a.getInitialState());
		State q;

		while ((q = worklist.pollFirst()) != null) {

			IDs.put(q, IDs.size());

			if (q.isAccept()) {
				finalStates.add(q);
			}

			for (Transition t : q.getTransitions()) {
				if (!IDs.containsKey(t.getDest())) {
					worklist.addLast(t.getDest());
				}
			}

		}

		// Output states
		StringBuilder out = new StringBuilder();
		out.append("initial: ").append(IDs.get(a.getInitialState()))
				.append(newLine);
		out.append("final: ");
		int i = 0;
		for (State s : finalStates) {

			out.append(IDs.get(s));
			if (i < finalStates.size() - 1) {
				out.append(", ");
			}

			i++;

		}

		// Output transitions
		StringBuilder trans = new StringBuilder();
		for (Entry<State, Integer> entry : IDs.entrySet()) {

			State s = entry.getKey();

			for (Transition t : s.getTransitions()) {
				for (char c = t.getMin(); c <= t.getMax(); c++) {

					out.append(newLine);
					out.append(IDs.get(s) + ", " + (int) c + ", "
							+ IDs.get(t.getDest()));

				}
			}

		}

		return out.toString();

	}

	public static void main(String[] args) throws IOException {

		String prefix = "c:\\Users\\Daniel\\Desktop\\Moo\\";
		
		IGame game = new BoxGame();
		
		// Player 0
		FileWriter w = new FileWriter(prefix + "player0.aut");
		w.write(serialize(game.getPlayer0Vertices()));
		w.close();

		// Player 1
		w = new FileWriter(prefix + "player1.aut");
		w.write(serialize(game.getPlayer1Vertices()));
		w.close();

		// Initial
		w = new FileWriter(prefix + "initial.aut");
		w.write(serialize(game.getInitialVertices()));
		w.close();

		// Initial
		w = new FileWriter(prefix + "safe.aut");
		w.write(serialize(game.getSafeVertices()));
		w.close();

		// Initial
		w = new FileWriter(prefix + "transitions.aut");
		w.write(serialize(game.getTransitions()));
		w.close();

	}
}
