package edu.illinois.automaticsafetygames.finitelybranching.learner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;
import com.microsoft.z3.enumerations.Z3_lbool;

import edu.illinois.automaticsafetygames.finitelybranching.learner.rpni.Automaton;
import edu.illinois.automaticsafetygames.finitelybranching.learner.rpni.ICERPNI;
import edu.illinois.automaticsafetygames.finitelybranching.learner.rpni.Sample;
import edu.illinois.automaticsafetygames.finitelybranching.teacher.ImplicationCounterexample;
import edu.illinois.automaticsafetygames.tools.Tools;

/**
 * A RPNI-based learner for automatic safety games over finitely branching
 * arenas.
 * 
 * @author Daniel Neider
 *
 */
public class RPNILearner implements ILearner {

	/**
	 * Used as compile time switch to enable consistency check after an
	 * conjecture has been computed.
	 */
	private static final boolean doConsistencyChecks = false;

	/**
	 * The set of positive words.
	 */
	private Sample sPlus;

	/**
	 * The set of negative words.
	 */
	private Sample sMinus;

	/**
	 * The set of existential implications.
	 */
	private Map<String, Set<String>> existentialImplications;

	/**
	 * The set of universal implications.
	 */
	private Map<String, Set<String>> universalImplications;

	/**
	 * The alphabet used by RPNI. (Store for efficiency reasons.)
	 */
	private ArrayList<String> alphabet;

	/**
	 * Creates a new instance of the RPNI-based learner.
	 * 
	 * @param alphabetSize
	 *            The alphabet size defined by the game
	 */
	public RPNILearner(int alphabetSize) {

		// Check parameters
		if (alphabetSize < 1) {
			throw new IllegalArgumentException(
					"alphabetSize must be greater than 0");
		}

		// Prepare empty sample
		this.sPlus = new Sample();
		this.sMinus = new Sample();
		this.existentialImplications = new HashMap<>();
		this.universalImplications = new HashMap<>();

		// Prepare the alphabet used by RPNI for faster access.
		alphabet = new ArrayList<>();
		for (int i = 0; i < alphabetSize; i++) {
			alphabet.add("" + ((char) i));
		}

	}

	public RPNILearner(ArrayList<String> alphabet) {

		// Check parameters
		if (alphabet == null) {
			throw new IllegalArgumentException("alphabet must not be null");
		}

		// Prepare empty sample
		this.sPlus = new Sample();
		this.sMinus = new Sample();
		this.existentialImplications = new HashMap<>();
		this.universalImplications = new HashMap<>();
		this.alphabet = alphabet;

	}

	@Override
	public void addPositiveCounterexample(String example) {

		if (sPlus.content.contains(example)) {
			throw new InformationAlreadyExistsException(example
					+ " already exists as positive counterexample");
		}

		sPlus.content.add(example);

	}

	@Override
	public void addNegativeCounterexample(String counterexample) {

		if (sMinus.content.contains(counterexample)) {
			throw new InformationAlreadyExistsException(counterexample
					+ " already exists as negative counterexample");
		}

		sMinus.content.add(counterexample);

	}

	@Override
	public void addExistentialImplicationCounterexample(
			ImplicationCounterexample impl)
			throws InformationAlreadyExistsException {

		if (existentialImplications.containsKey(impl.antecedent)) {
			throw new InformationAlreadyExistsException(
					impl.antecedent
							+ " already exists as antecedent of existential implication");
		}

		existentialImplications.put(impl.antecedent, impl.consequent);

	}

	@Override
	public void addUniversalImplicationCounterexample(
			ImplicationCounterexample impl)
			throws InformationAlreadyExistsException {

		if (universalImplications.containsKey(impl.antecedent)) {
			throw new InformationAlreadyExistsException(impl.antecedent
					+ " already exists as antecedent of universal implication");
		}

		universalImplications.put(impl.antecedent, impl.consequent);

	}

	@Override
	public dk.brics.automaton.Automaton deriveConjecture() throws Exception {

		/*
		 * Propagate and remove satisfied implications
		 */
		propagateUniversalAndremoveSatisfiedImplications();

		/*
		 * Propagate temporary
		 */
		// Copy sPlus
		Sample tmpSPlus = new Sample();
		for (String s : sPlus.content) {
			tmpSPlus.content.add(s);
		}

		// Propagate
		Set<String> newEntries = propagateZ3();
		for (String s : newEntries) {
			tmpSPlus.content.add(s);
		}

		/*
		 * Run RPNI
		 */
		// Create new RPNI algorithm
		ICERPNI rpni = new ICERPNI(tmpSPlus, sMinus, existentialImplications,
				universalImplications, alphabet);
		Automaton rpniConjecture = rpni.run();
		dk.brics.automaton.Automaton bricsConjecture = rpniConjecture.toBrics();

		/*
		 * Consistency check
		 */
		if (doConsistencyChecks) {
			if (!conjectureIsConsistent(bricsConjecture)) {
				throw new Error("Conjecture is not consistent");
			}
		}

		// Return
		return bricsConjecture;

	}

