package rampancy.util.wave;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rampancy.RampantRobot;
import rampancy.util.RDrawable;
import rampancy.util.RPoint;
import rampancy.util.RUtil;
import robocode.Bullet;

public class RWaveManager implements RDrawable {
	protected ArrayList<REnemyWave> enemyWaves;
	protected ArrayList<RBulletWave> bulletWaves;
	protected boolean drawBulletWaves;
	protected boolean drawEnemyWaves;

	public RWaveManager() {
		reset();
		drawBulletWaves = false;
		drawEnemyWaves = true;
	}
	
	public void reset() {
		this.enemyWaves = new ArrayList<REnemyWave>();
		this.bulletWaves = new ArrayList<RBulletWave>();
	}
	
	public void add(RWave wave) {
		if (wave instanceof RBulletWave) {
			bulletWaves.add((RBulletWave) wave);
		} else if (wave instanceof REnemyWave) {
			enemyWaves.add((REnemyWave) wave);
		}
	}
	
	public void update(RampantRobot reference) {
		long time = reference.getTime();
		for (int i = bulletWaves.size() - 1; i >= 0; i--) {
			RBulletWave wave = bulletWaves.get(i);
			wave.update(time);
			if (wave.didBreak()) {
				if (!wave.isVirtual()) {
					RampantRobot.getGunManager().updateGuns(reference, wave);
				}
				bulletWaves.remove(i);
			}
		}
		
		for (int i = enemyWaves.size() - 1; i >= 0; i--) {
			REnemyWave wave = enemyWaves.get(i);
			wave.update(time);
			if (wave.didBreak()) {
				if (!wave.isVirtual()) {
					
				}
				enemyWaves.remove(i);
			}
		}
	}
	
	// TODO: maybe weight this on power
	public REnemyWave getMostDangerousWave(RampantRobot reference) {
		double bestTime = Double.POSITIVE_INFINITY;
		REnemyWave bestWave = null;
		for (REnemyWave wave : enemyWaves) {
			double time = wave.timeToImpact(reference.getCurrentState().location);
			if (time < bestTime) {
				bestTime = time;
				bestWave = wave;
			}
		}
		return bestWave;
	}
	
	public List<REnemyWave> getMostDangerousWaves(RampantRobot reference) {
		final RPoint location = reference.getCurrentState().location;
		ArrayList<REnemyWave> workingWaves = new ArrayList<REnemyWave>(enemyWaves);
		Collections.sort(workingWaves, new Comparator<REnemyWave>() {
			public int compare(REnemyWave wave1, REnemyWave wave2) {
				return RUtil.sign(wave1.timeToImpact(location) - wave2.timeToImpact(location));
			}
		});
		
		if (workingWaves.isEmpty()) {
			return null;
		}
		
		if (workingWaves.size() < 2) {
			return workingWaves.subList(0, 1);
		}
		
		return workingWaves.subList(0, 2);
	}
	
	public REnemyWave getWaveForBullet(Bullet bullet) {
	    RPoint hitLocation = new RPoint(bullet.getX(), bullet.getY());
	    double bestDistance = Double.POSITIVE_INFINITY;
	    REnemyWave bestWave = null;
	    for (REnemyWave wave : enemyWaves) {
	        double travelDist = wave.distanceTo(hitLocation);
	        double velocity = wave.getVelocity();
	        double dist = Math.abs(travelDist - wave.getDistanceTraveled()) + Math.abs(velocity - bullet.getVelocity());
	        if (dist < bestDistance) {
	            bestDistance = dist;
	            bestWave = wave;
	        }
	    }
	    return bestWave;
	}
	
	public void toggleBulletWaves() {
		drawBulletWaves = !drawBulletWaves;
	}
	
	public void toggleEnemyWaves() {
		drawEnemyWaves = !drawEnemyWaves;
	}

	@Override
	public void draw(Graphics2D g) {
		if (drawBulletWaves) {
			for (RBulletWave wave : bulletWaves) {
				wave.draw(g);
			}
		}
	
		if (drawEnemyWaves) {
			for (REnemyWave wave : enemyWaves) {
				wave.draw(g);
			}
		}
	}
}
