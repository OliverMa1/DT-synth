package edu.illinois.automaticsafetygames.finitelybranching.learner.rpni;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ICERPNI extends Rpni {

	protected Map<String, Set<String>> existentialImplications;

	protected Map<String, Set<String>> universalImplications;

	public ICERPNI(Sample sPlus, Sample sMinus,
			Map<String, Set<String>> existentialImplications,
			Map<String, Set<String>> universalImplications,
			ArrayList<String> alphabet) {

		super(sPlus, sMinus, alphabet);

		this.existentialImplications = existentialImplications;
		this.universalImplications = universalImplications;

	}

	@Override
	public Automaton run() {

		// The guy writing this code forgot some edge cases :(

		// If sPlus is empty, construct trivial one-state rejecting DFA
		if (sPlus.content.size() == 0) {

			HashMap<String, State> states = new HashMap<>();
			ArrayList<State> fA = new ArrayList<State>();
			ArrayList<State> fR = new ArrayList<State>();
			HashMap<State, HashMap<String, State>> HTrans = new HashMap<>();

			// Initial state
			State initialState = new State();
			initialState.label = "";
			states.put("", initialState);
			fR.add(initialState);

			// Transitions
			HashMap<String, State> entry = new HashMap<>(alphabet.size());
			for (String s : alphabet) {
				entry.put(s, initialState);
			}
			HTrans.put(initialState, entry);

			pta = new Automaton(alphabet, states, initialState, fA, fR, HTrans);

			return pta;

		}

		// If sPlus contains only the empty word, construct trivial one-state
		// DFA accepting the empty word
		else if (sPlus.content.size() == 1 && sPlus.content.contains("")) {

			HashMap<String, State> states = new HashMap<>();
			ArrayList<State> fA = new ArrayList<State>();
			ArrayList<State> fR = new ArrayList<State>();
			HashMap<State, HashMap<String, State>> HTrans = new HashMap<>();

			// Initial state
			State initialState = new State();
			initialState.label = "";
			states.put("", initialState);
			fA.add(initialState);

			pta = new Automaton(alphabet, states, initialState, fA, fR, HTrans);

			return pta;

		}

		// In all other (normal) cases, run RPNI
		else {

			// DEBUG
			/*
			 * Automaton dbg = buildPTA(alphabet); if (!isConsistent(dbg, sPlus,
			 * sMinus, existentialImplications, universalImplications, true)) {
			 * throw new Error(
			 * "Initial prefix tree acceptor is not consistent with sample!"); }
			 */

			Automaton result = super.run();

			/*
			 * // DEBUG if (!isConsistent(result, sPlus, sMinus,
			 * existentialImplications, universalImplications, true)) { throw
			 * new Error("Result of RPNI is not consistent with sample!"); }
			 */

			return result;

		}

	}

	@Override
	public boolean isRpniCompatible(Automaton a) {
		return isConsistent(a, sPlus, sMinus, this.existentialImplications,
				this.universalImplications, false);
	}

	public static boolean isConsistent(Automaton aut, Sample sPlus,
			Sample sMinus, Map<String, Set<String>> existentialImplications,
			Map<String, Set<String>> universalImplications, boolean checkSPlus) {

		/*
		 * Check sPlus if desired
		 */
		if (checkSPlus) {
			for (String w : sPlus.content) {
				if (!aut.fA.contains(aut.qPrime(aut.initialState, w))) {
					// System.err.println("Automaton does not accept "
					// + Tools.toReadableString(w));
					return false;
				}
			}
		}

		/*
		 * Check sMinus
		 */
		for (String w : sMinus.content) {
			if (aut.fA.contains(aut.qPrime(aut.initialState, w))) {
				// System.err.println("Automaton does not reject "
				// + Tools.toReadableString(w));
				return false;
			}
		}

		/*
		 * Check existential implications
		 */
		for (Entry<String, Set<String>> e : existentialImplications.entrySet()) {

			// Skip if antecedent is not accepted
			if (!aut.fA.contains(aut.qPrime(aut.initialState, e.getKey()))) {
				continue;
			}

			boolean satisfied = false;
			for (String w : e.getValue()) {
				if (aut.fA.contains(aut.qPrime(aut.initialState, w))) {

					satisfied = true;
					break;

				}
			}

			if (!satisfied) {
				// System.err
				// .println("Automaton does not respect existential implication with antecedent "
				// + Tools.toReadableString(e.getKey()));
				return false;
			}

		}

		/*
		 * Check universal implications
		 */
		for (Entry<String, Set<String>> e : universalImplications.entrySet()) {

			// Skip if antecedent is not accepted
			if (!aut.fA.contains(aut.qPrime(aut.initialState, e.getKey()))) {
				continue;
			}

			boolean satisfied = true;
			for (String w : e.getValue()) {
				if (!(aut.fA.contains(aut.qPrime(aut.initialState, w)))) {

					satisfied = false;
					break;

				}
			}

			if (!satisfied) {
				// System.err
				// .println("Automaton does not respect universal implication with antecedent "
				// + Tools.toReadableString(e.getKey()));
				return false;
			}

		}

		return true;

	}

	public static void main(String[] args) {

		// S+
		Sample sPlus = new Sample();
		sPlus.content.add("10");

		// S-
		Sample sMinus = new Sample();
		sMinus.content.add("0");
		sMinus.content.add("1");

		// Ex. implications
		Map<String, Set<String>> existentialImplications = new HashMap<>();
		Set<String> consequent = new HashSet<>();
		consequent.add("110");
		// existentialImplications.put("101", consequent);

		// Uni. implications
		Map<String, Set<String>> universalImplications = new HashMap<>();
		consequent = new HashSet<>();
		consequent.add("1010");
		consequent.add("1011");
		universalImplications.put("", consequent);

		// Alphabet
		ArrayList<String> alphabet = new ArrayList<String>();
		alphabet.add("0");
		alphabet.add("1");

		ICERPNI alg = new ICERPNI(sPlus, sMinus, existentialImplications,
				universalImplications, alphabet);

		alg.run();
		System.out.println(alg.pta.toDot());

	}

}
