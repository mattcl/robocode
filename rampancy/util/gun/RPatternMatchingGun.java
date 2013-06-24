package rampancy.util.gun;

import java.util.ArrayList;
import java.util.HashMap;

import rampancy.RampantRobot;
import rampancy.util.REnemyRobot;
import rampancy.util.RRobotState;

public class RPatternMatchingGun extends RGun {
   
    public static final String NAME = "Pattern Matching Gun";
    public static final int HIST_SIZE = 15;

    public RPatternMatchingGun() {
        super(NAME);
    }

    @Override
    public RFiringSolution getFiringSolution(RampantRobot reference, REnemyRobot enemy) {
        ArrayList<RRobotState> states = enemy.getLastNStates(HIST_SIZE);
        if (states == null) {
            return null;
        }
        return null;
    }
    
    class Node {
        HashMap<State, Node> map;
        State state;
        
        public Node(State state) {
            this.state = state;
            map = new HashMap<State, Node>();
        }
    }
    
    class State {
        int deltaH;
        int deltaV;
        
        public State(int deltaH, int deltaV) {
            this.deltaH = deltaH;
            this.deltaV = deltaV;
        }

        private RPatternMatchingGun getOuterType() {
            return RPatternMatchingGun.this;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + deltaH;
            result = prime * result + deltaV;
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
            State other = (State) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (deltaH != other.deltaH)
                return false;
            if (deltaV != other.deltaV)
                return false;
            return true;
        }
    }
}
