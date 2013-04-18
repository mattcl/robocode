package rampancy;

import java.awt.Color;

import rampancy.util.gun.RCircularTargetingGun;
import rampancy.util.gun.RDynamicClusteringGun;
import rampancy.util.gun.RGunManager;

public class Durandal extends RampantRobot {
	
	public Durandal() {
		super();
	}
	
	public void run() {
		super.run();
	}

    @Override
    public void initialSetup() {
		setColors(Color.black, new Color(0x0D5E10), new Color(0x0D5E10), Color.white, Color.blue);
    }

	@Override
	protected void initGunManager(RGunManager gunManager) {
    	gunManager.add(new RCircularTargetingGun());
    	gunManager.add(new RDynamicClusteringGun());
	}
}
