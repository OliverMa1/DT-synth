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
public class RepairCriticalGame implements IGame {

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
	public RepairCriticalGame() {
		max = MAX_DEFAULT;
	}

	/**
	 * Creates a new instance of the repair game.
	 * 
	 * @param max
	 *            maximal number that the program variable <em>got_lock</em> can
	 *            be increase or decreased
	 */
	public RepairCriticalGame(int max) {
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
		return Automaton.minimize(Automaton.makeCharRange('\u0000', '\u0008')
				.concatenate(Automaton.makeCharRange('\u0000', '\u0008'))
				.concatenate(Automaton.makeCharRange('\u0000', '\u0001').repeat(5,5))
				.concatenate(Automaton.makeChar('\u0001').repeat()));
	}

	@Override
	public Automaton getPlayer1Vertices() {
		return Automaton.makeEmpty();
	}

	@Override
	public Automaton getInitialVertices() {
		//System.out.println(Automaton.makeChar('\u0000').repeat(2, 2));
		return (Automaton.makeChar('\u0000').repeat(7,7));
	}

	@Override
	public Automaton getSafeVertices() {

		Automaton t1 = new RegExp("\u0003\u0003").toAutomaton()
				.concatenate(Automaton.makeCharRange('\u0000', '\u0001').repeat(5,5))
				.concatenate(Automaton.makeChar('\u0001').repeat());
		Automaton t2 = new RegExp("\u0007\u0006").toAutomaton()
				.concatenate(Automaton.makeCharRange('\u0000', '\u0001').repeat(5,5))
				.concatenate(Automaton.makeChar('\u0001').repeat());

		return getPlayer0Vertices().minus(t1.union(t2));
	}

	@Override
	public Automaton getTransitions() {

		Automaton[] r = new Automaton[23];
		String pc2 =  "(" + conv(0,0) + "|" + conv(1,1)+ "|\\" + conv(2,2)+ "|\\" + conv(3,3)+ "|\\" + conv(4,4)+ "|\\" + conv(5,5)+ "|\\" + conv(6,6)+ "|\\" + conv(7,7)+ "|\\"  + conv(8,8) + ")";
		// Rho 0
		r[0] = new RegExp(
				"\\" + conv(0, 1) 
				+ pc2
				+ "(" + conv(0, 1) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"  
				+ conv(1, 1) + "*").toAutomaton();

		// Rho 1
		r[1] = new RegExp(
				"\\" + conv(1, 2) 
				+ pc2
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(\\" + conv(1, 9) + "{0," + max + "}|\\" + conv(9, 1) + "{0," + max
				+ "})").toAutomaton();
				
		// Rho 2
		r[2] = new RegExp(
				"\\" + conv(2, 2) 
				+ pc2
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(1, 1) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"  
				+ conv(1, 1) + "").toAutomaton();
		// Rho 3
		r[3] = new RegExp(
				"\\" + conv(2, 3) 
				+ pc2
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "((" + conv(0, 0) + "|" + conv(0, 0)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"  
				+ conv(1, 1) + "*)|"
				+ "((" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(1, 1) + "|" + conv(1, 1)+ ")"
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ "))").toAutomaton();
	
		// Rho 4
		r[4] = new RegExp(
				"\\" + conv(3, 4) 
				+ pc2
				+ "(" + conv(0, 0) + "|" + conv(1, 0)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"  
				+ conv(1, 1) + "*").toAutomaton();

		// Rho 5
		r[5] = new RegExp(
				"\\" + conv(4, 5) 
				+ pc2
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 1) + "|" + conv(1, 1)+ ")"
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"  
				+ conv(1, 1) + "").toAutomaton();
				
		// Rho 6
		r[6] = new RegExp(
				"\\" + conv(4, 8) 
				+ pc2
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"  
				+ conv(1, 1) + "").toAutomaton();
				
		// Rho 7
		r[7] = new RegExp(
				"\\" + conv(5, 6) 
				+ pc2
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 1) + "|" + conv(1, 1)+ ")"  
				+ conv(1, 1) + "*").toAutomaton();

		// Rho 8
		r[8] = new RegExp(
				"\\" + conv(6, 6) 
				+ pc2
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"
				+ "(" + conv(1, 1) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(1, 1) + "|" + conv(1, 1)+ ")"  
				+ conv(1, 1) + "*").toAutomaton();

		// Rho 9
		r[9] = new RegExp(
				"\\" + conv(6, 7) 
				+ pc2
				+ "(" + conv(0, 0) + "|" + conv(1, 0)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"
				+ "((" + conv(0, 0) + "|" + conv(0, 0)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"  
				+ conv(1, 1) + "*)|"
				+ "((" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(0, 0)+ ")"  
				+ conv(1, 1) + "*)").toAutomaton();

		// Rho 10
		r[10] = new RegExp(
				"\\" + conv(7, 8) 
				+ pc2
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 0)+ ")"
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"  
				+ conv(1, 1) + "*").toAutomaton();
				
