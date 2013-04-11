package rampancy.util.gun;

import java.util.ArrayList;
import java.util.List;

import rampancy.RampantRobot;
import rampancy.util.REnemyRobot;

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
	
	public String toString() {
		String str = "Gun stats:\n";
		str += "************\n";
		for (RGun gun : guns) {
			str += gun.toString() + "\n";
		}
		return str;
	}
}
