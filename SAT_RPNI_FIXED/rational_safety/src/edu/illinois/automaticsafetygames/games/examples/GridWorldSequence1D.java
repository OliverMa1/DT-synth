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
 */

public class GridWorldSequence1D implements IGame {

	private int n;

	private State[][][] states;

	private int initX = 0;

	private Automaton aut = null;

	public GridWorldSequence1D(int n) {

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

		aut = new Automaton();
		aut.setInitialState(states[0][initX][initX]);
		aut.restoreInvariant();

	}

	@Override
	public int getAlphabetSize() {
		return 2;
	}

	@Override
	public Automaton getPlayer0Vertices() {

		Automaton a = Automaton.makeCharRange('\u0000', '\u0001').concatenate(
				Automaton.makeCharRange('\u0000', '\u0001').repeat(2, 2)
						.repeat());

		a = a.intersection(aut);

		a.minimize();

		return a;

	}

	@Override
	public Automaton getPlayer1Vertices() {

		Automaton a = Automaton.makeCharRange('\u0000', '\u0001').repeat(2, 2)
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

		State s0 = new State();
		State s1 = new State();
		State s2 = new State();
		State s3 = new State();
		s0.setAccept(true);
		s1.setAccept(true);
		s2.setAccept(true);

		s0.addTransition(new Transition('\u0000', s1));
		s0.addTransition(new Transition('\u0001', s2));
		s1.addTransition(new Transition('\u0000', s0));
		s1.addTransition(new Transition('\u0001', s3));
		s2.addTransition(new Transition('\u0000', s3));
		s2.addTransition(new Transition('\u0001', s0));
		s3.addTransition(new Transition('\u0000', s3));
		s3.addTransition(new Transition('\u0001', s3));

		Automaton a = new Automaton();
		a.setInitialState(s0);
		a.restoreInvariant();

		Automaton b = Automaton.makeCharRange('\u0000', '\u0001').repeat()
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
		char[] from = { 0, 1 };
		char[] to = { Tools.convolution(0, 0, 2), Tools.convolution(1, 1, 2) };
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
					cur.addTransition(new Transition(Tools.convolution(2,
							c % 3, 2), sink));
				}



			}

		} while (!stack.isEmpty());

		trans.restoreInvariant();
		//trans.minimize();
		
		return trans;
	}

	public static void main(String[] args) {

		GridWorldSequence1D game = new GridWorldSequence1D(4);

		
		
		System.out.println(Tools.transducerToDot(game.getTransitions(),
				game.getAlphabetSize()));

	}

}
