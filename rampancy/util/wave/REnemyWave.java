package rampancy.util.wave;

import java.awt.Color;
import java.awt.Graphics2D;

import rampancy.RampantRobot;
import rampancy.util.RPoint;

public class REnemyWave extends RWave {
	
	public static final Color DEFAULT_COLOR = Color.red;

	public REnemyWave(RampantRobot reference, RPoint origin, long startTime, double power) {
		super(origin, startTime, power, DEFAULT_COLOR, false);
	}

	public REnemyWave(RPoint origin, long startTime, double power, Color color, boolean isVirtual) {
		super(origin, startTime, power, color, isVirtual);
	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub

	}

}
