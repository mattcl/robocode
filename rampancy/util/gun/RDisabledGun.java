package rampancy.util.gun;

import rampancy.RampantRobot;
import rampancy.util.REnemyRobot;

public class RDisabledGun extends RGun {
	
	public static final String NAME = "Disabled robot gun";

	public RDisabledGun() {
		super(NAME);
	}

	@Override
	public RFiringSolution getFiringSolution(RampantRobot reference, REnemyRobot enemy) {
		if (enemy.getCurrentState().energy == 0) {
			return new RFiringSolution(this, enemy, 0.1, enemy.getCurrentState().absoluteBearing);
		}
		return null;
	}

}
