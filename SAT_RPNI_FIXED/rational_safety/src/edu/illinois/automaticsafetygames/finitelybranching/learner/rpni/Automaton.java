package edu.illinois.automaticsafetygames.finitelybranching.learner.rpni;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import edu.illinois.automaticsafetygames.tools.Tools;
import dk.brics.automaton.Transition;

/*
 * Automaton is a class which represents a Deterministic Finite State Automaton.
 */
public class Automaton {

	ArrayList<String> alphabet;
	public ArrayList<State> fA, fR;
	ArrayList<StateTransition> gamma;
	HashMap<String, State> states;
	HashMap<State, HashMap<String, State>> HTrans;
	HashMap<State, StateTransition> HPrec;
	public State initialState;

	public Automaton(ArrayList<String> alphabet, HashMap<String, State> states,
			State initialState, ArrayList<State> fA, ArrayList<State> fR,
			HashMap<State, HashMap<String, State>> HTrans) {
		this.alphabet = alphabet;
		this.states = states;
		this.initialState = initialState;
		this.fA = fA;
		this.fR = fR;
		this.HTrans = HTrans;
		this.HPrec = new HashMap<State, StateTransition>();
		for (State s : HTrans.keySet()) {
			HashMap<String, State> tr = HTrans.get(s);
			for (String st : tr.keySet()) {
				StateTransition et = new StateTransition(s, st);
				HPrec.put(tr.get(st), et);
			}
		}
	}

	public Automaton(ArrayList<String> alphabet, HashMap<String, State> states,
			State initialState, ArrayList<State> fA, ArrayList<State> fR,
			HashMap<State, HashMap<String, State>> HTrans,
			HashMap<State, StateTransition> HPrec) {
		this.alphabet = alphabet;
		this.states = states;
		this.initialState = initialState;
		this.fA = fA;
		this.fR = fR;
		this.HTrans = HTrans;
		this.HPrec = HPrec;
	}

	@SuppressWarnings("unchecked")
	public Automaton copy() {
		ArrayList<String> alphabetC = (ArrayList<String>) this.alphabet.clone();

		HashMap<String, State> StatesC = (HashMap<String, State>) this.states
				.clone();

		ArrayList<State> fAC = (ArrayList<State>) this.fA.clone();

		ArrayList<State> fRC = (ArrayList<State>) this.fR.clone();

		HashMap<State, HashMap<String, State>> HTransC = new HashMap<State, HashMap<String, State>>();

		for (State s : this.states.values()) {
			HashMap<String, State> h = this.HTrans.get(s);
			if (h != null)
				HTransC.put(s, (HashMap<String, State>) h.clone());
		}

		HashMap<State, StateTransition> HPrecC = (HashMap<State, StateTransition>) this.HPrec
				.clone();
		return new Automaton(alphabetC, StatesC, initialState, fAC, fRC,
				HTransC, HPrecC);
	}

	/*
	 * Returns the State you reach when you read `symbol` from `state`. Returns
	 * an error State if the transition doesn't exist.
	 */
	public State transition(State state, String symbol) {
		HashMap<String, State> ht = HTrans.get(state);
		if (ht != null) {
			State st = ht.get(symbol);
			if (st != null)
				return st;
		}
		State error = new State();
		error.label = "error";
		return error;
	}

	/*
	 * Checks if a State exists.
	 */
	public boolean isDefined(State s) {
		return !s.label.equals("error");
	}

	/*
	 * Returns the State reached when you read `word` from `state`.
	 */
	public State qSec(State state, String word) {
		String symb;
		for (int i = 0; i < word.length(); i++) {
			symb = word.substring(i, i + 1);
			System.out.print(symb);
			state = transition(state, symb);
			if (state.label.equals("error"))
				return state;
		}
		System.out.println();
		return state;
	}

	public State qPrime(State state, String word) {
		State tmp = new State();
		tmp = state;
		String symb;
		for (int i = 0; i < word.length(); i++) {
			symb = word.substring(i, i + 1);
			tmp = this.transition(tmp, symb);
			if (tmp.label.equals("error"))
				return tmp;
		}
		return tmp;
	}

	/*
	 * Mark reachable States from `state`.
	 */
	public void markReachable(State state) {
		state.reachable = true;
		HashMap<String, State> hh = this.HTrans.get(state);
		if (hh != null)
			for (State st : hh.values())
				if (st.reachable == false)
					this.markReachable(st);
	}

	/*
	 * Delete useless transitions and States from the automaton.
	 */
	public void clean() {
		for (State s : states.values())
			s.reachable = false;
		markReachable(initialState);
		@SuppressWarnings("unchecked")
		HashMap<String, State> scpy = (HashMap<String, State>) states.clone();
		for (State s : states.values())
			if (!s.reachable) {
				HashMap<String, State> ht = HTrans.get(s);
				if (ht != null)
					for (State sta : ht.values())
						if (!sta.reachable)
							HPrec.remove(sta);
				HTrans.remove(s);
				scpy.remove(s.label);
			}
		@SuppressWarnings("unchecked")
		ArrayList<State> facpy = (ArrayList<State>) fA.clone();
		for (State s : fA)
			if (!s.reachable)
				facpy.remove(s);
		fA = facpy;
		states = scpy;
	}

