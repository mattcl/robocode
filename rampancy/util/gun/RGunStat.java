package rampancy.util.gun;

import rampancy.util.RUtil;

public class RGunStat {

	int shotsFired;
	int shotsHit;
	
	public void noteShotFired() {
		shotsFired++;
	}
	
	public void noteShotHit() {
		shotsHit++;
	}
	
	public double getHitPercentage() {
		if (shotsFired == 0) {
			return 0.0;
		}
		return RUtil.roundToPrecision((double) shotsHit / (double) shotsFired * 100, 2);
	}
}
