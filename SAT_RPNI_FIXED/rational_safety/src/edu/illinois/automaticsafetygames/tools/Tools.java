package edu.illinois.automaticsafetygames.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import dk.brics.automaton.*;
import edu.illinois.automaticsafetygames.games.IGame;

public class Tools {

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
	public static char convolution(int first, int second, int alphabetSize) {
		return (char) ((alphabetSize + 1) * second + first);
	}
	
	public static String pairAlphabetDescription(int alphabetSize) {

		String out = "";

		for (int i = 0; i <= alphabetSize; i++) {
			for (int j = 0; j <= alphabetSize; j++) {
				out += String.format("%1$d: (%2$d, %3$d)%n", (j
						* (alphabetSize + 1) + i), i, j);
			}
		}

		return out;

	}

	public static String toReadableString(String str) {

		if (str == null) {
			return "null";
		}

		String out = "";
		char[] chars = str.toCharArray();

		for (int i = 0; i < chars.length; i++) {
			out += (int) chars[i] + (i < chars.length - 1 ? " " : "");
		}

		return out;
	}

	public static String automatonToString(Automaton automaton) {
		return transducerToString(automaton, 0);
	}

	public static String transducerToString(Automaton automaton,
			int alphabetSize) {

		if (alphabetSize < 0) {
			throw new IllegalArgumentException("alphabetSize must be positive");
		}

		if (automaton == null) {
			return "null";
		}

		String statesOut = "";
		String transitionsOut = "";

		//
		Set<State> states = automaton.getStates();

		HashMap<State, Integer> stateID = new HashMap<>(2 * states.size());

		for (State s : states) {

			int id = stateID.size();
			statesOut += String.format("State %1$d [%2$s%3$s]%n", id,
					s.isAccept() ? "accepting" : "rejecting",
					s.equals(automaton.getInitialState()) ? ", initial" : "");
			stateID.put(s, id);

		}

		// Output transitions as normal automaton
		if (alphabetSize <= 0) {

			for (State s : states) {
				for (Transition t : s.getTransitions()) {

					transitionsOut += String.format(
							"%1$d -[%2$d, %3$d]-> %4$d%n", stateID.get(s),
							(int) t.getMin(), (int) t.getMax(),
							stateID.get(t.getDest()));

				}
			}

		}

		// Output transitions as transducer
		else {

			for (State s : states) {
				for (Transition t : s.getTransitions()) {

					transitionsOut += String.format(
							"%1$d -[(%2$d, %3$d) : (%4$d, %5$d)]-> %6$d%n",
							stateID.get(s), (int) t.getMin()
									% (alphabetSize + 1), (int) t.getMin()
									/ (alphabetSize + 1), (int) t.getMax()
									% (alphabetSize + 1), (int) t.getMax()
									/ (alphabetSize + 1),
							stateID.get(t.getDest()));

				}
			}

		}

		return statesOut + transitionsOut;

	}

	public static String automatonToDot(Automaton automaton) {
		return transducerToDot(automaton, 0);
	}

	public static String transducerToDot(Automaton automaton, int alphabetSize) {

		if (alphabetSize < 0) {
			throw new IllegalArgumentException("alphabetSize must be positive");
		}

		if (automaton == null) {
			return "null";
		}

		String statesOut = "";
		String transitionsOut = "";

		//
		Set<State> states = automaton.getStates();

		HashMap<State, Integer> stateID = new HashMap<>(2 * states.size());

		for (State s : states) {

			int id = stateID.size();
			statesOut += String.format("%1$d [shape=\"%2$s\"%3$s];%n", id, s
					.isAccept() ? "doublecircle" : "circle", s.equals(automaton
					.getInitialState()) ? ", color=\"green\"" : "");
			stateID.put(s, id);

		}

		// Output transitions as normal automaton
		if (alphabetSize <= 0) {

			for (State s : states) {
				for (Transition t : s.getTransitions()) {

					transitionsOut += String.format(
							"%1$d -> %2$d [label=\"(%3$d - %4$d)\"];%n",
							stateID.get(s), stateID.get(t.getDest()),
							(int) t.getMin(), (int) t.getMax());

				}
			}

		}

		else {

			for (State s : states) {
				for (Transition t : s.getTransitions()) {

					transitionsOut += String
							.format("%1$d -> %2$d [label=\"(%3$d, %4$d) - (%5$d, %6$d)\"];%n",
									stateID.get(s), stateID.get(t.getDest()),
									(int) t.getMin() % (alphabetSize + 1),
									(int) t.getMin() / (alphabetSize + 1),
									(int) t.getMax() % (alphabetSize + 1),
									(int) t.getMax() / (alphabetSize + 1));

				}
			}

		}

		return String.format("digraph automaton {%n%1$s%2$s};", statesOut,
				transitionsOut);

	}

	public static String automatonToDot(Automaton automaton,
			Map<Character, String> encoding) {
		return transducerToDot(automaton, encoding, 0);
	}

	public static String transducerToDot(Automaton automaton,
			Map<Character, String> encoding, int alphabetSize) {

		if (encoding == null) {
			throw new IllegalArgumentException("encoding must not be null");
		}
		if (alphabetSize < 0) {
			throw new IllegalArgumentException("slphabetSize must be positive");
		}

		if (automaton == null) {
			return "null";
		}

		String statesOut = "";
		String transitionsOut = "";

		//
		Set<State> states = automaton.getStates();

		HashMap<State, Integer> stateID = new HashMap<>(2 * states.size());

		for (State s : states) {

			int id = stateID.size();
			statesOut += String.format("%1$d [shape=\"%2$s\"%3$s];%n", id, s
					.isAccept() ? "doublecircle" : "circle", s.equals(automaton
					.getInitialState()) ? ", color=\"green\"" : "");
			stateID.put(s, id);

		}

		// Output transitions as normal automaton
		if (alphabetSize <= 0) {

			for (State s : states) {
				for (Transition t : s.getTransitions()) {
					for (int a = t.getMin(); a <= t.getMax(); a++) {

						transitionsOut += String.format(
								"%1$d -> %2$d [label=\"%3$s\"];%n",
								stateID.get(s), stateID.get(t.getDest()),
								encoding.get((char) a));

					}
				}
			}

		}

		else {

			for (State s : states) {
				for (Transition t : s.getTransitions()) {
					for (int a = t.getMin(); a <= t.getMax(); a++) {

						transitionsOut += String.format(
								"%1$d -> %2$d [label=\"(%3$s, %4$s)\"];%n",
								stateID.get(s), stateID.get(t.getDest()),
								encoding.get((char) (a % (alphabetSize + 1))),
								encoding.get((char) (a / (alphabetSize + 1))));

					}
				}
			}

		}

		return String.format("digraph automaton {%n%1$s%2$s};", statesOut,
				transitionsOut);

	}

	/**
	 * Computes the sum of the number of states of all automata.
	 * 
	 * @param game
	 *            the input game
	 * 
	 * @return Returns the sum of the number of states of all automata.
	 */
	public static int sizeOfGame(IGame game) {

		int sum = 0;

		sum += game.getInitialVertices().getNumberOfStates();
		sum += game.getPlayer0Vertices().getNumberOfStates();
		sum += game.getPlayer1Vertices().getNumberOfStates();
		sum += game.getSafeVertices().getNumberOfStates();
		sum += game.getTransitions().getNumberOfStates();

		return sum;

	}
}