		// Rho 11
		r[11] = new RegExp(
				"\\" + conv(8, 0) 
				+ pc2
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"  
				+ conv(1, 1) + "*").toAutomaton();
				
		// Rho 12
		r[12] = new RegExp(
				pc2
				+ "\\" + conv(0,1)
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 1) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"  
				+ conv(1, 1) + "*").toAutomaton();
				
		// Rho 13
		r[13] = new RegExp(
				pc2
				+ "\\" + conv(1,2)
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")\\"  
				+ conv(1, 9) + "*").toAutomaton();
				
		// Rho 14
		r[14] = new RegExp(
				pc2
				+ "\\" + conv(2,2)
				+ "(" + conv(1, 1) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")").toAutomaton();
				
		// Rho 15
		r[15] = new RegExp(
				pc2
				+ "\\" + conv(2,3)
				+ "((" + conv(0, 0) + "|" + conv(0, 0)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"  
				+ conv(1, 1) + "*)|"
				+ "((" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"  
				+ conv(1, 1) + ")").toAutomaton();
				
		// Rho 16
		r[16] = new RegExp(
				pc2
				+ "\\" + conv(3,4)
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"
				+ conv(1, 1) + "*").toAutomaton();
				
		// Rho 17
		r[17] = new RegExp(
				pc2
				+ "\\" + conv(4,5)
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 0)+ ")"
				+ conv(1, 1) + "*").toAutomaton();
				
		// Rho 18
		r[18] = new RegExp(
				 pc2
				+ "\\" + conv(5,5)
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(1, 1) + "|" + conv(1, 1)+ ")"
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(0, 0)+ ")"
				+ conv(1, 1) + "*").toAutomaton();
				
		// Rho 19
		r[19] = new RegExp(
			 pc2
				+ "\\" + conv(5,6)
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "((" + conv(0, 0) + "|" + conv(0, 0)+ ")"
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"
				+ conv(1, 1) + "*)|"
				+ "((" + conv(0, 0) + "|" + conv(1, 1)+ ")"
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(1, 1) + "|" + conv(1, 1)+ ")"
				+ conv(1, 1) + "*)").toAutomaton();

		// Rho 20
		r[20] = new RegExp(
				 pc2
				+ "\\" + conv(6,7)
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"
				+ "(" + conv(0, 0) + "|" + conv(1, 0)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(0, 0)+ ")"
				+ conv(1, 1) + "*").toAutomaton();	

		// Rho 21
		r[21] = new RegExp(
				 pc2
				+ "\\" + conv(7,8)
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 0)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"
				+ conv(1, 1) + "*").toAutomaton();		

		// Rho 22
		r[22] = new RegExp(
				 pc2
				+ "\\" + conv(8,0)
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")" 
				+ "(" + conv(0, 0) + "|" + conv(1, 1)+ ")"
				+ conv(1, 1) + "*").toAutomaton();
		// Union
		
		Automaton union = Automaton.makeEmpty();
		for (int i = 0; i < r.length; i++) {
			union = union.union(r[i]);
		}

		return Automaton.minimize(union);

	}

}
