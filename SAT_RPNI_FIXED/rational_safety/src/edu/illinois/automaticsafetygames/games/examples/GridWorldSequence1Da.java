package edu.illinois.automaticsafetygames.games.examples;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import edu.illinois.automaticsafetygames.games.IGame;
import edu.illinois.automaticsafetygames.tools.Tools;

/*
 * 0 = right
 * 1 = left
 * 2 = stay
 */

public class GridWorldSequence1Da implements IGame {

	private int n;

	private State[][][] states;

	private int initX = 1;
	private int initY = 0;

	private Automaton aut = null;

	public GridWorldSequence1Da(int n) {

		// Check parameters
		if (n <= 0) {
			throw new IllegalArgumentException("n has to be greater than 0");
		}

		// Store parameter
		this.n = n;

		// Create state array
		states = new State[2][n][n];

		// Populate array
		populateArray();

	}

	private void populateArray() {

		// Create states
		for (int p = 0; p < 2; ++p) {
			for (int i1 = 0; i1 < n; ++i1) {
				for (int i2 = 0; i2 < n; ++i2) {
					(states[p][i1][i2] = new State()).setAccept(true);
				}
			}
		}

		// Create up transitions

		// Right = 0
		for (int i1 = 0; i1 < n - 1; ++i1) {
			for (int i2 = 0; i2 < n; ++i2) {
				states[0][i1][i2].addTransition(new Transition('\u0000',
						states[1][i1 + 1][i2]));
			}
		}
		for (int i1 = 0; i1 < n; ++i1) {
			for (int i2 = 0; i2 < n - 1; ++i2) {
				states[1][i1][i2].addTransition(new Transition('\u0000',
						states[0][i1][i2 + 1]));
			}
		}

		// Left = 1
		for (int i1 = 1; i1 < n; ++i1) {
			for (int i2 = 0; i2 < n; ++i2) {
				states[0][i1][i2].addTransition(new Transition('\u0001',
						states[1][i1 - 1][i2]));
			}
		}

		for (int i1 = 0; i1 < n; ++i1) {
			for (int i2 = 1; i2 < n; ++i2) {
				states[1][i1][i2].addTransition(new Transition('\u0001',
						states[0][i1][i2 - 1]));
			}
		}

		// stay = 2
		for (int i1 = 0; i1 < n; ++i1) {
			for (int i2 = 0; i2 < n; ++i2) {
				states[0][i1][i2].addTransition(new Transition('\u0002',
						states[1][i1][i2]));
			}
		}

		for (int i1 = 0; i1 < n; ++i1) {
			for (int i2 = 0; i2 < n; ++i2) {
				states[1][i1][i2].addTransition(new Transition('\u0002',
						states[0][i1][i2]));
			}
		}

		aut = new Automaton();
		aut.setInitialState(states[0][initX][initY]);
		aut.restoreInvariant();

	}

	@Override
	public int getAlphabetSize() {
		return 3;
	}

	@Override
	public Automaton getPlayer0Vertices() {

		Automaton a = Automaton.makeCharRange('\u0000', '\u0002').concatenate(
				Automaton.makeCharRange('\u0000', '\u0002').repeat(2, 2)
						.repeat());

		a = a.intersection(aut);

		a.minimize();

		return a;

	}

	@Override
	public Automaton getPlayer1Vertices() {

		Automaton a = Automaton.makeCharRange('\u0000', '\u0002').repeat(2, 2)
				.repeat();

		a = a.intersection(aut);

		a.minimize();

		return a;

	}

	@Override
	public Automaton getInitialVertices() {
		return Automaton.makeEmptyString();
	}

	@Override
	public Automaton getSafeVertices() {

		State e1 = new State(); //the environment is on the right of the system (environment's turn)
		State awaye = new State(); //the environment is a block away, right of the system 
		State e2 = new State(); //the environment is on the right of the system (system's turn)
		State w1 = new State(); //the environment is on the left of the system (environment's turn)
		State w2 = new State(); //the environment is on the left of the system (system's turn)
		State awayw = new State(); //the environment is a block away, left of the system  (system's turn)
		State in = new State(); //the environment and the system are at the same position (system's turn)

		e1.setAccept(true);
		awaye.setAccept(true);
		e2.setAccept(true);
		w1.setAccept(true);
		w2.setAccept(true);
		awayw.setAccept(true);
		in.setAccept(true);

		e1.addTransition(new Transition('\u0000', awaye));
		e1.addTransition(new Transition('\u0001', in));
		e1.addTransition(new Transition('\u0002', e2));
		e2.addTransition(new Transition('\u0002', e1));
		w1.addTransition(new Transition('\u0001', awayw));
		w1.addTransition(new Transition('\u0002', w2));
		w1.addTransition(new Transition('\u0000', in));
		w2.addTransition(new Transition('\u0002', w1));
		awayw.addTransition(new Transition('\u0001', w1));
		awaye.addTransition(new Transition('\u0000', e1));
		in.addTransition(new Transition('\u0001', e1));
		in.addTransition(new Transition('\u0000', w1));

		Automaton a = new Automaton();
		a.setInitialState(e1);
		a.restoreInvariant();

		Automaton b = Automaton.makeCharRange('\u0000', '\u0002').repeat()
				.minus(aut);
		Automaton c = aut.intersection(a);

		Automaton d = b.union(c);
		d.minimize();

		return d;

	}

	@Override
	public Automaton getTransitions() {

		State sink = new State();
		sink.setAccept(true);

		// Clone
		Automaton trans = aut.clone();

		// Change transitions
		char[] from = { 0, 1, 2};
		char[] to = { Tools.convolution(0, 0, 3), Tools.convolution(1, 1, 3), Tools.convolution(2, 2, 3) };
		trans = trans.homomorph(from, to);

		LinkedList<State> stack = new LinkedList<>();
		HashSet<State> visited = new HashSet<>();
		stack.addFirst(trans.getInitialState());
		
		do {

			
			State cur = stack.pop();
			visited.add(cur);
			cur.setAccept(false);

			Set<Transition> transitions = new HashSet<Transition>(
					cur.getTransitions());

			for (Transition t : transitions) {

				if (!visited.contains(t.getDest()) && !stack.contains(t.getDest())) {
					stack.addFirst(t.getDest());
				}
				
				for (char c = t.getMin(); c <= t.getMax(); ++c) {
					cur.addTransition(new Transition(Tools.convolution(3,
							c % 4, 3), sink));
				}



			}

		} while (!stack.isEmpty());

		trans.restoreInvariant();
		//trans.minimize();
		
		return trans;
	}

	public static void main(String[] args) {

		GridWorldSequence1Da game = new GridWorldSequence1Da(4);

		
		
		System.out.println(Tools.transducerToDot(game.getTransitions(),
				game.getAlphabetSize()));

	}

}
