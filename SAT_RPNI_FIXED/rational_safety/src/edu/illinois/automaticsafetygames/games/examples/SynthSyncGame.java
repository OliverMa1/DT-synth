package edu.illinois.automaticsafetygames.games.examples;
import edu.illinois.automaticsafetygames.tools.Tools;
import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import edu.illinois.automaticsafetygames.games.IGame;

/**
 * <p>
 * A finitely branching version of the <em> repair critical game</em> described by
 * Beyene et al. [1].
 * </p>
 * 
 * <p>
 * Configuration of the program are 8-tupel <code>(pc1,pc2,f1a,f1b,f2a,f2b,t2b,t1b)</code>
 * consisting of a program counters <code>pc1,pc</code> (integer value between 0 and
 * 8),  boolean variables <code>f1a,f1b,f2a,f2b,t2b</code>, and a
 * non-negative integer variable <code>t1b</code>.
 * </p>
 * 
 * <p>
 * A configurations is encoded as word in the following way:
 * <ol>
 * <li>the program counter is encoded by a symbol in the set
 * <em>{0, ..., 8}</em>;</li>
 * <li>the is encoded in binary by either the symbol 0 or 1; and</li>
 * <li>the value of the variable <code>t1b</code> is encoded in unary as a
 * finite (potentially empty) sequence of 1s.</li>
 * </ol>
 * </p>
 * 
 * <p>
 * In the game as formulated by Beyene et al., the variable
 * <code>t1b</code> can be nondeterministically be set to an arbitrary
 * value, which results in an infinitely-branching. We turn the game into a
 * finitely-branching version by only allowing to change this value by at most
 * <code>max</code>, which can be specified when instantiating the game.
 * </p>
 * 
 * <ul>
 * <li>[1] Tewodros A. Beyene, Swarat Chaudhuri, Corneliu Popeea, and Andrey
 * Rybalchenko. A Constraint-Based Approach to Solving Games on Infinite Graphs.</li>
 * </ul>
 * 
 * @author Oliver Markgraf
 *
 */
public class SynthSyncGame implements IGame {

	/**
	 * Default value for parameter <code>max</code>.
	 */
	public static final int MAX_DEFAULT = 10;

	/**
	 * Maximal number that the program variable <em>got_lock</em> can be
	 * increase or decreased.
	 */
	private int max;

	/**
	 * Creates a new instance of the repair game.
	 */
	public SynthSyncGame() {
		max = MAX_DEFAULT;
	}

	/**
	 * Creates a new instance of the repair game.
	 * 
	 * @param max
	 *            maximal number that the program variable <em>got_lock</em> can
	 *            be increase or decreased
	 */
	public SynthSyncGame(int max) {
		this.max = max;
	}

	@Override
	public int getAlphabetSize() {
		return 9;
	}

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
	private char conv(int first, int second) {
		int a = ((getAlphabetSize() + 1) * second + first);
		return (char) ((getAlphabetSize() + 1) * second + first);
	}

	@Override
	public Automaton getPlayer0Vertices() {
		return Automaton.minimize(Automaton.makeCharRange('\u0000', '\u0004')
				.concatenate(Automaton.makeCharRange('\u0000', '\u0006'))
				.concatenate(Automaton.makeCharRange('\u0000', '\u0004'))
				.concatenate(Automaton.makeCharRange('\u0000', '\u0002'))
				.concatenate(Automaton.makeCharRange('\u0000', '\u0002'))
				.concatenate(Automaton.makeCharRange('\u0000', '\u0002'))
				.concatenate(Automaton.makeCharRange('\u0000', '\u0003')));
	}

	@Override
	public Automaton getPlayer1Vertices() {
		return Automaton.makeEmpty();
	}

	@Override
	public Automaton getInitialVertices() {
		//System.out.println(Automaton.makeChar('\u0000').repeat(2, 2));
		return (Automaton.makeChar('\u0000')).repeat(7,7);
	}

