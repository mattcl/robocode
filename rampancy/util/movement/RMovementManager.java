package rampancy.util.movement;

import rampancy.RampantRobot;

public interface RMovementManager {

	public void update(RampantRobot reference);
    public void updateEndOfRound(RampantRobot reference);
    public RMovementChoice getMovementChoice(RampantRobot reference);
}
