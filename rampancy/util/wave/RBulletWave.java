package rampancy.util.wave;

import java.awt.Color;
import java.awt.Graphics2D;

import rampancy.util.RPoint;
import rampancy.util.gun.RFiringSolution;

public class RBulletWave extends RWave {
	
	protected RFiringSolution firingSolution;

	public RBulletWave(RPoint origin, RFiringSolution firingSolution, long startTime, Color color) {
		this(origin, firingSolution, startTime, color, false);
	}

	public RBulletWave(RPoint origin, RFiringSolution firingSolution, long startTime, Color color, boolean isVirtual) {
		super(origin, startTime, firingSolution.power, color, isVirtual);
		this.firingSolution = firingSolution;
	}

	@Override
	public void draw(Graphics2D g) {
		firingSolution.draw(g);
	}
}