	@Override
	public Automaton getSafeVertices() {

		Automaton t1 = Automaton.minimize(Automaton.makeCharRange('\u0000', '\u0004')
				.concatenate(Automaton.makeChar('\u0000'))
				.concatenate(Automaton.makeChar('\u0000'))
				.concatenate(Automaton.makeCharRange('\u0000', '\u0002'))
				.concatenate(Automaton.makeCharRange('\u0000', '\u0002'))
				.concatenate(Automaton.makeCharRange('\u0000', '\u0002'))
				.concatenate(Automaton.makeChar('\u0002')));
		Automaton t2 = Automaton.minimize(Automaton.makeCharRange('\u0000', '\u0004')
				.concatenate(Automaton.makeChar('\u0001'))
				.concatenate(Automaton.makeChar('\u0001'))
				.concatenate(Automaton.makeCharRange('\u0000', '\u0002'))
				.concatenate(Automaton.makeCharRange('\u0000', '\u0002'))
				.concatenate(Automaton.makeCharRange('\u0000', '\u0002'))
				.concatenate(Automaton.makeChar('\u0002')));
		Automaton t3 = Automaton.minimize(Automaton.makeCharRange('\u0000', '\u0004')
				.concatenate(Automaton.makeChar('\u0002'))
				.concatenate(Automaton.makeChar('\u0002'))
				.concatenate(Automaton.makeCharRange('\u0000', '\u0002'))
				.concatenate(Automaton.makeCharRange('\u0000', '\u0002'))
				.concatenate(Automaton.makeCharRange('\u0000', '\u0002'))
				.concatenate(Automaton.makeChar('\u0002')));
		Automaton t4 = Automaton.minimize(Automaton.makeCharRange('\u0000', '\u0004')
				.concatenate(Automaton.makeChar('\u0003'))
				.concatenate(Automaton.makeChar('\u0003'))
				.concatenate(Automaton.makeCharRange('\u0000', '\u0002'))
				.concatenate(Automaton.makeCharRange('\u0000', '\u0002'))
				.concatenate(Automaton.makeCharRange('\u0000', '\u0002'))
				.concatenate(Automaton.makeChar('\u0002')));
				
		Automaton t5 = Automaton.minimize(Automaton.makeCharRange('\u0000', '\u0004')
				.concatenate(Automaton.makeChar('\u0004'))
				.concatenate(Automaton.makeChar('\u0004'))
				.concatenate(Automaton.makeCharRange('\u0000', '\u0002'))
				.concatenate(Automaton.makeCharRange('\u0000', '\u0002'))
				.concatenate(Automaton.makeCharRange('\u0000', '\u0002'))
				.concatenate(Automaton.makeChar('\u0002')));
		

		return getPlayer0Vertices().minus(t1.union(t2).union(t3).union(t4).union(t5));
	}

