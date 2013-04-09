package rampancy.util.gun;

import rampancy.RampantRobot;
import rampancy.util.REnemyRobot;
import rampancy.util.RPoint;
import rampancy.util.RRobotState;
import rampancy.util.RUtil;

public class RCircularTargetingGun extends RGun {
	
	public static final String NAME = "Circular targeting gun";
	public static final int MAX_PROJECTED_TURNS = 100;

	public RCircularTargetingGun() {
		super(NAME);
	}

	@Override
	public RFiringSolution getFiringSolution(RampantRobot reference, REnemyRobot enemy) {
		RRobotState currentState = enemy.getCurrentState();
		double heading = currentState.heading;
		double velocity = currentState.velocity;
		double deltaH = currentState.deltaH;
		RPoint location = currentState.location.getCopy();
		
		// TODO: compute this somehow
		double maxBulletPower = 2.0;
		for (int i = 0; i < MAX_PROJECTED_TURNS; i++) {
			heading += deltaH;
			location = RUtil.project(location, heading, velocity);
			
		}
		return null;
	}

}
