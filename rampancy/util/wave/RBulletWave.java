package rampancy.util.wave;

import java.awt.Color;
import java.awt.Graphics2D;

import rampancy.util.RPoint;

public class RBulletWave extends RWave {

	public RBulletWave(RPoint origin, long startTime, double power, Color color) {
		super(origin, startTime, power, color);
	}

	public RBulletWave(RPoint origin, long startTime, double power, Color color, boolean isVirtual) {
		super(origin, startTime, power, color, isVirtual);
	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
	}
}
