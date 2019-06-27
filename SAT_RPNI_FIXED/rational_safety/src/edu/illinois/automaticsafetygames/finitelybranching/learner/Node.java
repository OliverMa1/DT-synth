package edu.illinois.automaticsafetygames.finitelybranching.learner;

import java.util.Set;

import edu.illinois.automaticsafetygames.finitelybranching.learner.PrefixTree.Classification;

/**
 * <p>
 * This class implements a node in a prefix tree (see {@link PrefixTree}). As
 * usual for recursive data structures, a node contains several attributes and a
 * set of child nodes, modeled as an array of fixed length. The length of this
 * array is determined by the (positive) integer <code>alphabetSize</code>,
 * which is passed when constructing a node. However, the alphabet size is not
 * stored explicitly but can be retrieved as <code>children.length</code> if
 * necessary.
 * </p>
 * 
 * <p>
 * For efficiency reasons, all attributes of a node allow public access.
 * </p>
 * 
 * @author Daniel Neider
 */
public class Node {

	Node parent;

	public Set<Node> existentialConsequent;

	public Set<Node> universalConsequent;

	/**
	 * The classification
	 */
	public Classification classification;

	/**
	 * The id of the word represented by this node
	 */
	public int id;

	/**
	 * The children of the node, index by the character to append
	 */
	public Node[] children;

	/**
	 * <p>
	 * Creates a new node with the given arguments.
	 * </p>
	 * <p>
	 * The argument <code>classification</code> must not be </code>null</code>;
	 * the arguments <code>existentialConsequent</code> and
	 * <code>universalConsequent</code> must not both be different from
	 * <code>null</code>. For efficiency, however, these conditions are not
	 * checked.
	 * </p>
	 * 
	 * @param id
	 *            The ID attached to this node
	 * @param classification
	 *            The classification attached to this node (must not be
	 *            <code>null</code>)
	 * @param parent
	 *            The parent of this node
	 * @param existentialConsequent
	 *            a set of nodes constituting the consequent of an existential
	 *            implication counterexample
	 * @param universalConsequent
	 *            a set of nodes constituting the consequent of a universal
	 *            implication counterexample
	 * @param alphabetSize
	 *            The alphabet size to use
	 */
	public Node(int id, Classification classification, Node parent,
			Set<Node> existentialConsequent, Set<Node> universalConsequent,
			int alphabetSize) {

		this.id = id;
		this.classification = classification;
		this.parent = parent;
		this.existentialConsequent = existentialConsequent;
		this.universalConsequent = universalConsequent;
		this.children = new Node[alphabetSize];

	}

}