	/*
	 * 1) Propagates universal implications 2) Removes satisfied implications
	 * (both types)
	 */
	private void propagateUniversalAndremoveSatisfiedImplications() {

		boolean finished;
		do {

			finished = true;

			/*
			 * Universal implications
			 */
			Iterator<Entry<String, Set<String>>> it = universalImplications
					.entrySet().iterator();
			while (it.hasNext()) {

				Entry<String, Set<String>> entry = it.next();

				if (sPlus.content.contains(entry.getKey())) {

					for (String consequent : entry.getValue()) {
						if (!sPlus.content.contains(consequent)) {

							sPlus.content.add(consequent);
							finished = false;

						}
					}

					it.remove();

				}

			}

			/*
			 * Existential implications
			 */
			it = existentialImplications.entrySet().iterator();
			while (it.hasNext()) {

				Entry<String, Set<String>> entry = it.next();

				if (sPlus.content.contains(entry.getKey())) {

					boolean satisfied = false;

					for (String consequent : entry.getValue()) {
						if (sPlus.content.contains(consequent)) {

							satisfied = true;
							break;

						}
					}

					if (satisfied) {

						it.remove();
						finished = false;

					}
				}

			}

		} while (!finished);

	}

	private Set<String> propagateZ3() throws Z3Exception {

		Context ctx = new Context();

		/*
		 * 1) Create variables for each word occurring in an implication
		 */
		HashMap<String, BoolExpr> variables = new HashMap<>();

		// Existential implications
		for (Entry<String, Set<String>> e : existentialImplications.entrySet()) {

			// Add antecedent
			if (!variables.containsKey(e.getKey())) {
				variables.put(e.getKey(),
						ctx.mkBoolConst(Tools.toReadableString(e.getKey())));
			}

			// Add consequents
			for (String c : e.getValue()) {
				if (!variables.containsKey(c)) {
					variables
							.put(c, ctx.mkBoolConst(Tools.toReadableString(c)));
				}
			}

		}
		// Universal implications
		for (Entry<String, Set<String>> e : universalImplications.entrySet()) {

			// Add antecedent
			if (!variables.containsKey(e.getKey())) {
				variables.put(e.getKey(),
						ctx.mkBoolConst(Tools.toReadableString(e.getKey())));
			}

			// Add consequents
			for (String c : e.getValue()) {
				if (!variables.containsKey(c)) {
					variables
							.put(c, ctx.mkBoolConst(Tools.toReadableString(c)));
				}
			}

		}

		/*
		 * 2) Create constraints
		 */
		Solver solver = ctx.mkSolver();

		// Classifications
		for (Entry<String, BoolExpr> e : variables.entrySet()) {

			if (sPlus.content.contains(e.getKey())) {
				solver.add(e.getValue());
			}

			if (sMinus.content.contains(e.getKey())) {
				solver.add(ctx.mkNot(e.getValue()));
			}

		}

		// Existential implications
		for (Entry<String, Set<String>> e : existentialImplications.entrySet()) {

			BoolExpr bigOr = ctx.mkFalse();
			for (String c : e.getValue()) {
				bigOr = ctx.mkOr(bigOr, variables.get(c));
			}

			solver.add(ctx.mkImplies(variables.get(e.getKey()), bigOr));

		}

		// Universal implications
		for (Entry<String, Set<String>> e : universalImplications.entrySet()) {

			BoolExpr bigAnd = ctx.mkTrue();
			for (String c : e.getValue()) {
				bigAnd = ctx.mkAnd(bigAnd, variables.get(c));
			}

			solver.add(ctx.mkImplies(variables.get(e.getKey()), bigAnd));

		}

		/*
		 * 3) Solve
		 */
		Status status = solver.check();
		if (status != Status.SATISFIABLE) {
			throw new Error(
					"Propagation not possible as sample is contradictory (status "
							+ status + ")");
		}

		/*
		 * 4) Collect
		 */
		Model model = solver.getModel();
		HashSet<String> result = new HashSet<>();

		// Existential implications
		for (Entry<String, Set<String>> entry : existentialImplications
				.entrySet()) {

			addIfTrueAndNotExisting(result, entry.getKey(), model, variables);

			for (String c : entry.getValue()) {
				addIfTrueAndNotExisting(result, c, model, variables);
			}

		}

		// Universal implications
		for (Entry<String, Set<String>> entry : universalImplications
				.entrySet()) {

			addIfTrueAndNotExisting(result, entry.getKey(), model, variables);

			for (String c : entry.getValue()) {
				addIfTrueAndNotExisting(result, c, model, variables);
			}

		}

		/*
		 * Clean up
		 */
		solver.dispose();
		ctx.dispose();

		return result;
	}

