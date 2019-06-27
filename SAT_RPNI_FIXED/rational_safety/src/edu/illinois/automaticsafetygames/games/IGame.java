package edu.illinois.automaticsafetygames.games;

import dk.brics.automaton.Automaton;

public interface IGame {

	int getAlphabetSize();
	
	Automaton getPlayer0Vertices();
	
	Automaton getPlayer1Vertices();
	
	Automaton getInitialVertices();
	
	Automaton getSafeVertices();
	
	Automaton getTransitions();
	
}
