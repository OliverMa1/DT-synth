package edu.illinois.automaticsafetygames.finitelybranching.learner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import com.microsoft.z3.enumerations.Z3_lbool;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import edu.illinois.automaticsafetygames.finitelybranching.learner.PrefixTree.Classification;
import edu.illinois.automaticsafetygames.finitelybranching.teacher.ImplicationCounterexample;

/**
 * A SAT-based learner for automatic safety games over finitely branching
 * arenas.
 * 
 * @author Daniel Neider
 *
 */
public class Z3LearnerSAT implements ILearner {

	/**
	 * Used as compile time switch to enable consistency check after an
	 * conjecture has been computed.
	 */
	private static final boolean doConsistencyChecks = false;

	/**
	 * Stores the alphabet size to work with.
	 */
	private int alphabetSize;

	/**
	 * Stores the data to learn from.
	 */
	private PrefixTree prefixTree;

	/**
	 * The size of the previous conjecture; used as initial value of n for next
	 * conjecture computation.
	 */
	private int sizeOfPreviousConjecture = 1;

	/**
	 * Creates a new instance of the SAT-based learner.
	 * 
	 * @param alphabetSize
	 *            The alphabet size defined by the game
	 */
	public Z3LearnerSAT(int alphabetSize) {

		// Check arguments
		if (alphabetSize <= 0) {
			throw new IllegalArgumentException(
					"alphabetSize must be greater than 0");
		}

		// Store arguments
		this.alphabetSize = alphabetSize;

		// Initialize variables
		prefixTree = new PrefixTree(alphabetSize);

	}

	@Override
	public void addPositiveCounterexample(String positive) {

		prefixTree.add(positive, Classification.ACCEPT, null, null);

	}

	@Override
	public void addNegativeCounterexample(String negative) {

		prefixTree.add(negative, Classification.REJECT, null, null);

	}

	@Override
	public void addExistentialImplicationCounterexample(
			ImplicationCounterexample impl) {

		prefixTree.add(impl.antecedent, Classification.UNCLASSIFIED,
				impl.consequent, null);

	}

	@Override
	public void addUniversalImplicationCounterexample(
			ImplicationCounterexample impl) {

		prefixTree.add(impl.antecedent, Classification.UNCLASSIFIED, null,
				impl.consequent);

	}

	@Override
	public Automaton deriveConjecture() throws Exception {

		/*
		 * Setup solver
		 */
		Context ctx = new Context();
		Solver solver = ctx.mkSolver();

		/*
		 * Compute conjecture
		 */
		Automaton conjecture = null;
		// int n = 0;
		int n = sizeOfPreviousConjecture - 1;

		do {

			n += 1;
			// System.out.println("n=" + n);
			conjecture = construct(n, ctx, solver);

		} while (conjecture == null);

		// Update size of conjecture
		sizeOfPreviousConjecture = n;

		/*
		 * Consistency check
		 */
		if (doConsistencyChecks) {
			if (!prefixTree.automatonIsConsistent(conjecture)) {
				throw new Error("Conjecture is not consistent");
			}
		}

		/*
		 * Cleanup
		 */
		solver.dispose();
		ctx.dispose();

		return conjecture;

	}

