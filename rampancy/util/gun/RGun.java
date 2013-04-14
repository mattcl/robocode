package rampancy.util.gun;

import rampancy.RampantRobot;
import rampancy.util.REnemyRobot;
import rampancy.util.wave.RBulletWave;

public abstract class RGun {

	protected String name;
	protected RGunStat stat;
	
	public RGun(String name) {
		this.name = name;
		this.stat = new RGunStat();
	}

	public String toString() {
		return name + " score " + stat.getHitPercentage() + "%";
	}
	
	public String summary() {
		return toString();
	}
	
	public void noteShotFired() {
		stat.noteShotFired();
	}
	
	public void noteShotHit() {
		stat.noteShotHit();
	}
	
	public double getHitPercentage() {
		return stat.getHitPercentage();
	}
	
	public void update(RampantRobot reference, RBulletWave wave) {
		// do nothing
	}
	
	public void updateEndOfRound(RampantRobot reference) {
		// do nothing
	}
	
	abstract public RFiringSolution getFiringSolution(RampantRobot reference, REnemyRobot enemy);
}
