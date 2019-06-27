package edu.illinois.automaticsafetygames.games.examples;
import edu.illinois.automaticsafetygames.tools.Tools;
import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import edu.illinois.automaticsafetygames.games.IGame;
import edu.illinois.automaticsafetygames.finitelybranching.teacher.*;

/**
 * <p>
 * A finitely branching version of the <em>program repair game</em> described by
 * Beyene et al. [1].
 * </p>
 * 
 * <p>
 * Configuration of the program are triples <code>(pc, lock, got_lock)</code>
 * consisting of a program counter <code>pc</code> (integer value between 0 and
 * 4), a boolean variable <code>lock</code> representing a lock, and a
 * non-negative integer variable <code>got_lock</code>.
 * </p>
 * 
 * <p>
 * A configurations is encoded as word in the following way:
 * <ol>
 * <li>the program counter is encoded by a symbol in the set
 * <em>{0, ..., 4}</em>;</li>
 * <li>the is encoded in binary by either the symbol 0 or 1; and</li>
 * <li>the value of the variable <code>got_lock</code> is encoded in unary as a
 * finite (potentially empty) sequence of 1s.</li>
 * </ol>
 * </p>
 * 
 * <p>
 * In the game as formulated by Beyene et al., the variable
 * <code>got_lock</code> can be nondeterministically be set to an arbitrary
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
 * @author Daniel Neider
 *
 */
public class Cindy2Game implements IGame {

	/**
	 * Default value for parameter <code>max</code>.
	 */
	public static final int MAX_CAP = 10;
	public static final int MAX_ADD = 5;


	/**
	 * Maximal number that the program variable <em>got_lock</em> can be
	 * increase or decreased.
	 */
	private int max_cap;
	private int max_add;

	/**
	 * Creates a new instance of the repair game.
	 */
	public Cindy2Game() {
		max_cap = MAX_CAP;
		max_add = MAX_ADD;
	}

	/**
	 * Creates a new instance of the repair game.
	 * 
	 * @param max
	 *            maximal number that the program variable <em>got_lock</em> can
	 *            be increase or decreased
	 */
	public Cindy2Game(int max1, int max2) {
		this.max_cap = max1;
		this.max_add = max2;
	}

	@Override
	public int getAlphabetSize() {
		return 4;
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
		Automaton[] p = new Automaton[6];
	    p[0] =  Automaton.minimize(Automaton.makeChar('\u0000')
				.concatenate((Automaton.makeCharSet("\u0000\u0002").repeat(max_cap,max_cap))
				.concatenate(Automaton.makeChar('\u0001')).repeat(5,5)));
		p[1] = Automaton.minimize(Automaton.makeChar('\u0000')
				.concatenate((Automaton.makeChar('\u0000').repeat())
				.concatenate(Automaton.makeChar('\u0002').repeat())
				.concatenate(Automaton.makeChar('\u0001')).repeat(5,5)));
		p[2] = Automaton.makeChar('\u0003');
		Automaton union = p[2].union(Automaton.minimize(p[0].intersection(p[1])));
					//	System.out.println(Tools.automatonToDot(union));
		//System.out.println(" P 1 \n " + Tools.automatonToDot(union));

		return Automaton.minimize(union);		
		
		}

	@Override
	public Automaton getPlayer1Vertices() {
		Automaton[] p = new Automaton[6];
	    p[0] =  Automaton.minimize(Automaton.makeChar('\u0001')
				.concatenate((Automaton.makeCharSet("\u0000\u0002").repeat(max_cap,max_cap))
				.concatenate(Automaton.makeChar('\u0001')).repeat(5,5)));
		p[1] = Automaton.minimize(Automaton.makeChar('\u0001')
				.concatenate((Automaton.makeChar('\u0000').repeat())
				.concatenate(Automaton.makeChar('\u0002').repeat())
				.concatenate(Automaton.makeChar('\u0001')).repeat(5,5)));
		Automaton union = Automaton.minimize(p[0].intersection(p[1]));	
			//	System.out.println(Tools.automatonToDot(union));
		//System.out.println(" P 1 \n " + Tools.automatonToDot(union));

		return union;		
	}