	private Automaton construct(int n, Context ctx, Solver solver) {

		// Push solver (as it seems to be better in Java rather than to create a
		// new)
		solver.push();

		/*
		 * Initialize variables
		 */
		int numVars = 0;

		// d variables
		BoolExpr[][][] d = new BoolExpr[n][alphabetSize][n];
		for (int p = 0; p < n; p++) {
			for (int a = 0; a < alphabetSize; a++) {
				for (int q = 0; q < n; q++) {
					d[p][a][q] = ctx.mkBoolConst(Integer.toString(numVars++));
					// d[p][a][q] = ctx.mkBoolConst("d_" + p + "-" + a + "-" +
					// q);
				}
			}
		}

		// f variables
		BoolExpr[] f = new BoolExpr[n];
		for (int q = 0; q < n; q++) {
			f[q] = ctx.mkBoolConst(Integer.toString(numVars++));
			// f[q] = ctx.mkBoolConst("f_" + q);
		}

		// x variables
		BoolExpr[][] x = new BoolExpr[prefixTree.numberOfNodes][n];
		for (int u = 0; u < prefixTree.numberOfNodes; u++) {
			for (int q = 0; q < n; q++) {
				x[u][q] = ctx.mkBoolConst(Integer.toString(numVars++));
				// x[u][q] = ctx.mkBoolConst("x_" + u + "-" + q);
			}
		}

		/*
		 * Construct constraints
		 */
		addConstraintsToSolver(n, ctx, solver, d, f, x);

		/*
		 * Solve
		 */
		// System.out.println(solver);
		Status result = solver.check();

		Automaton conjecture = null;

		if (result == Status.SATISFIABLE) {

			// System.out.println("\n" + solver);
			// System.out.println("\n" + solver.getModel());

			conjecture = deriveAutomatonFromModel(ctx, solver.getModel(), n, d,
					f);

		} else {
			// System.out.println("Fail!");
		}

		// Remove current constraints from solver
		solver.pop();

		return conjecture;

	}

	/**
	 * @param n
	 * @param ctx
	 * @param solver
	 * @param d
	 * @param f
	 * @param x
	 */
	private void addConstraintsToSolver(int n, Context ctx, Solver solver,
			BoolExpr[][][] d, BoolExpr[] f, BoolExpr[][] x) {

		/*
		 * Constraints on d
		 */
		for (int p = 0; p < n; p++) {
			for (int a = 0; a < alphabetSize; a++) {

				// Complete function
				BoolExpr bigOr = ctx.mkFalse();
				for (int q = 0; q < n; q++) {
					bigOr = ctx.mkOr(bigOr, d[p][a][q]);
				}
				solver.add(bigOr);

				// Deterministic function
				for (int q1 = 0; q1 < n; q1++) {
					for (int q2 = q1 + 1; q2 < n; q2++) {
						solver.add(ctx.mkOr(ctx.mkNot(d[p][a][q1]),
								ctx.mkNot(d[p][a][q2])));
					}
				}

			}
		}

		/*
		 * Constraints on x
		 */
		for (int u = 1; u < prefixTree.numberOfNodes; u++) {

			// At most one state is reached
			for (int q1 = 0; q1 < n; q1++) {
				for (int q2 = q1 + 1; q2 < n; q2++) {
					solver.add(ctx.mkOr(ctx.mkNot(x[u][q1]),
							ctx.mkNot(x[u][q2])));
				}
			}

		}

		/*
		 * Create constraints on empty word (implying the initial state of the
		 * conjecture)
		 */
		solver.add(x[prefixTree.root.id][0]);
		for (int q = 1; q < n; q++) {
			solver.add(ctx.mkNot(x[prefixTree.root.id][q]));
		}

		/*
		 * Create constraints for prefixes. While doing so, also create
		 * acceptance constraints.
		 */

		// Create initial work list (initial capacity is guessed)
		ArrayList<Node> worklist = new ArrayList<>(prefixTree.numberOfNodes / 3);
		worklist.add(prefixTree.root);

		// Process work list
		while (!worklist.isEmpty()) {

			// Get current node
			Node cur = worklist.remove(worklist.size() - 1);

			// Add acceptance constraints
			switch (cur.classification) {

			case ACCEPT:

				for (int q = 0; q < n; q++) {
					solver.add(ctx.mkImplies(x[cur.id][q], f[q]));
				}
				break;

			case REJECT:

				for (int q = 0; q < n; q++) {
					solver.add(ctx.mkImplies(x[cur.id][q], ctx.mkNot(f[q])));
				}
				break;

			case UNCLASSIFIED:
				break;

			}

			// Current node is the antecedent of a existential implication
			if (cur.existentialConsequent != null) {

				BoolExpr bigOr = ctx.mkFalse();

				for (Node exNode : cur.existentialConsequent) {
					for (int q = 0; q < n; q++) {
						bigOr = ctx.mkOr(bigOr,
								ctx.mkAnd(x[exNode.id][q], f[q]));
					}
				}

				for (int p = 0; p < n; p++) {
					solver.add(ctx.mkImplies(ctx.mkAnd(x[cur.id][p], f[p]),
							bigOr));
				}

			}

			// Current node is the antecedent of an universal implication
			if (cur.universalConsequent != null) {

				BoolExpr bigAnd = ctx.mkTrue();

				for (Node unNode : cur.universalConsequent) {

					BoolExpr bigOr = ctx.mkFalse();

					for (int q = 0; q < n; q++) {
						bigOr = ctx.mkOr(bigOr,
								ctx.mkAnd(x[unNode.id][q], f[q]));
					}

					bigAnd = ctx.mkAnd(bigAnd, bigOr);

				}

				for (int p = 0; p < n; p++) {
					solver.add(ctx.mkImplies(ctx.mkAnd(x[cur.id][p], f[p]),
							bigAnd));
				}

			}

			// Transitions constraints
			for (int a = 0; a < cur.children.length; a++) {

				Node next = cur.children[a];

				// Create constraint for transition and add child to work list
				if (next != null) {

					// Create transition constraint
					for (int p = 0; p < n; p++) {
						for (int q = 0; q < n; q++) {
							solver.add(ctx.mkImplies(
									ctx.mkAnd(x[cur.id][p], d[p][a][q]),
									x[next.id][q]));
						}
					}

					// Add child to work list
					worklist.add(next);

				}

			}

		}

	}

