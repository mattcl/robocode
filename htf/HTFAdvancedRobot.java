package htf;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public abstract class HTFAdvancedRobot extends AdvancedRobot {
	
	double lastHeading;
	double lastVelocity;

	public void run() {
		super.run();
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);
	}
	
	public void onScannedRobot(ScannedRobotEvent e) {
		double absoluteBearing = Utils.normalAbsoluteAngleDegrees(e.getBearing() + getHeading());
		double gunOffset = Utils.normalRelativeAngleDegrees(absoluteBearing - getHeading());
		
	}
}
