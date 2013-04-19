package rampancy.util.wave;

import java.awt.Color;
import java.awt.Graphics2D;

import rampancy.util.REnemyRobot;
import rampancy.util.RPoint;
import rampancy.util.RRobot;

public class REnemyWave extends RWave {
	
	protected REnemyRobot enemy;
	protected RPoint hitLocation;
	protected double hitGuessFactor;
	
	public REnemyWave(REnemyRobot enemy, RRobot target, long startTime) {
		this(enemy, target, startTime, WAVE_COLOR);
	}

	public REnemyWave(REnemyRobot enemy, RRobot target, long startTime, Color color) {
		this(enemy, target, startTime, color, false);
	}

	public REnemyWave(REnemyRobot enemy, RRobot target, long startTime, Color color, boolean isVirtual) {
		super(enemy, target, startTime, enemy.getShotPower(), color, isVirtual);
		this.enemy = enemy;
		this.hitLocation = null;
		this.hitGuessFactor = 0;
	}
	
	public void setHitLocation(RPoint hitLocation) {
	    this.hitLocation = hitLocation;
	    double absB = origin.computeAbsoluteBearingTo(hitLocation);
	    hitGuessFactor = getGuessFactor(absB);
	}
	
	public RPoint getHitLocation() {
	   return hitLocation; 
	}
	
	public double getHitGuessFactor() {
	   return hitGuessFactor; 
	}
	
	public void draw(Graphics2D g) {
		super.draw(g);
	}

}
