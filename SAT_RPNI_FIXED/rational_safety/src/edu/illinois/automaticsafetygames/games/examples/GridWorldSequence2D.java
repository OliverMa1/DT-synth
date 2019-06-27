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
 * 0 = up
 * 1 = right
 * 2 = down
 * 3 = left
 */

public class GridWorldSequence2D implements IGame {

	private int n;

	private int m;

	private State[][][][][] states;

	private int initX = 0;

	private int initY = 0;

	Automaton aut = null;

	public GridWorldSequence2D(int m, int n) {

		// Check parameters
		if (m <= 0) {
			throw new IllegalArgumentException("m has to be greater than 0");
		}
		if (n <= 0) {
			throw new IllegalArgumentException("n has to be greater than 0");
		}

		// Store parameter
		this.m = m;
		this.n = n;

		// Create state array
		states = new State[2][n][m][n][m];

		// Populate array
		populateArray();

	}

	private void populateArray() {

		// Create states
		for (int p = 0; p < 2; ++p) {
			for (int i1 = 0; i1 < n; ++i1) {
				for (int j1 = 0; j1 < m; ++j1) {
					for (int i2 = 0; i2 < n; ++i2) {
						for (int j2 = 0; j2 < m; ++j2) {
							(states[p][i1][j1][i2][j2] = new State())
									.setAccept(true);
						}
					}
				}
			}
		}

		// Create up transitions
		// Up = 0
		for (int i1 = 0; i1 < n - 1; ++i1) {
			for (int j1 = 0; j1 < m; ++j1) {
				for (int i2 = 0; i2 < n; ++i2) {
					for (int j2 = 0; j2 < m; ++j2) {
						states[0][i1][j1][i2][j2].addTransition(new Transition(
								'\u0000', states[1][i1 + 1][j1][i2][j2]));
					}
				}
			}
		}
		for (int i1 = 0; i1 < n; ++i1) {
			for (int j1 = 0; j1 < m; ++j1) {
				for (int i2 = 0; i2 < n - 1; ++i2) {
					for (int j2 = 0; j2 < m; ++j2) {
						states[1][i1][j1][i2][j2].addTransition(new Transition(
								'\u0000', states[0][i1][j1][i2 + 1][j2]));
					}
				}
			}
		}

		// Down = 2
		for (int i1 = 1; i1 < n; ++i1) {
			for (int j1 = 0; j1 < m; ++j1) {
				for (int i2 = 0; i2 < n; ++i2) {
					for (int j2 = 0; j2 < m; ++j2) {
						states[0][i1][j1][i2][j2].addTransition(new Transition(
								'\u0002', states[1][i1 - 1][j1][i2][j2]));
					}
				}
			}
		}
		for (int i1 = 0; i1 < n; ++i1) {
			for (int j1 = 0; j1 < m; ++j1) {
				for (int i2 = 1; i2 < n; ++i2) {
					for (int j2 = 0; j2 < m; ++j2) {
						states[1][i1][j1][i2][j2].addTransition(new Transition(
								'\u0002', states[0][i1][j1][i2 - 1][j2]));
					}
				}
			}
		}

		// Right = 1
		for (int i1 = 0; i1 < n; ++i1) {
			for (int j1 = 0; j1 < m - 1; ++j1) {
				for (int i2 = 0; i2 < n; ++i2) {
					for (int j2 = 0; j2 < m; ++j2) {
						states[0][i1][j1][i2][j2].addTransition(new Transition(
								'\u0001', states[1][i1][j1 + 1][i2][j2]));
					}
				}
			}
		}
		for (int i1 = 0; i1 < n; ++i1) {
			for (int j1 = 0; j1 < m; ++j1) {
				for (int i2 = 0; i2 < n; ++i2) {
					for (int j2 = 0; j2 < m - 1; ++j2) {
						states[1][i1][j1][i2][j2].addTransition(new Transition(
								'\u0001', states[0][i1][j1][i2][j2 + 1]));
					}
				}
			}
		}

		// Left = 3
		for (int i1 = 0; i1 < n; ++i1) {
			for (int j1 = 1; j1 < m; ++j1) {
				for (int i2 = 0; i2 < n; ++i2) {
					for (int j2 = 0; j2 < m; ++j2) {
						states[0][i1][j1][i2][j2].addTransition(new Transition(
								'\u0003', states[1][i1][j1 - 1][i2][j2]));
					}
				}
			}
		}

		for (int i1 = 0; i1 < n; ++i1) {
			for (int j1 = 0; j1 < m; ++j1) {
				for (int i2 = 0; i2 < n; ++i2) {
					for (int j2 = 1; j2 < m; ++j2) {
						states[1][i1][j1][i2][j2].addTransition(new Transition(
								'\u0003', states[0][i1][j1][i2][j2 - 1]));
					}
				}
			}
		}

		// Create automaton
		aut = new Automaton();
		aut.setInitialState(states[0][initX][initY][initX][initY]);
		aut.restoreInvariant();
		aut.minimize();

	}

