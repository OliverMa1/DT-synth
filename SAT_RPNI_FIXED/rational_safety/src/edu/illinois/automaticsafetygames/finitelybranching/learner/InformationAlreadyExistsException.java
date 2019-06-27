package edu.illinois.automaticsafetygames.finitelybranching.learner;

public class InformationAlreadyExistsException extends RuntimeException {
	private static final long serialVersionUID = 4476791075272295913L;

	public InformationAlreadyExistsException(String msg) {
		super(msg);
	}

}