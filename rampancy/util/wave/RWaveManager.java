package rampancy.util.wave;

import java.awt.Graphics2D;
import java.util.ArrayList;

import rampancy.RampantRobot;
import rampancy.util.RDrawable;

public class RWaveManager implements RDrawable {
	protected ArrayList<RWave> enemyWaves;
	protected ArrayList<RBulletWave> bulletWaves;

	public RWaveManager() {
		this.enemyWaves = new ArrayList<RWave>();
		this.bulletWaves = new ArrayList<RBulletWave>();
	}
	
	public void add(RWave wave) {
		if (wave instanceof RBulletWave) {
			bulletWaves.add((RBulletWave) wave);
		} else {
			enemyWaves.add(wave);
		}
	}
	
	public void update(RampantRobot reference) {
		long time = reference.getTime();
		for (int i = bulletWaves.size() - 1; i >= 0; i--) {
			RBulletWave wave = bulletWaves.get(i);
			wave.update(time);
			if (wave.didBreak()) {
				bulletWaves.remove(i);
			}
		}
	}

	@Override
	public void draw(Graphics2D g) {
		for (RBulletWave wave : bulletWaves) {
			wave.draw(g);
		}
	}
}
