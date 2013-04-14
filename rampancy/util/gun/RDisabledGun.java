package rampancy.util.gun;

import rampancy.RampantRobot;
import rampancy.util.REnemyRobot;

public class RDisabledGun extends RGun {
	
	public static final String NAME = "Disabled robot gun";

	public RDisabledGun() {
		super(NAME);
		// I don't care about the stats for this gun, since it is only used to
		// attack disabled robots
		this.displayStats = false;
	}

	@Override
	public RFiringSolution getFiringSolution(RampantRobot reference, REnemyRobot enemy) {
		if (enemy.getCurrentState().energy == 0) {
			return new RFiringSolution(this, enemy, 0.1, enemy.getCurrentState().absoluteBearing);
		}
		return null;
	}
	
	@Override
	public double getHitPercentage() {
		return 100;
	}
}
