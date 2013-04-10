package rampancy.util.gun;

import rampancy.RampantRobot;
import rampancy.util.REnemyRobot;

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
	
	abstract public RFiringSolution getFiringSolution(RampantRobot reference, REnemyRobot enemy);
}