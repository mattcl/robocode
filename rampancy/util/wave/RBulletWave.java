package rampancy.util.wave;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import rampancy.RampantRobot;
import rampancy.util.REnemyRobot;
import rampancy.util.RMovementPath;
import rampancy.util.RPoint;
import rampancy.util.RRobotState;
import rampancy.util.RUtil;
import rampancy.util.gun.RFiringSolution;
import robocode.util.Utils;

public class RBulletWave extends RWave {
	
	public static final Color WAVE_COLOR = new Color(0x3B3B3B);
	
	protected RFiringSolution firingSolution;
	protected REnemyRobot target;
	protected RRobotState creatorState;
	protected RRobotState initialState;
	protected RPoint bulletLocation;
	protected double smallestAbsB;
	protected double largestAbsB;
	protected RPoint smallestIntersection;
	protected RPoint largestIntersection;
	protected double absoluteFiringAngle;
	protected double maxEscapeAngleForward;
	protected double maxEscapeAngleBackward;
	protected RMovementPath path1;
	protected RMovementPath path2;
	protected boolean didHit;

	public RBulletWave(RampantRobot reference, RFiringSolution firingSolution, long startTime, Color color) {
		this(reference, firingSolution, startTime, color, false);
	}

	public RBulletWave(RampantRobot reference, RFiringSolution firingSolution, long startTime, Color color, boolean isVirtual) {
		super(reference.getLocation(), startTime, firingSolution.power, color, isVirtual);
		this.firingSolution = firingSolution;
		this.target = firingSolution.target;
		this.creatorState = reference.getCurrentState().getCopy();
		this.initialState = target.getCurrentState().getCopy();
		this.absoluteFiringAngle = firingSolution.firingAngle;
		this.bulletLocation = this.origin.getCopy();
		this.largestAbsB = Double.NEGATIVE_INFINITY;
		this.smallestAbsB = Double.POSITIVE_INFINITY;
		this.firingSolution.gun.noteShotFired();
		path1 = new RMovementPath();
		path2 = new RMovementPath();
		double escapeAngle1 = RUtil.computePreciseMaxEscapeAngle(RampantRobot.getGlobalBattlefield(), creatorState, initialState, velocity, 1, path1);
		double escapeAngle2 = RUtil.computePreciseMaxEscapeAngle(RampantRobot.getGlobalBattlefield(), creatorState, initialState, velocity, -1, path2);
		this.maxEscapeAngleForward = escapeAngle1;
		this.maxEscapeAngleBackward = escapeAngle2;
		if (initialState.directionTraveling < 0) {
			this.maxEscapeAngleForward = escapeAngle2;
			this.maxEscapeAngleBackward = escapeAngle1;
		}
	}
	
	public RFiringSolution getFiringSolution() {
		return firingSolution;
	}
	
	public RRobotState getInitialState() {
		return initialState;
	}
	
	public double getGuessFactorForLargest() {
		return getGuessFactor(largestAbsB);
	}
	
	public double getGuessFactorForSmallest() {
		return getGuessFactor(smallestAbsB);
	}
	
	protected double getGuessFactor(double desiredAbsB) {
		double angleOffset = Utils.normalRelativeAngle(desiredAbsB - initialState.absoluteBearing);
		double escapeAngle = maxEscapeAngleForward;
		if (initialState.directionTraveling < 0) {
			escapeAngle = maxEscapeAngleBackward;
		}
		if (angleOffset > escapeAngle) {
			escapeAngle = RUtil.computeMaxEscapeAngle(velocity);
		}
		return Math.max(-1, Math.min(1, angleOffset / Math.abs(escapeAngle))) * initialState.directionTraveling;
	}
	
	public void update(long time) {
		super.update(time);
		bulletLocation = RUtil.project(origin, absoluteFiringAngle, distanceTraveled);
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
		if (!didHit && RUtil.pointOnRobot(bulletLocation, target)) {
			didHit = true;
			firingSolution.gun.noteShotHit();
		}
	}
	
	public boolean didBreak() {
		// TODO: make this more accurate
		return (distanceTraveled > target.getCurrentState().location.distance(origin) + 50);
	}
	
	public boolean didHitEnemy() {
		return didHit;
	}

	@Override
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
			g.setColor(Color.red);
			RUtil.drawLine(origin, origin.projectTo(maxEscapeAngleForward + initialState.absoluteBearing, distanceTraveled), g);
			g.setColor(Color.yellow);
			RUtil.drawLine(origin, origin.projectTo(maxEscapeAngleBackward + initialState.absoluteBearing, distanceTraveled), g);
			firingSolution.draw(g);
			path1.draw(g);
			path2.draw(g);
		}
		g.setColor(WAVE_COLOR);
		RUtil.drawOval(bulletLocation, 5, g);
	}
}
