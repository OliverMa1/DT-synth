package edu.illinois.automaticsafetygames.finitelybranching.learner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.illinois.automaticsafetygames.tools.Tools;
import dk.brics.automaton.Automaton;

/**
 * <p>
 * This class implements a prefix tree that stores a prefix closed set of words.
 * Each word is associated with
 * <ul>
 * <li>an ID (which starts with <code>0</code> and is unique for each word);
 * <li>a classification (see {@link Classification});
 * <li>an automaton (which might be <code>null</code>) representing the
 * consequent if the word is the antecedent of an <em>existential</em>
 * implication counterexample; and</li>
 * <li>an automaton (which might be <code>null</code>) representing the
 * consequent if the word is the antecedent of an <em>universal</em> implication
 * counterexample.</li>
 * </ul>
 * 
 * Since a word cannot be antecedent of two different types of implication
 * counterexamples, at least one of the automata has to be <code>null</code>.
 * </p>
 * 
 * <p>
 * Words in the tree are over the alphabet
 * <code>{0, ..., alphabetSize - 1}</code> of characters where
 * <code>alphabetSize</code> is a positive (non-zero) integer given as a
 * parameter when the prefix tree is constructed. The alphabet size cannot be
 * changed after creating the tree.
 * </p>
 * 
 * <p>
 * Each word in the tree is represented by a {@link Node}. The word represented
 * by a node is obtained by traversing the tree from the root to this node in
 * the following way: the root node corresponds to the empty word, and if node
 * <code>n</code> corresponds to the word <em>u</em>, then node
 * <code>n.children[i]</code> corresponds to the word <em>ui</em> (given that is
 * not <code>null</code>.
 * </p>
 * 
 * <p>
 * New words (and their attached information) are inserted by calling
 * {@link PrefixTree#add(String, Classification, Automaton, Automaton)}. A new
 * word can only be inserted if it does not override existing information; a
 * word overrides information if a corresponding node exists and
 * <ul>
 * <li>the classification of the new word is <code>ACCEPT</code> or
 * <code>REJECT</code> and the classification of the existing word is
 * <code>ACCEPT</code> or <code>REJECT</code>; or</li>
 * <li>at least one automaton associated to the new word is not
 * <code>null</code> and at least one automaton associated to the existing word
 * is not <code>null</code>.</li>
 * </ul>
 * 
 * If these conditions are not fulfilled, a
 * {@link InformationAlreadyExistsException} is thrown.
 * </p>
 * 
 * <p>
 * Adding a word to the tree also adds all of its prefixes that are not already
 * contained in the tree (unclassified and both automata set to
 * <code>null</code>. An numeric, monotonic growing, unique ID is automatically
 * assigned to each inserted word. The tree asserts that the IDs range between
 * <code>0</code> and the total number of words in the tree minus one.
 * </p>
 * 
 * <p>
 * Words cannot be deleted from the tree, but their information can be updated
 * if this does not override previous information (see above for details).
 * </p>
 * 
 * @author Daniel Neider
 *
 */
public class PrefixTree {

	/**
	 * The classification of a word represented by a node in the prefix tree.
	 * 
	 * @author Daniel Neider
	 *
	 */
	public enum Classification {

		/**
		 * Indicates that the word should be accepted.
		 */
		ACCEPT,

		/**
		 * Indicates that the word should be rejected.
		 */
		REJECT,

		/**
		 * Indicates that the word is unclassified.
		 */
		UNCLASSIFIED
	}

	/**
	 * The root node of this tree
	 */
	Node root;

	/**
	 * The number of nodes in this tree
	 */
	int numberOfNodes;

	/**
	 * The alphabet size assumed by this prefix tree
	 */
	int alphabetSize;

	/**
	 * Creates a prefix tree with exactly one root node (representing the empty
	 * word) with the given alphabet size (see {@link PrefixTree#alphabetSize}).
	 * The ID of the root is <code>0</code>, it is unclassified and both
	 * automata are <code>null</code>.
	 * 
	 * @param alphabetSize
	 *            The alphabet size to use (must be greater than <code>0</code>)
	 */
	public PrefixTree(int alphabetSize) {

		// Check arguments
		if (alphabetSize <= 0) {
			throw new IllegalArgumentException(
					"alphabetSize has to be grater than 0");
		}

		// Store argument
		this.alphabetSize = alphabetSize;

		// Create root
		root = new Node(0, Classification.UNCLASSIFIED, null, null, null,
				alphabetSize);
		numberOfNodes = 1;

	}

