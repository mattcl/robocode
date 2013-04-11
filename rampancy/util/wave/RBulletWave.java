package rampancy.util.wave;

import java.awt.Color;
import java.awt.Graphics2D;

import rampancy.RampantRobot;
import rampancy.util.REnemyRobot;
import rampancy.util.RPoint;
import rampancy.util.RRobotState;
import rampancy.util.RUtil;
import rampancy.util.gun.RFiringSolution;

public class RBulletWave extends RWave {
	
	public static final Color WAVE_COLOR = new Color(0x3B3B3B);
	
	protected RFiringSolution firingSolution;
	protected REnemyRobot target;
	protected RRobotState creatorState;
	protected RRobotState initialState;
	protected RPoint bulletLocation;
	protected double absoluteFiringAngle;
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
	}
	
	public void update(long time) {
		super.update(time);
		bulletLocation = RUtil.project(origin, absoluteFiringAngle, distanceTraveled);
		if (RUtil.pointOnRobot(bulletLocation, target)) {
			didHit = true;
		}
	}
	
	public boolean didBreak() {
		// TODO: make this more accurate
		return (distanceTraveled > target.getCurrentState().location.distance(origin) + 100);
	}
	
	public boolean didHitEnemy() {
		return didHit;
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(WAVE_COLOR);
		RUtil.drawOval(origin, (int)(distanceTraveled - velocity), g);
		RUtil.drawOval(origin, (int)distanceTraveled, g);
		firingSolution.draw(g);
		RUtil.drawOval(bulletLocation, 5, g);
	}
}