	@Override
	public Automaton getTransitions() {

		Automaton[] r = new Automaton[24];
		Automaton[] l = new Automaton[6];
		String pc2 = "(\\" + conv(0,0) + "|\\" + conv(1,1)+ "|\\" + conv(2,2)+ "|\\" + conv(3,3)+ "|\\" + conv(4,4)+ "|\\" + conv(5,5)+ ")";
		// Rho 0
		r[0] = new RegExp(
				conv(0, 0) 
				+ pc2
				+ pc2 
				+ conv(0, 0)
				+ conv(0, 1)
				+ pc2
				+ pc2).toAutomaton();
		r[1] = new RegExp(
				conv(0, 1) 
				+ pc2
				+ pc2 
				+ conv(1, 1)
				+ conv(0, 1)
				+ pc2
				+ pc2).toAutomaton();
		r[2] = new RegExp(
				conv(0, 2) 
				+ pc2
				+ pc2 
				+ conv(2, 2)
				+ conv(0, 1)
				+ pc2
				+ pc2).toAutomaton();
		// Rho 1
		r[3] = new RegExp(
				conv(0, 0) 
				+ pc2
				+ pc2 
				+ conv(0, 0)
				+ conv(1, 2)
				+ pc2
				+ pc2).toAutomaton();
		
		r[4] = new RegExp(
				conv(0, 1) 
				+ pc2
				+ pc2 
				+ conv(1, 1)
				+ conv(1, 2)
				+ pc2
				+ pc2).toAutomaton();
		r[5] = new RegExp(
				conv(0, 2) 
				+ pc2
				+ pc2 
				+ conv(2, 2)
				+ conv(1, 2)
				+ pc2
				+ pc2).toAutomaton();
		r[6] = new RegExp(
				conv(1, 1) 
				+ pc2
				+ pc2 
				+ conv(0, 0)
				+ conv(1, 2)
				+ pc2
				+ pc2).toAutomaton();
		
		r[7] = new RegExp(
				conv(1, 2) 
				+ pc2
				+ pc2 
				+ conv(1, 1)
				+ conv(1, 2)
				+ pc2
				+ pc2).toAutomaton();
		r[8] = new RegExp(
				conv(1, 3) 
				+ pc2
				+ pc2 
				+ conv(2, 2)
				+ conv(1, 2)
				+ pc2
				+ pc2).toAutomaton();
				
		r[9] = new RegExp(
				conv(2, 2) 
				+ pc2
				+ pc2 
				+ conv(0, 0)
				+ conv(1, 2)
				+ pc2
				+ pc2).toAutomaton();
		
		r[10] = new RegExp(
				conv(2, 3) 
				+ pc2
				+ pc2 
				+ conv(1, 1)
				+ conv(1, 2)
				+ pc2
				+ pc2).toAutomaton();
		r[11] = new RegExp(
				conv(2, 4) 
				+ pc2
				+ pc2 
				+ conv(2, 2)
				+ conv(1, 2)
				+ pc2
				+ pc2).toAutomaton();
		// next 2
		r[12] = new RegExp(
				pc2
				+ pc2
				+ pc2 
				+ conv(0, 1)
				+ pc2
				+ conv(0, 1)
				+ pc2).toAutomaton();
		
		r[13] = new RegExp(
				pc2 
				+ pc2
				+ pc2 
				+ conv(1, 2)
				+ pc2
				+ conv(1, 2)
				+ pc2).toAutomaton();
		// next 3
		
		r[14] = new RegExp(
				conv(1, 1) 
				+ conv(0, 3)
				+ pc2 
				+ pc2
				+ pc2
				+ pc2
				+ conv(0,1)).toAutomaton();
		
		r[15] = new RegExp(
				conv(2, 2) 
				+ conv(0, 6)
				+ pc2 
				+ pc2
				+ pc2
				+ pc2
				+ conv(0,1)).toAutomaton();
		r[16] = new RegExp(
				"(" + conv(0, 0) + "|" + conv(3, 3) + "|" + conv(4, 4) + "|" + conv(5, 5) + ")" 
				+ conv(0, 5)
				+ pc2 
				+ pc2
				+ pc2
				+ pc2
				+ conv(0,1)).toAutomaton();
		r[17] = new RegExp(
				conv(0, 0)
				+ pc2
				+ conv(0, 0)
				+ pc2
				+ pc2
				+ pc2
				+ conv(1,2)).toAutomaton();
		r[18] = new RegExp(
				conv(1, 1)
				+ pc2
				+ conv(1, 1)
				+ pc2
				+ pc2
				+ pc2
				+ conv(1,2)).toAutomaton();
		r[19] = new RegExp(
				conv(2, 2)
				+ pc2
				+ conv(2, 2)
				+ pc2
				+ pc2
				+ pc2
				+ conv(1,2)).toAutomaton();
		r[20] = new RegExp(
				conv(3, 3)
				+ pc2
				+ conv(3, 3)
				+ pc2
				+ pc2
				+ pc2
				+ conv(1,2)).toAutomaton();
		r[21] = new RegExp(
				conv(4, 4)
				+ pc2
				+ conv(4, 4)
				+ pc2
				+ pc2
				+ pc2
				+ conv(1,2)).toAutomaton();
		r[22] = new RegExp(
				conv(0, 0)
				+ pc2
				+ conv(0, 0)
				+ pc2
				+ pc2
				+ pc2
				+ conv(1,2)).toAutomaton();
		r[23] = new RegExp(
				pc2
				+ pc2
				+ pc2
				+ pc2
				+ conv(2,2)
				+ conv(2,2)
				+ conv(3,3)).toAutomaton();
		l[0] = new RegExp(
				pc2
				+ conv(0, 0)
				+ conv(0, 0)
				+ pc2
				+ pc2
				+ pc2
				+ conv(2,3)).toAutomaton();	
		l[1] = new RegExp(
				pc2
				+ conv(1, 1)
				+ conv(1, 1)
				+ pc2
				+ pc2
				+ pc2
				+ conv(2,3)).toAutomaton();	
		l[2] = new RegExp(
				pc2
				+ conv(2, 2)
				+ conv(2, 2)
				+ pc2
				+ pc2
				+ pc2
				+ conv(2,3)).toAutomaton();	
		l[3] = new RegExp(
				pc2
				+ conv(3, 3)
				+ conv(3, 3)
				+ pc2
				+ pc2
				+ pc2
				+ conv(2,3)).toAutomaton();	
		l[4] = new RegExp(
				pc2
				+ conv(4, 4)
				+ conv(4, 4)
				+ pc2
				+ pc2
				+ pc2
				+ conv(2,3)).toAutomaton();	
		l[5] = new RegExp(
				pc2
				+ "\\" + conv(5, 5)
				+ "\\" +conv(5, 5)
				+ pc2
				+ pc2
				+ pc2
				+ conv(2,3)).toAutomaton();
		Automaton t1 = new RegExp(
				pc2
				+ pc2
				+ pc2
				+ pc2
				+ pc2
				+ pc2
				+ conv(2,3)).toAutomaton();			
		
		// need repeating state
		
		// Union
		
		Automaton union = Automaton.makeEmpty();
		for (int i = 0; i < r.length; i++) {
			union = union.union(r[i]);
		}
		Automaton union2 = Automaton.makeEmpty();
		for (int i = 0; i < l.length; i++) {
			union2 = union2.union(l[i]);
		}
		union2 = t1.minus(union2);
		return Automaton.minimize(union.union(union2));
		//return Automaton.minimize(r[0]);
	}

}