	public void add(String word, Classification classification,
			Set<String> existentialConsequent, Set<String> universalConsequent)
			throws InformationAlreadyExistsException {

		Node n = getOrCreate(word);

		// Check insert
		if (classification != Classification.UNCLASSIFIED
				&& n.classification != Classification.UNCLASSIFIED) {
			throw new InformationAlreadyExistsException(
					"String with classification already exists");
		}
		if ((n.existentialConsequent != null || n.universalConsequent != null)
				&& (existentialConsequent != null || universalConsequent != null)) {
			throw new InformationAlreadyExistsException(
					"String with consequent already exists");
		}

		// Attach info
		n.classification = classification;

		// Add existential consequent if existing
		if (existentialConsequent != null) {

			Set<Node> nodes = new HashSet<>();
			for (String s : existentialConsequent) {
				nodes.add(getOrCreate(s));
			}
			n.existentialConsequent = nodes;

		}

		// Add universal consequent if existing
		if (universalConsequent != null) {

			Set<Node> nodes = new HashSet<>();
			for (String s : universalConsequent) {
				nodes.add(getOrCreate(s));
			}
			n.universalConsequent = nodes;

		}

	}

	private Node getOrCreate(String word) {

		Node cur = root;
		char[] c = word.toCharArray();

		// Add all prefixes of the string
		for (int i = 0; i < c.length; i++) {

			// Successor exists
			Node next = cur.children[c[i]];
			if (next != null) {
				cur = next;
			}

			// Successor does not exists and, hence, create it
			else {

				next = new Node(numberOfNodes, Classification.UNCLASSIFIED,
						cur, null, null, alphabetSize);
				numberOfNodes += 1;
				cur.children[c[i]] = next;
				cur = next;

			}

		}

		return cur;

	}

	/**
	 * Retrieves the word represented by the given node.
	 * 
	 * @param n
	 *            a node
	 * @return Returns the word represented by the given node.
	 */
	public String getWord(Node n) {

		StringBuilder ret = new StringBuilder();

		Node cur = n;
		while (cur.parent != null) {

			Node next = cur.parent;
			for (int i = 0; i < next.children.length; i++) {
				if (next.children[i] != null && next.children[i].id == cur.id) {

					ret.append((char) i);
					break;

				}
			}
			cur = next;

		}

		return ret.reverse().toString();

	}

	/**
	 * Returns the number of nodes (i.e., the number of words) contained in the
	 * tree.
	 * 
	 * @return The number of nodes contained in the tree.
	 */
	public int getNumberOfNodes() {
		return numberOfNodes;
	}

