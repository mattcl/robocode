package rampancy.util.gun;

public class RGunStat {

	int realShotsFired;
	int realShotsHit;
	int virtualShotsFired;
	int virtualShotsHit;
	
	public void noteShotFired(boolean isVirtual) {
		if (isVirtual) {
			virtualShotsFired++;
		} else {
			realShotsFired++;
		}
	}
	
	public void noteShotHit(boolean isVirtual) {
		if (isVirtual) {
			virtualShotsHit++;
		} else {
			realShotsHit++;
		}
	}
	
	public double getRealHitPercentage() {
		if (realShotsFired == 0) {
			return 0.0;
		}
		return ((double) realShotsHit / (double) realShotsFired) * 100;
	}
	
	public double getVirtualHitPercentage() {
		if (virtualShotsFired == 0) {
			return 0.0;
		}
		return ((double) virtualShotsHit / (double) virtualShotsFired) * 100;
	}
}
