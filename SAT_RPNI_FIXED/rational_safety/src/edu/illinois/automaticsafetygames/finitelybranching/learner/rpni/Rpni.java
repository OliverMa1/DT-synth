package edu.illinois.automaticsafetygames.finitelybranching.learner.rpni;

import java.util.ArrayList;
import java.util.HashMap;

public class Rpni {

	protected Automaton pta = null;

	protected ArrayList<State> redStates = new ArrayList<State>();

	protected ArrayList<State> blueStates = new ArrayList<State>();

	protected Sample sPlus;

	protected Sample sMinus;

	protected ArrayList<String> alphabet = null;

	public Rpni(ArrayList<String> alphabet) {
		this(new Sample(), new Sample(), alphabet);
	}

	public Rpni(Sample sP, Sample sM, ArrayList<String> alphabet) {

		sMinus = sM;
		sPlus = sP;
		this.alphabet = alphabet;

	}

	public Automaton run() {
		//int p = 0;
		pta = buildPTA(alphabet); // A <-- buildpta(S+,S-)
		redStates.add(pta.initialState); // red <-- initialState
		HashMap<String, State> hh = pta.HTrans.get(pta.initialState);
		for (State s : hh.values())
			// blue <-- {Qa | a in alphabet AND Pref(S+)}
			blueStates.add(s);
		reverseB();
		// System.out.print("NB States : ");
		// System.out.println(pta.states.size());
		boolean found;
		Automaton tmpA = pta.copy();
		while (!blueStates.isEmpty()) {
			found = false;
			State b = chooseBlue();
			blueStates.remove(b);
			for (int i = 0; i < redStates.size() && !found; i++) {
				State r = redStates.get(i);
				tmpA = pta.copy();
				tmpA.merge(r, b);
				// if (++p % 10 == 0)
				// System.out.print(".");
				if (isRpniCompatible(tmpA)) {
					tmpA.clean();
					found = true;
					pta = tmpA;
					cleanrpni();
					// System.out.println();
					// System.out.print("NB States : ");
					// System.out.println(pta.states.size());
					for (State s : redStates)
						for (String a : alphabet)
							if (!blueStates.contains(pta.transition(s, a))
									&& !redStates
											.contains(pta.transition(s, a))
									&& pta.isDefined(pta.transition(s, a))
									&& pta.states.containsValue(pta.transition(
											s, a)))
								blueStates.add(pta.transition(s, a));

				}
			}
			if (!found)
				promote(b);
		}
		for (State s : this.pta.states.values())
			for (String w : this.sMinus.content)
				if (s.label
						.equals(this.pta.qPrime(this.pta.initialState, w).label)
						&& !this.pta.fR.contains(s))
					this.pta.fR.add(s);

		// System.out.println(pta);
		return pta;

	}

	/*
	 * Generates a basic automaton from the positive sample and the alphabet.
	 */
	public Automaton buildPTA(ArrayList<String> alphabet) {

		ArrayList<State> fA = new ArrayList<State>(), fR = new ArrayList<State>();

		HashMap<String, State> States = new HashMap<String, State>();
		HashMap<State, HashMap<String, State>> HTrans = new HashMap<State, HashMap<String, State>>();

		State initialState = new State(), tmp = new State();
		initialState.label = "";
		tmp.label = "tmp";

		// System.out.println("BUILD PTA");
		// System.out.println("\n");
		States.put("", initialState);
		for (String sP : this.sPlus.content) {
			for (int i = 1; i <= sP.length(); i++) {
				tmp.label = sP.substring(0, i);

				if (States.get(tmp.label) == null) {
					State s = tmp.copy();
					States.put(tmp.label, s);
					if (!s.label.isEmpty()) {
						State st = (State) States.get(s.label.substring(0,
								s.label.length() - 1));
						if (HTrans.get(st) == null) {
							HashMap<String, State> h = new HashMap<String, State>();
							h.put(s.label.substring(s.label.length() - 1,
									s.label.length()), s);
							HTrans.put(st, h);
						} else
							HTrans.get(st).put(
									s.label.substring(s.label.length() - 1,
											s.label.length()), s);
					}
				}
			}
		}

		for (String str : sPlus.content)
			fA.add(States.get(str));

		return new Automaton(alphabet, States, initialState, fA, fR, HTrans);
	}

	/*
	 * Promotes a blue State to a red State.
	 */
	public void promote(State s) {
		this.redStates.add(s);
		this.blueStates.remove(s);
		ArrayList<State> tmp = new ArrayList<State>();
		HashMap<String, State> hh = this.pta.HTrans.get(s);
		if (hh != null) {
			for (State st : hh.values())
				tmp.add(st);
			for (int t = tmp.size() - 1; t >= 0; t--)
				this.blueStates.add(tmp.get(t));
		}
	}

	/*
	 * Picks a blue State in the content
	 */
	public State chooseBlue() {
		return this.blueStates.get(0);
	}

	/*
	 * Checks if an automaton is rpni-compatible.
	 */
	public boolean isRpniCompatible(Automaton a) {
		for (String w : sMinus.content)
			if (a.fA.contains(a.qPrime(a.initialState, w)))
				return false;
		return true;
	}

	public void cleanrpni() {
		for (State s : blueStates)
			if (!pta.states.containsValue(s))
				blueStates.remove(s);
		for (State s : redStates)
			if (!pta.states.containsValue(s))
				redStates.remove(s);
	}

	private void reverseB() {
		int l = blueStates.size();
		Object[] ar = (Object[]) blueStates.toArray();
		for (int i = 0; i < l; i++) {
			blueStates.add((State) ar[l - i - 1]);
			blueStates.remove(0);
		}
	}
}