	/**
	 * Checks whether a given automaton is consistent with this prefix tree.
	 * 
	 * @param aut
	 *            the automaton to check
	 * @return Returns <code>true</code> if the automaton is consistent and
	 *         <code>false</code> otherwise.
	 */
	public boolean automatonIsConsistent(Automaton automaton) {

		/**
		 * Class defining an entry of the work list.
		 * 
		 * @author Daniel Neider
		 *
		 */
		class Pair {

			public Node node;

			public String word;

			public Pair(Node node, String word) {

				this.node = node;
				this.word = word;

			}

		}

		/*
		 * Prepare work list
		 */
		ArrayList<Pair> worklist = new ArrayList<>();
		worklist.add(new Pair(root, ""));

		/*
		 * Process work list
		 */
		while (!worklist.isEmpty()) {

			// Get current element of work list
			Pair current = worklist.remove(worklist.size() - 1);
			Node node = current.node;
			String word = current.word;

			// Check positive classification
			if (node.classification == Classification.ACCEPT) {
				if (!automaton.run(word)) {

					System.err.println("Automaton does not accept "
							+ Tools.toReadableString(word));
					return false;

				}
			}

			// Check negative classification
			else if (node.classification == Classification.REJECT) {
				if (automaton.run(word)) {

					System.err.println("Automaton does not reject "
							+ Tools.toReadableString(word));
					return false;

				}
			}

			// Check existential implication
			if (node.existentialConsequent != null) {

				if (automaton.run(word)) {

					// Check each word
					boolean ok = false;
					for (Node n : node.existentialConsequent) {
						if (automaton.run(getWord(n))) {

							ok = true;
							break;

						}
					}

					if (!ok) {

						System.err
								.println("Automaton is not existentially closed for antecedent "
										+ Tools.toReadableString(word)
										+ " (ID " + node.id + ")");
						return false;

					}

				}

			}

			// Check universal implication
			if (node.universalConsequent != null) {

				if (automaton.run(word)) {

					// Check each word in the consequent
					boolean ok = true;
					for (Node n : node.universalConsequent) {
						if (!automaton.run(getWord(n))) {

							ok = false;
							break;

						}
					}

					if (!ok) {

						System.err
								.println("Automaton is not universally closed for antecedent "
										+ Tools.toReadableString(word)
										+ " (ID " + node.id + ")");
						return false;

					}

				}

			}

			// Process children of current node
			for (int a = 0; a < node.children.length; a++) {

				Node next = node.children[a];
				if (next != null) {
					worklist.add(new Pair(next, word + (char) a));
				}

			}

		}

		return true;

	}

	/**
	 * Returns a string representation of this prefix tree
	 */
	@Override
	public String toString() {

		StringBuffer treeBuffer = new StringBuffer();
		StringBuffer automatonBuffer = new StringBuffer();
		toString(root, 0, "", treeBuffer, automatonBuffer);

		return String.format("%1$s%n%2$s", treeBuffer, automatonBuffer);

	}

	/**
	 * Recursively constructs a string representation of the tree rooted at
	 * <code>node</code>.
	 * 
	 * @param node
	 *            The node at which to start the recursive descend (must not be
	 *            <code>null</code>)
	 * @param depth
	 *            The depth of the node in the tree
	 * @param word
	 *            The word represented by this node (must not be
	 *            <code>null</code>)
	 * @param treeBuffer
	 *            The buffer to write the tree to (must not be <code>null</code>
	 *            )
	 * @param automatonBuffer
	 *            The buffer to write the automata to (must not be
	 *            <code>null</code>)
	 */
	private void toString(Node node, int depth, String word,
			StringBuffer treeBuffer, StringBuffer automatonBuffer) {

		// Output current node
		for (int i = 0; i < depth; i++) {
			treeBuffer.append("  ");
		}
		treeBuffer.append(String.format("'%1$s' (id: %2$d; %3$s%4$s)%n", word,
				node.id, node.classification,
				(node.existentialConsequent == null ? "" : "; existential"),
				(node.universalConsequent == null ? "" : "; universal")));

		// Output automaton
		if (node.existentialConsequent != null) {
			automatonBuffer.append(String.format(
					"%nExistential consequent for '%1$s':%n", word));

			for (Node n : node.existentialConsequent) {
				automatonBuffer.append(Tools.toReadableString(getWord(n))
						+ "\n");
			}

		}
		if (node.universalConsequent != null) {
			automatonBuffer.append(String.format(
					"%nUniversal consequent for '%1$s':%n", word));

			for (Node n : node.universalConsequent) {
				automatonBuffer.append(Tools.toReadableString(getWord(n))
						+ "\n");
			}

		}

		// Make recursive call
		for (int i = 0; i < node.children.length; i++) {
			if (node.children[i] != null) {
				toString(node.children[i], depth + 1, (word + i), treeBuffer,
						automatonBuffer);
			}
		}

	}

	public static void main(String[] args) {

		PrefixTree t = new PrefixTree(2);
		HashSet<String> consequent = new HashSet<>();

		consequent.add("\u0000\u0001");
		consequent.add("\u0000\u0000\u0001\u0000");
		t.add("\u0001\u0001", Classification.ACCEPT, null, consequent);

		consequent.clear();
		consequent.add("\u0001\u0001");
		// t.add("\u0000\u0001", Classification.REJECT, consequent, null);

		System.out.println(t);

	}

}
