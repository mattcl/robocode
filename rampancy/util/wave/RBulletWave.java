package rampancy.util.wave;

import java.awt.Color;
import java.awt.Graphics2D;

import rampancy.util.RPoint;
import rampancy.util.RRobot;
import rampancy.util.RUtil;
import rampancy.util.gun.RFiringSolution;

public class RBulletWave extends RWave {
	

	protected RFiringSolution firingSolution;
	protected RPoint bulletLocation;
	protected boolean didHit;
	protected double[] factorDangers;

	public RBulletWave(RRobot creator, RFiringSolution firingSolution, long startTime, Color color) {
		this(creator, firingSolution, startTime, color, false);
	}

	public RBulletWave(RRobot creator, RFiringSolution firingSolution, long startTime, Color color, boolean isVirtual) {
		super(creator, firingSolution.target, startTime, firingSolution.power, color, isVirtual);
		this.firingSolution = firingSolution;
		this.bulletLocation = this.origin.getCopy();
		this.firingSolution.gun.noteShotFired();
		this.factorDangers = null;
	}
	
	public RFiringSolution getFiringSolution() {
		return firingSolution;
	}
	
	public void update(long time) {
		super.update(time);
		bulletLocation = RUtil.project(origin, firingSolution.firingAngle, distanceTraveled);
		if (!didHit && RUtil.pointOnRobot(bulletLocation, target)) {
			didHit = true;
			firingSolution.gun.noteShotHit();
		}
	}
	
	public void updateDanger(double[] dangers) {
	   this.factorDangers = dangers;
	}
	
	public boolean didHitEnemy() {
		return didHit;
	}

	@Override
	public void draw(Graphics2D g) {
	    super.draw(g);
		g.setColor(WAVE_COLOR);
		if (!isVirtual) {
			firingSolution.draw(g);
		}
		RUtil.drawOval(bulletLocation, 5, g);
	}
}
