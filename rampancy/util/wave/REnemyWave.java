package rampancy.util.wave;

import java.awt.Color;
import java.awt.Graphics2D;

import rampancy.util.REnemyRobot;
import rampancy.util.RRobot;

public class REnemyWave extends RWave {
	
	protected REnemyRobot enemy;
	
	public REnemyWave(REnemyRobot enemy, RRobot target, long startTime) {
		this(enemy, target, startTime, WAVE_COLOR);
	}

	public REnemyWave(REnemyRobot enemy, RRobot target, long startTime, Color color) {
		this(enemy, target, startTime, color, false);
	}

	public REnemyWave(REnemyRobot enemy, RRobot target, long startTime, Color color, boolean isVirtual) {
		super(enemy, target, startTime, enemy.getShotPower(), color, isVirtual);
		this.enemy = enemy;
	}
	
	public void draw(Graphics2D g) {
		super.draw(g);
	}
}
