package edu.illinois.automaticsafetygames.finitelybranching.teacher;

import java.util.Set;

import edu.illinois.automaticsafetygames.tools.Tools;

/**
 * <p>
 * This class represents an implication counterexample consisting of a word as
 * antecedent and a finite set of words as consequent.
 * </p>
 * 
 * <p>
 * For efficiency, the attributes of this class are public.
 * </p>
 * 
 * @author Daniel Neider
 *
 */
public class ImplicationCounterexample {

	/**
	 * The antecedent of the implication counterexample.
	 */
	public final String antecedent;

	/**
	 * The consequent of the implication counterexample.
	 */
	public final Set<String> consequent;

	/**
	 * Creates a new implication counterexample with the given antecedent and
	 * consequent.
	 * 
	 * @param antecedent
	 *            The antecedent
	 * @param consequent
	 *            The consequent
	 */
	public ImplicationCounterexample(String antecedent, Set<String> consequent) {
		this.antecedent = antecedent;
		this.consequent = consequent;
	}

	@Override
	public int hashCode() {
		
		final int prime = 31;
		
		int result = 1;
		
		result = prime * result
				+ ((antecedent == null) ? 0 : antecedent.hashCode());
		result = prime * result
				+ ((consequent == null) ? 0 : consequent.hashCode());
		
		return result;
	
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImplicationCounterexample other = (ImplicationCounterexample) obj;
		if (antecedent == null) {
			if (other.antecedent != null)
				return false;
		} else if (!antecedent.equals(other.antecedent))
			return false;
		if (consequent == null) {
			if (other.consequent != null)
				return false;
		} else if (!consequent.equals(other.consequent))
			return false;
		return true;
		
	}
	
	@Override
	public String toString() {

		String ret = Tools.toReadableString(antecedent) + " -> [";

		int i = 0;
		for (String c : consequent) {

			ret += Tools.toReadableString(c)
					+ (i < consequent.size() - 1 ? ", " : "");
			i += 1;

		}

		return ret + "]";

	}

}
