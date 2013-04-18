package rampancy.util.movement;

import rampancy.RampantRobot;
import rampancy.util.wave.REnemyWave;

public interface RMovementManager {

	public void update(RampantRobot reference);
    public void updateEndOfRound(RampantRobot reference);
    public RMovementChoice getMovementChoice(RampantRobot reference);
    public RMovementChoice getMovementChoice(RampantRobot reference, REnemyWave wave);
}
