package rampancy.util.gun;

import java.awt.Graphics2D;

import rampancy.util.RDrawable;
import rampancy.util.REnemyRobot;

public class RFiringSolution implements RDrawable {
	public REnemyRobot target;
	public double power;
	public double firingAngle;
	
	public RFiringSolution(REnemyRobot target, double power, double firingAngle) {
		this.target = target;
		this.power = power;
		this.firingAngle = firingAngle;
	}

	public void draw(Graphics2D g) {
		// do nothing by default
	}
}

