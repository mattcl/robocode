package rampancy.util.movement;

import java.awt.Graphics2D;

import rampancy.util.RDrawable;

public class RMovementChoice implements RDrawable {
	public double goAngle;
	public double distance;
	public double velocity;
	
    public RMovementChoice(double goAngle, double distance) {
    	this.goAngle = goAngle;
    	this.distance = distance;
    }

	@Override
	public void draw(Graphics2D g) {
		// DO NOTHING
	}
}