	private Automaton deriveAutomatonFromModel(Context ctx, Model model, int n,
			BoolExpr[][][] d, BoolExpr[] f) {

		// Buffer for transitions
		int[][] transitions = new int[n][alphabetSize];

		/*
		 * Create states
		 */
		HashMap<Integer, State> stateID = new HashMap<>();

		// Traverse the automaton from the initial state onwards ;)
		ArrayList<Integer> worklist = new ArrayList<>();
		worklist.add(0);
		while (!worklist.isEmpty()) {

			// Get next state
			int p = worklist.remove(worklist.size() - 1);

			// Check if already existing
			if (stateID.containsKey(p)) {
				continue;
			}

			// Create new state
			State s = new State();
			stateID.put(p, s);

			// Set acceptance behavior
			if (model.eval(f[p], true).getBoolValue()
					.equals(Z3_lbool.Z3_L_TRUE)) {
				s.setAccept(true);
			}

			// Process successor states (via transitions)
			for (int a = 0; a < alphabetSize; a++) {

				// Identify next state
				int q = -1;

				for (int i = 0; i < n; i++) {
					if (model.eval(d[p][a][i], true).getBoolValue()
							.equals(Z3_lbool.Z3_L_TRUE)) {

						q = i;
						break;

					}
				}

				// Debug
				if (q == -1) {
					System.out.println("p=" + p + "; a=" + a);
				}

				// Store transition
				transitions[p][a] = q;

				// Add successor to worklist
				worklist.add(q);

			}

		}

		/*
		 * Create transitions
		 */
		for (Entry<Integer, State> e : stateID.entrySet()) {

			int p = e.getKey();

			for (int a = 0; a < alphabetSize; a++) {
				e.getValue()
						.addTransition(
								new Transition((char) a, stateID
										.get(transitions[p][a])));
			}

		}

		/*
		 * Create automaton
		 */
		Automaton result = new Automaton();
		result.setInitialState(stateID.get(0));
		result.restoreInvariant();

		return result;

	}

	public static void main(String[] args) {

		try {
			new Z3LearnerSAT(2).deriveConjecture();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
