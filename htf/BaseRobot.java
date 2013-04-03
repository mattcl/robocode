package htf;

import robocode.Robot;
import robocode.ScannedRobotEvent;

public class BaseRobot extends Robot {
	
	public void run() {
		// we want to make sure that the gun and radar
		// turn independently from the robot
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);
		while(true) {
			turnRadarRight(360);
			ahead(100);
			turnRight(20);
		}
	}
	
	public void onScannedRobot(ScannedRobotEvent e) {
		fire(1);
	}
}
