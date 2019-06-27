package edu.illinois.automaticsafetygames.finitelybranching.learner.rpni;

public class StateTransition {

    State start;
    String symbol;

    StateTransition(State st, String sy) {
        this.start = st;
        this.symbol = sy;
    }

    @Override
    public String toString() {
        return start.label + "->" + symbol;
    }
}