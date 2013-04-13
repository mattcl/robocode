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
		return name + " real " + stat.getRealHitPercentage() + "%, virtual " + stat.getVirtualHitPercentage() + "%";
	}
	
	public String summary() {
		return toString();
	}
	
	public void noteShotFired(boolean isVirtual) {
		stat.noteShotFired(isVirtual);
	}
	
	public void noteShotHit(boolean isVirtual) {
		stat.noteShotHit(isVirtual);
	}
	
	public void update(RampantRobot reference, RBulletWave wave) {
		// do nothing
	}
	
	public void updateEndOfRound(RampantRobot reference) {
		// do nothing
	}
	
	abstract public RFiringSolution getFiringSolution(RampantRobot reference, REnemyRobot enemy);
}
