package rampancy.util.gun;

import java.awt.Graphics2D;

import rampancy.util.RDrawable;

public class RFiringSolution implements RDrawable {
	public double power;
	public double firingAngle;
	
	public RFiringSolution(double power, double firingAngle) {
		this.power = power;
		this.firingAngle = firingAngle;
	}

	public void draw(Graphics2D g) {
		// do nothing by default
	}
}

