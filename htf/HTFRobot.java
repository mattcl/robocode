package htf;

import robocode.Robot;
import robocode.ScannedRobotEvent;

public abstract class HTFRobot extends Robot {
	
	public void run() {
		super.run();
		// we want to make sure that the gun and radar
		// turn independently from the robot
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);
		setColors();
	}
	
	public void onScannedRobot(ScannedRobotEvent e) {
	}
	
	abstract public void setColors();
}
