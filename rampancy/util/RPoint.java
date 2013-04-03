package rampancy.util;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import robocode.util.Utils;

public class RPoint extends Point2D.Double implements RDrawable {
	
	public RPoint(double x, double y) {
		super(x, y);
	}
	
	public RPoint getCopy() {
		return (RPoint) this.clone();
	}
	
	public RPoint projectTo(double angle, double distance) {
		double px = x + Math.sin(angle) * distance;
		double py = y + Math.cos(angle) * distance;
		return new RPoint(px, py);
	}

	public double computeAbsoluteBearingTo(RPoint destination) {
        return Utils.normalAbsoluteAngle(Math.atan2(destination.x - x, destination.y - y));
    }
	
	public double computeAbsoluteBearingFrom(RPoint source) {
        return Utils.normalAbsoluteAngle(Math.atan2(x - source.x, y - source.y));
    }

	public void draw(Graphics2D g) {
		
	}
}