	private void addIfTrueAndNotExisting(Set<String> set, String s, Model m,
			Map<String, BoolExpr> variables) throws Z3Exception {

		if (!set.contains(s)) {
			if (m.eval(variables.get(s), true).getBoolValue() == Z3_lbool.Z3_L_TRUE) {
				set.add(s);
			}
		}

	}

	public boolean conjectureIsConsistent(dk.brics.automaton.Automaton automaton) {

		// Check positive words
		for (String word : sPlus.content) {
			if (!automaton.run(word)) {

				System.err.println("Automaton does not accept "
						+ Tools.toReadableString(word));
				return false;

			}
		}

		// Check negative words
		for (String word : sMinus.content) {
			if (automaton.run(word)) {

				System.err.println("Automaton does not reject "
						+ Tools.toReadableString(word));
				return false;

			}
		}

		// Check existential implication
		for (Map.Entry<String, Set<String>> entry : existentialImplications
				.entrySet()) {
			if (automaton.run(entry.getKey())) {

				// Check each word
				boolean ok = false;
				for (String consequent : entry.getValue()) {
					if (automaton.run(consequent)) {

						ok = true;
						break;

					}
				}

				if (!ok) {

					System.err
							.println("Automaton is not existentially closed for antecedent "
									+ Tools.toReadableString(entry.getKey()));
					return false;

				}

			}
		}

		// Check universal implication
		for (Map.Entry<String, Set<String>> entry : universalImplications
				.entrySet()) {
			if (automaton.run(entry.getKey())) {

				// Check each word in the consequent
				boolean ok = true;
				for (String consequent : entry.getValue()) {
					if (!automaton.run(consequent)) {

						ok = false;
						break;

					}
				}

				if (!ok) {

					System.err
							.println("Automaton is not universally closed for antecedent "
									+ Tools.toReadableString(entry.getKey()));
					return false;

				}

			}

		}

		return true;

	}

	@Override
	public String toString() {

		String end = System.getProperty("line.separator");
		StringBuilder builder = new StringBuilder();

		// Positive
		builder.append("S+").append(end);
		for (String s : sPlus.content) {
			builder.append(s).append(end);
		}

		// Negative
		builder.append("S-").append(end);
		for (String s : sMinus.content) {
			builder.append(s).append(end);
		}

		// Ex. implications
		builder.append("EXISTENTIAL").append(end);
		builder.append(printImplications(existentialImplications)).append(end);

		// Uni. implications
		builder.append("UNIVERSAL").append(end);
		builder.append(printImplications(universalImplications)).append(end);

		return builder.toString();

	}

	public static String printImplications(Map<String, Set<String>> implications) {

		if (implications == null) {
			return "null";
		}

		StringBuilder builder = new StringBuilder();

		for (Entry<String, Set<String>> implication : implications.entrySet()) {

			builder.append(implication.getKey()).append(" -> [");

			Iterator<String> it = implication.getValue().iterator();
			while (it.hasNext()) {

				builder.append(it.next());
				if (it.hasNext()) {
					builder.append(", ");
				}

			}

			builder.append("]");

		}

		return builder.toString();

	}

	public static void main(String[] args) throws Exception {

		ArrayList<String> alphabet = new ArrayList<String>();
		alphabet.add("0");
		alphabet.add("1");

		RPNILearner alg = new RPNILearner(alphabet);

		// Positive
		alg.addPositiveCounterexample("0101");
		alg.addPositiveCounterexample("11");

		// Negative
		alg.addNegativeCounterexample("");
		alg.addNegativeCounterexample("1");

		// Existential implications
		HashSet<String> consequent = new HashSet<>();
		consequent.add("110");
		consequent.add("111111");
		alg.addUniversalImplicationCounterexample(new ImplicationCounterexample(
				"11", consequent));

		dk.brics.automaton.Automaton result = alg.deriveConjecture();
		System.out.println(Tools.automatonToDot(result));
	}

}
