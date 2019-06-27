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
 * 4 = stay
 */

public class GridWorldSequence2Da implements IGame {

	private int n;

	private int m;

	private State[][][][][] states;

	private int initX = 0;

	private int initY = 1;

	private int initX2 = 0;

	private int initY2 = 0;

	Automaton aut = null;

	public GridWorldSequence2Da(int m, int n) {

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
		// Stay = 4
		for (int i1 = 0; i1 < n; ++i1) {
			for (int j1 = 0; j1 < m; ++j1) {
				for (int i2 = 0; i2 < n; ++i2) {
					for (int j2 = 0; j2 < m; ++j2) {
						states[0][i1][j1][i2][j2].addTransition(new Transition(
								'\u0004', states[1][i1][j1][i2][j2]));
					}
				}
			}
		}

		for (int i1 = 0; i1 < n; ++i1) {
			for (int j1 = 0; j1 < m; ++j1) {
				for (int i2 = 0; i2 < n; ++i2) {
					for (int j2 = 0; j2 < m; ++j2) {
						states[1][i1][j1][i2][j2].addTransition(new Transition(
								'\u0004', states[0][i1][j1][i2][j2]));
					}
				}
			}
		}

		// Create automaton
		aut = new Automaton();
		aut.setInitialState(states[0][initX][initY][initX2][initY2]);
		aut.restoreInvariant();
		aut.minimize();

	}

	@Override
	public int getAlphabetSize() {
		return 5;
	}

	@Override
	public Automaton getPlayer0Vertices() {

		Automaton a = Automaton.makeCharRange('\u0000', '\u0004').concatenate(
				Automaton.makeCharRange('\u0000', '\u0004').repeat(2, 2)
						.repeat());

		a = a.intersection(aut);

		a.minimize();

		return a;

	}

	@Override
	public Automaton getPlayer1Vertices() {

		Automaton a = Automaton.makeCharRange('\u0000', '\u0004').repeat(2, 2)
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

		State e1 = new State();
		State e2 = new State();
		State n1 = new State();
		State n2 = new State();
		State s1 = new State();
		State s2 = new State();
		State w1 = new State();
		State w2 = new State();

		State ne = new State();
		State nw = new State();

		State se = new State();
		State sw = new State();

		State farn = new State();
		State fare = new State();
		State farw = new State();
		State fars = new State();

		State in = new State();

		e1.setAccept(true);
		e2.setAccept(true);
		n1.setAccept(true);
		n2.setAccept(true);
		s1.setAccept(true);
		s2.setAccept(true);
		w1.setAccept(true);
		w2.setAccept(true);

		ne.setAccept(true);
		nw.setAccept(true);
		se.setAccept(true);
		sw.setAccept(true);

		farn.setAccept(true);
		fare.setAccept(true);
		farw.setAccept(true);
		fars.setAccept(true);

		in.setAccept(true);

		e1.addTransition(new Transition('\u0000', ne));
		e1.addTransition(new Transition('\u0001', fare));
		e1.addTransition(new Transition('\u0002', se));
		e1.addTransition(new Transition('\u0003', in));
		e1.addTransition(new Transition('\u0004', e2));

		w1.addTransition(new Transition('\u0000', nw));
		w1.addTransition(new Transition('\u0001', in));
		w1.addTransition(new Transition('\u0002', sw));
		w1.addTransition(new Transition('\u0003', farw));
		w1.addTransition(new Transition('\u0004', w2));

		s1.addTransition(new Transition('\u0000', in));
		s1.addTransition(new Transition('\u0001', se));
		s1.addTransition(new Transition('\u0002', fars));
		s1.addTransition(new Transition('\u0003', sw));
		s1.addTransition(new Transition('\u0004', s2));

		n1.addTransition(new Transition('\u0000', farn));
		n1.addTransition(new Transition('\u0001', ne));
		n1.addTransition(new Transition('\u0002', in));
		n1.addTransition(new Transition('\u0003', nw));
		n1.addTransition(new Transition('\u0004', n2));

		farn.addTransition(new Transition('\u0000', n1));
		fare.addTransition(new Transition('\u0001', e1));
		fars.addTransition(new Transition('\u0002', s1));
		farw.addTransition(new Transition('\u0003', w1));

		in.addTransition(new Transition('\u0000', s1));
		in.addTransition(new Transition('\u0001', w1));
		in.addTransition(new Transition('\u0002', n1));
		in.addTransition(new Transition('\u0003', e1));

		ne.addTransition(new Transition('\u0000', e1));
		ne.addTransition(new Transition('\u0001', n1));
		nw.addTransition(new Transition('\u0003', n1));
		nw.addTransition(new Transition('\u0000', w1));

		se.addTransition(new Transition('\u0002', e1));
		se.addTransition(new Transition('\u0001', s1));
		sw.addTransition(new Transition('\u0002', w1));
		sw.addTransition(new Transition('\u0003', s1));

		n2.addTransition(new Transition('\u0004', n1));
		e2.addTransition(new Transition('\u0004', e1));
		s2.addTransition(new Transition('\u0004', s1));
		w2.addTransition(new Transition('\u0004', w1));

		Automaton a = new Automaton();
		a.setInitialState(e1);
		a.restoreInvariant();
		a.minimize();

		// Finish things

		Automaton b = Automaton.makeCharRange('\u0000', '\u0004').repeat()
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
		char[] from = { 0, 1, 2, 3, 4 };
		char[] to = { Tools.convolution(0, 0, 5), Tools.convolution(1, 1, 5),
				Tools.convolution(2, 2, 5), Tools.convolution(3, 3, 5), Tools.convolution(4, 4, 5) };
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
					cur.addTransition(new Transition(Tools.convolution(5,
							c % 6, 5), sink));
				}

			}

		} while (!stack.isEmpty());

		trans.restoreInvariant();
		// trans.minimize();

		return trans;

	}

	public static void main(String[] args) {

		GridWorldSequence2Da game = new GridWorldSequence2Da(3, 3);

		System.out.println(Tools.automatonToDot(game.getSafeVertices()));

	}

}
