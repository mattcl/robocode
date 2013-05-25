package rampancy.util.wave;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import rampancy.util.REnemyRobot;
import rampancy.util.RPoint;
import rampancy.util.RRobot;
import rampancy.util.RUtil;
import robocode.util.Utils;

public class REnemyWave extends RWave {
	
	protected REnemyRobot enemy;
	protected RPoint hitLocation;
	protected double hitGuessFactor;
	protected ArrayList<RPoint> dangerMap;
	
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
		this.dangerMap = null;
	}
	
	public void setHitLocation(RPoint hitLocation) {
	    this.hitLocation = hitLocation;
	    double absB = origin.computeAbsoluteBearingTo(hitLocation);
	    hitGuessFactor = getGuessFactor(absB);
	}
	
	public REnemyRobot getEnemy() {
	    return enemy;
	}
	
	public RPoint getHitLocation() {
	   return hitLocation; 
	}
	
	public double getHitGuessFactor() {
	   return hitGuessFactor; 
	}
	
	public void draw(Graphics2D g) {
		super.draw(g);
		if (dangerMap != null) {
		    Color lastColor = g.getColor();
		    double max = Double.NEGATIVE_INFINITY;
		    double min = Double.POSITIVE_INFINITY;
		    for (RPoint point : dangerMap) {
		        if (point.y > max) {
		            max = point.y;
		        }
		        if (point.y < min) {
		            min = point.y;
		        }
		    }
		    for (RPoint point : dangerMap) {
		        RPoint location = origin.projectTo(Utils.normalAbsoluteAngle(point.x + initialTargetState.absoluteBearing), distanceTraveled);
		        float hue = (float)RUtil.scaleToRange(2.0 / 3.0, 1, min, max, point.y);
		        Color dangerColor = Color.getHSBColor(hue, 1f, 0.6f);
		        g.setColor(dangerColor);
		        RUtil.fillOval(location, 2, g);
		    }
		    g.setColor(lastColor);
		}
	}
	
	public void setDangerMap(ArrayList<RPoint> map) {
	    this.dangerMap = map;
	}
	
	public ArrayList<RPoint> getDangerMap() {
	    return dangerMap;
	}
	
	public boolean hasDangerMap() {
	    return dangerMap != null;
	}
}
