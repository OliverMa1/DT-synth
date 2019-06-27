package edu.illinois.automaticsafetygames.finitelybranching.learner;

import edu.illinois.automaticsafetygames.finitelybranching.teacher.ImplicationCounterexample;
import dk.brics.automaton.Automaton;

/**
 * <p>
 * Interface defining how a edu.illinois.automaticsafetygames.finitelybranching.learner accepts counterexamples and produces
 * conjectures.
 * </p>
 * 
 * <p>
 * <b>As general requirement, a edu.illinois.automaticsafetygames.finitelybranching.learner implementing this interface must produce
 * a conjecture that is consistent with the counterexamples that have been added
 * thus far.</b>
 * </p>
 * 
 * <p>
 * When adding positive and negative counterexamples, a edu.illinois.automaticsafetygames.finitelybranching.learner should throw a
 * {@link InformationAlreadyExistsException} if the counterexample has
 * already been added as a positive or negative counterexample. Likewise, the
 * edu.illinois.automaticsafetygames.finitelybranching.learner should throw such an exception if the implication counterexample has
 * been added before (i.e., the antecedent occurs as antecedent in another
 * implication counterexample, regardless of the type). However, adding an
 * implication counterexample whose antecedent is an existing positive or
 * negative counterexample, or adding a positive or negative counterexample that
 * occurs as antecedent of an implication counterexample is permitted.
 * </p>
 * 
 * @author Daniel Neider
 *
 */
public interface ILearner {

	/**
	 * Adds a new positive counterexample.
	 * 
	 * @param positive
	 *            The positive counterexample (must not be <code>null</code>)
	 * @throws InformationAlreadyExistsException
	 *             if <code>positive</code> has been added before as either
	 *             positive or negative counterexample
	 */
	void addPositiveCounterexample(String positive)
			throws InformationAlreadyExistsException;

	/**
	 * Adds a new negative counterexample.
	 * 
	 * @param negative
	 *            The negative counterexample (must not be <code>null</code>)
	 * @throws InformationAlreadyExistsException
	 *             if <code>negative</code> has been added before as either
	 *             positive or negative counterexample
	 */
	void addNegativeCounterexample(String negative)
			throws InformationAlreadyExistsException;

	/**
	 * Adds a new existential implication counterexample.
	 * 
	 * @param impl
	 *            The existential implication counterexample (must not be
	 *            <code>null</code>)
	 * @throws InformationAlreadyExistsException
	 *             if <code>impl</code> has been added before as either
	 *             existential or universal implication counterexample
	 */
	void addExistentialImplicationCounterexample(ImplicationCounterexample impl)
			throws InformationAlreadyExistsException;

	/**
	 * Adds a new universal implication counterexample.
	 * 
	 * @param impl
	 *            The universal implication counterexample (must not be
	 *            <code>null</code>)
	 * @throws InformationAlreadyExistsException
	 *             if <code>impl</code> has been added before as either
	 *             existential or universal implication counterexample
	 */
	void addUniversalImplicationCounterexample(ImplicationCounterexample impl)
			throws InformationAlreadyExistsException;

	/**
	 * Derives a conjecture that <b>must be consistent</b> with the
	 * counterexamples added thus for.
	 * 
	 * @return A conjecture consistent with the counterexamples.
	 * @throws Exception
	 */
	Automaton deriveConjecture() throws Exception;

}