	public void merge(State qR, State qB) {
		StateTransition et = HPrec.get(qB);
		if (et != null) {
			HPrec.remove(qB);
			HTrans.get(et.start).remove(et.symbol);
			addTransitionH(et.start, et.symbol, qR);
			fold(qR, qB);
		}

	}

	public void fold(State qR, State qB) {
		State b = new State();
		if (fA.contains(qB) && !fA.contains(qR))
			fA.add(qR);
		for (String s : alphabet) {
			b = transition(qB, s);
			if (isDefined(b)) {
				if (isDefined(transition(qR, s)))
					fold(transition(qR, s), b);
				else
					addTransitionH(qR, s, b);
			}
		}
	}

	public void addPrec(State start, String symb, State end) {
		HPrec.put(end, new StateTransition(start, symb));
	}

	/*
	 * Adds a transition to the automaton.
	 */
	public void addTransitionH(State start, String symb, State end) {
		if (HTrans.get(start) == null) {
			HashMap<String, State> tr = new HashMap<String, State>();
			tr.put(symb, end);
			HTrans.put(start, tr);
		} else
			HTrans.get(start).put(symb, end);
		addPrec(start, symb, end);
	}

	@Override
	public String toString() {
		String newLine = System.getProperty("line.separator");
		StringBuilder result = new StringBuilder();
		result.append("====================PTA=================");
		result.append(newLine);
		result.append("------------------States----------------");
		result.append(newLine);
		for (State s : this.states.values()) {
			result.append(s.label.isEmpty() ? "initialState" : s.label);
			result.append(newLine);
		}
		result.append(newLine);
		result.append("-----------Accepting States----------");
		result.append(newLine);
		for (State s1 : this.fA) {
			result.append(s1.label.isEmpty() ? "initialState" : s1.label);
			result.append(newLine);
		}
		result.append(newLine);
		result.append("--------------Rejecting States-----------");
		result.append(newLine);
		for (State s2 : this.fR) {
			result.append(s2.label.isEmpty() ? "initialState" : s2.label);
			result.append(newLine);
		}
		result.append(newLine);
		result.append("--------------Transitions-----------------");
		result.append(newLine);
		for (State s : this.HTrans.keySet()) {
			HashMap<String, State> hh = this.HTrans.get(s);
			for (String str : hh.keySet()) {
				State se = hh.get(str);
				result.append(s.label.isEmpty() ? "initialState" : s.label);
				result.append(" -> ");
				result.append(str);
				result.append(" -> ");
				result.append(se.label.isEmpty() ? "initialState" : se.label);
				result.append(newLine);
			}
		}
		result.append("===============/PTA=====================");
		return result.toString();
	}

	public String toDot() {

		StringBuilder out = new StringBuilder();
		String end = System.getProperty("line.separator");

		// Header
		out.append("digraph automaton {").append(end);

		// States
		for (State q : states.values()) {

			String label = q.label.isEmpty() ? "init" : "\""
					+ Tools.toReadableString(q.label) + "\"";

			out.append("\t").append(label).append(" [shape=\"");
			out.append(fA.contains(q) ? "doublecircle\"" : "circle\"");
			out.append(q.label.isEmpty() ? ", color=\"green\"" : "");
			out.append("];").append(end);

		}

		// Transitions
		for (Entry<State, HashMap<String, State>> e1 : HTrans.entrySet()) {

			String sourceLabel = e1.getKey().label.isEmpty() ? "init" : "\""
					+ Tools.toReadableString(e1.getKey().label) + "\"";

			for (Entry<String, State> e2 : e1.getValue().entrySet()) {

				String destLabel = e2.getValue().label.isEmpty() ? "init"
						: "\"" + Tools.toReadableString(e2.getValue().label)
								+ "\"";

				out.append("\t").append(sourceLabel).append(" -> ")
						.append(destLabel);
				out.append(" [label=\"")
						.append(Tools.toReadableString(e2.getKey()))
						.append("\"];").append(end);

			}

		}

		// Footer
		out.append("}");

		return out.toString();

	}

	public dk.brics.automaton.Automaton toBrics() {

		// System.out.println("RPNI automaton\n" + toDot());

		// Create brics states
		HashMap<State, dk.brics.automaton.State> bricsStates = new HashMap<>(
				2 * states.values().size());
		for (State q : states.values()) {
			bricsStates.put(q, new dk.brics.automaton.State());
		}

		// Transitions
		for (Entry<State, HashMap<String, State>> transition : HTrans
				.entrySet()) {

			dk.brics.automaton.State source = bricsStates.get(transition
					.getKey());

			for (Entry<String, State> e : transition.getValue().entrySet()) {

				if (e.getKey().length() != 1) {
					throw new Error(
							"Error converting to brics: transition label must be a character");
				}

				dk.brics.automaton.State dest = bricsStates.get(e.getValue());
				source.addTransition(new Transition(e.getKey().charAt(0), dest));

			}

		}

		// Final states
		for (State s : fA) {
			bricsStates.get(s).setAccept(true);
		}

		// Construct automaton
		dk.brics.automaton.Automaton ret = new dk.brics.automaton.Automaton();
		ret.setInitialState(bricsStates.get(initialState));
		ret.restoreInvariant();

		return ret;

	}

}
