package rampancy.util.movement;

import java.util.List;

import rampancy.RampantRobot;
import rampancy.util.wave.REnemyWave;

public interface RMovementManager {

	public void update(RampantRobot reference, REnemyWave wave);
    public void updateEndOfRound(RampantRobot reference);
    public RMovementChoice getMovementChoice(RampantRobot reference);
    public RMovementChoice getMovementChoice(RampantRobot reference, List<REnemyWave> waves);
}