	@Override
	public int getAlphabetSize() {
		return 4;
	}

	@Override
	public Automaton getPlayer0Vertices() {

		Automaton a = Automaton.makeCharRange('\u0000', '\u0003').concatenate(
				Automaton.makeCharRange('\u0000', '\u0003').repeat(2, 2)
						.repeat());

		a = a.intersection(aut);

		a.minimize();

		return a;

	}

	@Override
	public Automaton getPlayer1Vertices() {

		Automaton a = Automaton.makeCharRange('\u0000', '\u0003').repeat(2, 2)
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

		// Up-down automaton
		State p0 = new State();
		State p1 = new State();
		State p2 = new State();
		p0.setAccept(true);
		p1.setAccept(true);
		p2.setAccept(true);

		p0.addTransition(new Transition('\u0000', p1));
		p0.addTransition(new Transition('\u0002', p2));
		p0.addTransition(new Transition('\u0001', p0));
		p0.addTransition(new Transition('\u0003', p0));
		p1.addTransition(new Transition('\u0000', p0));
		p1.addTransition(new Transition('\u0001', p1));
		p1.addTransition(new Transition('\u0003', p1));
		p2.addTransition(new Transition('\u0002', p0));
		p2.addTransition(new Transition('\u0001', p2));
		p2.addTransition(new Transition('\u0003', p2));

		Automaton pAut = new Automaton();
		pAut.setInitialState(p0);
		pAut.restoreInvariant();
		pAut.minimize();

		// Left-right automaton
		State q0 = new State();
		State q1 = new State();
		State q2 = new State();
		q0.setAccept(true);
		q1.setAccept(true);
		q2.setAccept(true);

		q0.addTransition(new Transition('\u0001', q1));
		q0.addTransition(new Transition('\u0003', q2));
		q0.addTransition(new Transition('\u0000', q0));
		q0.addTransition(new Transition('\u0002', q0));
		q1.addTransition(new Transition('\u0001', q0));
		q1.addTransition(new Transition('\u0000', q1));
		q1.addTransition(new Transition('\u0002', q1));
		q2.addTransition(new Transition('\u0003', q0));
		q2.addTransition(new Transition('\u0000', q2));
		q2.addTransition(new Transition('\u0002', q2));

		Automaton qAut = new Automaton();
		qAut.setInitialState(q0);
		qAut.restoreInvariant();
		qAut.minimize();

		// Finish things
		Automaton a = pAut.intersection(qAut);

		Automaton b = Automaton.makeCharRange('\u0000', '\u0003').repeat()
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
		char[] from = { 0, 1, 2, 3 };
		char[] to = { Tools.convolution(0, 0, 4), Tools.convolution(1, 1, 4),
				Tools.convolution(2, 2, 4), Tools.convolution(3, 3, 4) };
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

				if (!visited.contains(t.getDest())
						&& !stack.contains(t.getDest())) {
					stack.addFirst(t.getDest());
				}

				for (char c = t.getMin(); c <= t.getMax(); ++c) {
					cur.addTransition(new Transition(Tools.convolution(4,
							c % 5, 4), sink));
				}

			}

		} while (!stack.isEmpty());

		trans.restoreInvariant();
		// trans.minimize();

		return trans;

	}

	public static void main(String[] args) {

		GridWorldSequence2D game = new GridWorldSequence2D(3, 3);

		System.out.println(Tools.automatonToDot(game.getSafeVertices()));

	}

}