	@Override
	public Automaton getInitialVertices() {
		System.out.println("Testing...");
		Automaton t1 = new RegExp("\u0000\u0000\u0000\u0000\u0001\u0000\u0002\u0002\u0001\u0000\u0002\u0002\u0001\u0000\u0002\u0002\u0001\u0000\u0002\u0002\u0001").toAutomaton();
		Automaton t2 = AutomatonTeacher.computeImage(getTransitions(),t1,4);
		
		System.out.println(Tools.automatonToDot(t1));
		System.out.println("T2 : " + Tools.automatonToDot(t2));

		
		
		
		return Automaton.minimize(Automaton.makeCharRange('\u0000', '\u0001')
				.concatenate((Automaton.makeChar('\u0002').repeat(max_cap,max_cap))
				.concatenate(Automaton.makeChar('\u0001')).repeat(5,5)));
	}

	@Override
	public Automaton getSafeVertices() {
			    
		Automaton[] p = new Automaton[2];	    
		p[0] =  Automaton.minimize(Automaton.makeCharRange('\u0000','\u0001')
				.concatenate((Automaton.makeCharSet("\u0000\u0002").repeat(max_cap,max_cap))
				.concatenate(Automaton.makeChar('\u0001')).repeat(5,5)));
		p[1] = Automaton.minimize(Automaton.makeCharRange('\u0000','\u0001')
				.concatenate((Automaton.makeChar('\u0000').repeat())
				.concatenate(Automaton.makeChar('\u0002').repeat())
				.concatenate(Automaton.makeChar('\u0001')).repeat(5,5)));
		Automaton union = Automaton.minimize(p[0].intersection(p[1]));
		//System.out.println(Tools.automatonToDot(union));
		return union;
	}

