package rampancy.util.wave;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import rampancy.RampantRobot;
import rampancy.util.RDrawable;
import rampancy.util.REnemyRobot;
import rampancy.util.RPoint;
import rampancy.util.RRobot;
import rampancy.util.RRobotState;
import rampancy.util.RUtil;
import rampancy.util.movement.RMovementPath;
import robocode.util.Utils;

abstract public class RWave implements RDrawable {
	public static final Color WAVE_COLOR = new Color(0x3B3B3B);

	protected Color color;
	protected RPoint origin;
	protected long startTime;
	protected double power;
	protected double velocity;
	protected double distanceTraveled;
	protected boolean isVirtual;
	protected RRobot creator;
	protected RRobot target;
	protected RRobotState initialCreatorState;
	protected RRobotState initialTargetState;
	protected double escapeAngleClockwise;
	protected double escapeAngleCounterClockwise;
	protected RMovementPath path1;
	protected RMovementPath path2;
	protected double smallestAbsB;
	protected double largestAbsB;
	protected RPoint smallestIntersection;
	protected RPoint largestIntersection;
	
	public RWave(RRobot creator, RRobot target, long startTime, double power, Color color) {
		this(creator, target, startTime, power, color, false);
	}
	
	public RWave (RRobot creator, RRobot target, long startTime, double power, Color color, boolean isVirtual) {
	    this.creator = creator;
		this.initialCreatorState = creator.getCurrentState().getCopy();
		this.origin = initialCreatorState.location.getCopy();
		this.target = target;
		this.initialTargetState = target.getTargetableState().getCopy();
		this.startTime = startTime;
		this.power = power;
		this.velocity = RUtil.computeBulletVelocity(power);
		this.distanceTraveled = velocity;
		this.isVirtual = isVirtual;
		this.path1 = new RMovementPath();
		this.path2 = new RMovementPath();
		this.escapeAngleClockwise = RUtil.computePreciseMaxEscapeAngle(RampantRobot.getGlobalBattlefield(), initialCreatorState, initialTargetState, velocity, 1 * initialTargetState.directionTraveling, path1);
		this.escapeAngleCounterClockwise = RUtil.computePreciseMaxEscapeAngle(RampantRobot.getGlobalBattlefield(), initialCreatorState, initialTargetState, velocity, -1 * initialTargetState.directionTraveling, path2);
		this.largestAbsB = Double.NEGATIVE_INFINITY;
		this.smallestAbsB = Double.POSITIVE_INFINITY;
		this.color = color;
	}
	
	public boolean didBreak() {
		// TODO: make this more accurate
		return (distanceTraveled > target.getCurrentState().location.distance(origin) + 50);
	}
	
	public double distanceTo(RPoint point) {
		return origin.distance(point) - getDistanceTraveled();
	}
	
	public double distanceTo(RPoint point, long timeOffset) {
		return origin.distance(point) - getDistanceTraveled() + getVelocity() * timeOffset;
	}
	
