package rampancy.util.gun;

import java.util.ArrayList;
import java.util.List;

import rampancy.RampantRobot;
import rampancy.util.REnemyRobot;
import rampancy.util.wave.RBulletWave;

public class RGunManager {
	
	protected ArrayList<RGun> guns;
	
	public RGunManager() {
		guns = new ArrayList<RGun>();
	}
	
	public void add(RGun gun) {
		guns.add(gun);
	}
	
	public List<RFiringSolution> getFiringSolutions(RampantRobot reference, REnemyRobot enemy) {
		ArrayList<RFiringSolution> firingSolutions = new ArrayList<RFiringSolution>();
		for (RGun gun : guns) {
			RFiringSolution solution = gun.getFiringSolution(reference, enemy);
			if (solution != null) {
				firingSolutions.add(solution);
			}
		}
		return firingSolutions;
	}
	
	public void updateGuns(RampantRobot reference, RBulletWave wave) {
		for (RGun gun : guns) {
			gun.update(reference, wave);
		}
	}
	
	public void updateEndOfRound(RampantRobot reference) {
		for (RGun gun : guns) {
			gun.updateEndOfRound(reference);
		}
	}
	
	public String toString() {
		String str = "Gun stats:\n";
		str += "************\n";
		for (RGun gun : guns) {
		    if (gun.displayStats()) {
    			str += gun.toString() + "\n";
		    }
		}
		return str;
	}
}
