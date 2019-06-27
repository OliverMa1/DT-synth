package edu.illinois.automaticsafetygames.finitelybranching.learner.rpni;

public class State {

    public String label;
    public boolean reachable;

    @Override
    public String toString() {
        return label + (reachable ? "T" : "F");
    }

    State copy() {
        State s = new State();
        s.label = this.label;
        return s;
    }
    
}