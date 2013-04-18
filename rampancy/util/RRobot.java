package rampancy.util;

public interface RRobot {
    
    public RRobotState getCurrentState();
    //public RRobotState getFiringState(); TODO: maybe implement this
    public RRobotState getTargetableState();

}
