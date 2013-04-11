package rampancy.util.wave;

import java.awt.Color;

import rampancy.util.RDrawable;
import rampancy.util.REnemyRobot;
import rampancy.util.RPoint;
import rampancy.util.RUtil;

abstract public class RWave implements RDrawable {

	protected Color color;
	protected RPoint origin;
	protected long startTime;
	protected double power;
	protected double velocity;
	protected double distanceTraveled;
	protected boolean isVirtual;
	
	public RWave(RPoint origin, long startTime, double power, Color color) {
		this(origin, startTime, power, color, false);
	}
	
	public RWave (RPoint origin, long startTime, double power, Color color, boolean isVirtual) {
		this.origin = origin.getCopy();
		this.startTime = startTime;
		this.power = power;
		this.velocity = RUtil.computeBulletVelocity(power);
		this.distanceTraveled = velocity;
		this.isVirtual = isVirtual;
	}
	
	public void update(long time) {
		distanceTraveled = (time - startTime) * getVelocity();
	}
	
	public double distanceTo(RPoint point) {
		return origin.distance(point) - getDistanceTraveled();
	}
	
	public double distanceTo(RPoint point, long timeOffset) {
		return origin.distance(point) - getDistanceTraveled() + getVelocity() * timeOffset;
	}
	
	public long timeToImpact(RPoint point) {
		double remainingDistance = distanceTo(point) - getDistanceTraveled();
		return (long) (remainingDistance / getVelocity());
	}
	
	public Color getColor() {
		return color;
	}

	public RPoint getOrigin() {
		return origin;
	}

	public long getStartTime() {
		return startTime;
	}

	public double getVelocity() {
		return velocity;
	}

	public double getDistanceTraveled() {
		return distanceTraveled;
	}

	public boolean isVirtual() {
		return isVirtual;
	}
	
	public void computeIntersection(RPoint botLocation) {
		RPoint topLeft = new RPoint(botLocation.x - REnemyRobot.BOT_RADIUS, botLocation.y + REnemyRobot.BOT_RADIUS);
		RPoint topRight = new RPoint(botLocation.x + REnemyRobot.BOT_RADIUS, botLocation.y + REnemyRobot.BOT_RADIUS);
		RPoint bottomLeft = new RPoint(botLocation.x - REnemyRobot.BOT_RADIUS, botLocation.y - REnemyRobot.BOT_RADIUS);
		RPoint bottoimRight = new RPoint(botLocation.x + REnemyRobot.BOT_RADIUS, botLocation.y - REnemyRobot.BOT_RADIUS);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
		long temp;
		temp = Double.doubleToLongBits(power);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (int) (startTime ^ (startTime >>> 32));
		temp = Double.doubleToLongBits(velocity);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RWave other = (RWave) obj;
		if (origin == null) {
			if (other.origin != null)
				return false;
		} else if (!origin.equals(other.origin))
			return false;
		if (Double.doubleToLongBits(power) != Double
				.doubleToLongBits(other.power))
			return false;
		if (startTime != other.startTime)
			return false;
		if (Double.doubleToLongBits(velocity) != Double
				.doubleToLongBits(other.velocity))
			return false;
		return true;
	}
}