	@Override
	public Automaton getTransitions() {
		Automaton[] r = new Automaton[5];
		// cind
		String z_2_star = "(" + conv(0,0) + "|" + "\\"+conv(2,2) + ")*";
		r[0] = new RegExp(
				conv(0, 1) 
				+ "(\\" +conv(2, 2) + "|" + conv(0,2) + ")*"
				+ conv(1, 1)
				+ "(\\" +conv(2, 2) + "|" + conv(0,2) + ")*"
				+ conv(1, 1)
				+ z_2_star
				+ conv(1, 1)
				+ z_2_star
				+ conv(1, 1)
				+ z_2_star
				+ conv(1, 1)).toAutomaton();
		r[1] = new RegExp(
				conv(0, 1) 
				+ z_2_star
				+ conv(1, 1)
				+ "(\\" +conv(2, 2) + "|" + conv(0,2) + ")*"
				+ conv(1, 1)
				+ "(\\" +conv(2, 2) + "|" + conv(0,2) + ")*"
				+ conv(1, 1)
				+ z_2_star
				+ conv(1, 1)
				+ z_2_star
				+ conv(1, 1)).toAutomaton();
		r[2] = new RegExp(
				conv(0, 1)
				+ z_2_star
				+ conv(1, 1) 
				+ z_2_star
				+ conv(1, 1)
				+ "(\\" +conv(2, 2) + "|" + conv(0,2) + ")*"
				+ conv(1, 1)
				+ "(\\" +conv(2, 2) + "|" + conv(0,2) + ")*"
				+ conv(1, 1)
				+ z_2_star
				+ conv(1, 1)).toAutomaton();
		r[3] = new RegExp(
				conv(0, 1) 
				+ z_2_star
				+ conv(1, 1)
				+ z_2_star
				+ conv(1, 1)
				+ z_2_star
				+ conv(1, 1)
				+ "(\\" +conv(2, 2) + "|" + conv(0,2) + ")*"
				+ conv(1, 1)
				+ "(\\" +conv(2, 2) + "|" + conv(0,2) + ")*"
				+ conv(1, 1)).toAutomaton();
		r[4] = new RegExp(
				conv(0, 1) 
				+ "(\\" +conv(2, 2) + "|" + conv(0,2) + ")*"
				+ conv(1, 1)
				+ z_2_star
				+ conv(1, 1)
				+ z_2_star
				+ conv(1, 1)
				+ z_2_star
				+ conv(1, 1)
				+ "(\\" +conv(2, 2) + "|" + conv(0,2) + ")*"
				+ conv(1, 1)).toAutomaton();
				
		//stepmother
		String stay =  "(" + "\\"+conv(2,2) + "|" + conv (0,0) + ")*" + conv(1, 1);
		Automaton[] s = new Automaton[5];
		s[0] = new RegExp(
					conv(0, 0) + "*"
					+ "\\"+conv(2, 0) 
					+ "\\"+conv(2, 2) + "*"
					+ conv(1, 1)
					+ stay
					+ stay
					+ stay
					+ stay).toAutomaton();
		s[1] = new RegExp(
					 stay
					+ conv(0, 0) + "*"
					+ "\\"+conv(2, 0) 
					+ "\\"+conv(2, 2) + "*"
					+ conv(1, 1)
					+ stay
					+ stay
					+ stay).toAutomaton();
		s[2] = new RegExp(
					 stay
					+ stay
					+ conv(0, 0) + "*"
					+ "\\"+conv(2, 0) 
					+ "\\"+conv(2, 2) + "*"
					+ conv(1, 1)
					+ stay
					+ stay).toAutomaton();
		s[3] = new RegExp(
					 stay
					+ stay
					+ stay
					+ conv(0, 0) + "*"
					+ "\\"+conv(2, 0) 
					+ "\\"+conv(2, 2) + "*"
					+ conv(1, 1)
					+ stay).toAutomaton();
		s[4] = new RegExp(
					 stay
					+ stay
					+ stay
					+ stay
					+ conv(0, 0) + "*"
					+ "\\"+conv(2, 0) 
					+ "\\"+conv(2, 2) + "*"
					+ conv(1, 1)).toAutomaton();
					
		// overflow
		Automaton[] o = new Automaton[6];
		String no_o =  "(" + "\\" +conv(0,4) + "|" + "\\" +conv(2,4) + ")*" + "\\" +conv(1,4);
		o[0] = new RegExp(
				"\\" + conv(1,3)
				+ no_o
				+ "\\" +conv(0,4) + "*"
				+ "\\" +conv(1,4) 
				+ no_o
				+ no_o
				+ no_o).toAutomaton();
		o[1] = new RegExp(
				"\\" + conv(1,3)
				+ no_o
				+ no_o
				+ "\\" +conv(0,4) + "*"
				+ "\\" +conv(1,4) 
				+ no_o
				+ no_o).toAutomaton();

		o[2] = new RegExp(
				"\\" + conv(1,3)
				+ no_o
				+ no_o
				+ no_o
				+ "\\" +conv(0,4) + "*"
				+ "\\" +conv(1,4) 
				+ no_o).toAutomaton();
		o[3] = new RegExp(
				"\\" + conv(1,3)
				+ no_o
				+ no_o
				+ no_o
				+ no_o
				+ "\\" +conv(0,4) + "*"
				+ "\\" +conv(1,4)).toAutomaton();
		o[4] = new RegExp(
				"\\" + conv(1,3)
				+ "\\" +conv(0,4) + "*"
				+ "\\" +conv(1,4) 
				+ no_o
				+ no_o
				+ no_o
				+ no_o).toAutomaton();
		o[5] = new RegExp("" + conv(3,3)).toAutomaton();
		// Union
		Automaton unionC = Automaton.makeEmpty();
		for (int i = 0; i < r.length; i++) {
			unionC = unionC.union(r[i]);
		}
		Automaton unionS = Automaton.makeEmpty();
		for (int i = 0; i < s.length; i++) {
			unionS = unionS.union(s[i]);
		}
		

		unionS = unionS.repeat(max_add,max_add);
		unionS = new RegExp(""+conv(1, 0)).toAutomaton().concatenate(unionS);
		
		Automaton unionO = Automaton.makeEmpty();
		for (int i = 0; i < o.length; i++) {
			unionO = unionO.union(o[i]);
		}
		//System.out.println(Tools.automatonToDot(unionC));
		Automaton union = unionO.union(unionS.union(unionC));
		//System.out.println(" Union O \n " + Tools.automatonToDot(unionO));
		//union.determinize();
		return Automaton.minimize(unionC);

	}

}