	public void draw(Graphics2D g) {
		g.setColor(WAVE_COLOR);
		if (!isVirtual) {
			RUtil.drawOval(origin, (int)(distanceTraveled - velocity), g);
			RUtil.drawOval(origin, (int)distanceTraveled, g);
			if (smallestIntersection != null) {
				RUtil.drawLine(origin, origin.projectTo(smallestAbsB, distanceTraveled), g);
				RPoint midpoint = origin.projectTo(smallestAbsB, distanceTraveled / 2);
				g.drawString("" + RUtil.roundToPrecision(getGuessFactorForSmallest(), 2), (int) midpoint.x, (int) midpoint.y);
			}
			if (largestIntersection != null) {
				RUtil.drawLine(origin, origin.projectTo(largestAbsB, distanceTraveled), g);
				RPoint midpoint = origin.projectTo(largestAbsB, distanceTraveled / 2);
				g.drawString("" + RUtil.roundToPrecision(getGuessFactorForLargest(), 2), (int) midpoint.x, (int) midpoint.y);
			}
			int direction = initialTargetState.directionTraveling;
			g.setColor(Color.red);
			RUtil.drawLine(origin, origin.projectTo(escapeAngleClockwise * direction + initialTargetState.absoluteBearing, distanceTraveled), g);
			g.setColor(Color.yellow);
			RUtil.drawLine(origin, origin.projectTo(-escapeAngleCounterClockwise *direction + initialTargetState.absoluteBearing, distanceTraveled), g);
		}
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
	
	public Color getColor() {
		return color;
	}
	
	public double getDistanceTraveled() {
		return distanceTraveled;
	}
	
	public double getGuessFactor(double desiredAbsB) {
		double angleOffset = Utils.normalRelativeAngle(desiredAbsB - initialTargetState.absoluteBearing);
		double escapeAngle = escapeAngleClockwise;
		if (initialTargetState.directionTraveling < 0) {
			escapeAngle = escapeAngleCounterClockwise;
		}
		if (angleOffset > escapeAngle) {
			escapeAngle = RUtil.computeMaxEscapeAngle(velocity);
		}
		return Math.max(-1, Math.min(1, angleOffset / Math.abs(escapeAngle))) * initialTargetState.directionTraveling;
	}
	
	public double getGuessFactorForLargest() {
		return getGuessFactor(largestAbsB);
	}
	
	public double getGuessFactorForSmallest() {
		return getGuessFactor(smallestAbsB);
	}
	
	public RRobotState getInitialCreatorState() {
	    return initialCreatorState;
	}
	
	public RRobotState getInitialTargetState() {
		return initialTargetState;
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

	public boolean isVirtual() {
		return isVirtual;
	}
	
	public long timeToImpact(RPoint point) {
		double remainingDistance = distanceTo(point) - getDistanceTraveled();
		return (long) (remainingDistance / getVelocity());
	}

	public void update(long time) {
		distanceTraveled = (time - startTime) * getVelocity();
		List<RPoint> intersections = computeIntersections(target.getCurrentState().location);
		if (!intersections.isEmpty()) {
			// compute widest intersection
			for (RPoint point : intersections) {
				double absB = origin.computeAbsoluteBearingTo(point);
				if (absB < smallestAbsB) {
					smallestAbsB = absB;
					smallestIntersection = point;
				}
				if (absB > largestAbsB) {
					largestAbsB = absB;
					largestIntersection = point;
				}
			}
		}
	}
	
	protected List<RPoint> computeIntersections(RPoint botLocation) {
		ArrayList<RPoint> intersections = new ArrayList<RPoint>();
		double innerRadius = distanceTraveled - velocity;
		double outerRadius = distanceTraveled;
		double topY = botLocation.y + REnemyRobot.BOT_RADIUS;
		double botY = botLocation.y - REnemyRobot.BOT_RADIUS;
		double leftX = botLocation.x - REnemyRobot.BOT_RADIUS;
		double rightX = botLocation.x + REnemyRobot.BOT_RADIUS;

		evaluateXCandidates(leftX, topY, botY, innerRadius, outerRadius, intersections);
		evaluateXCandidates(rightX, topY, botY, innerRadius, outerRadius, intersections);
		evaluateYCandidates(topY, leftX, rightX, innerRadius, outerRadius, intersections);
		evaluateYCandidates(botY, leftX, rightX, innerRadius, outerRadius, intersections);
		return intersections;
	}
	
	// TODO: account for corner points
	// TODO: refactor the next two methods
	protected void evaluateXCandidates(double x, double topY, double botY, double innerRadius, double outerRadius, List<RPoint> intersections) {
		double diff = Math.abs(x - origin.x);
		if (diff > outerRadius) {
			return;
		}
		
		double outerOffset = Math.sqrt(outerRadius * outerRadius - diff * diff);
		double innerOffset = Math.sqrt(innerRadius * innerRadius - diff * diff);
		
		double candidate = origin.y + outerOffset;
		if (candidate <= topY && candidate >= botY) {
			intersections.add(new RPoint(x, candidate));
		}
		candidate = origin.y - outerOffset;
		if (outerOffset != 0 && candidate <= topY && candidate >= botY) {
			intersections.add(new RPoint(x, candidate));
		}
		
		candidate = origin.y + innerOffset;
		if (candidate <= topY && candidate >= botY) {
			intersections.add(new RPoint(x, candidate));
		}
		candidate = origin.y - innerOffset;
		if (innerOffset != 0 && candidate <= topY && candidate >= botY) {
			intersections.add(new RPoint(x, candidate));
		}
	}

	protected void evaluateYCandidates(double y, double leftX, double rightX, double innerRadius, double outerRadius, List<RPoint> intersections) {
		double diff = Math.abs(y - origin.y);
		if (diff > outerRadius) {
			return;
		}
		
		double outerOffset = Math.sqrt(outerRadius * outerRadius - diff * diff);
		double innerOffset = Math.sqrt(innerRadius * innerRadius - diff * diff);
		
		double candidate = origin.x + outerOffset;
		if (candidate <= rightX && candidate >= leftX) {
			intersections.add(new RPoint(candidate, y));
		}
		candidate = origin.x - outerOffset;
		if (candidate <= rightX && candidate >= leftX) {
			intersections.add(new RPoint(candidate, y));
		}
		
		candidate = origin.x + innerOffset;
		if (candidate <= rightX && candidate >= leftX) {
			intersections.add(new RPoint(candidate, y));
		}
		candidate = origin.x - innerOffset;
		if (candidate <= rightX && candidate >= leftX) {
			intersections.add(new RPoint(candidate, y));
		}
	}
}
