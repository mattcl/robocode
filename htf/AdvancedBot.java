package htf;

import robocode.ScannedRobotEvent;
import robocode.TurnCompleteCondition;
import robocode.util.Utils;


public class AdvancedBot extends HTFAdvancedRobot {
	
	double lastHeading;
	double lastTime;

	public void run() {
		super.run();
	
		// setup instance variables
		lastHeading = 0;
		lastTime = 0;
		
		while (true) {
			setTurnRadarRight(360);
			setAhead(10000);
			setTurnRight(10);
			waitFor(new TurnCompleteCondition(this));
		}
	}
	
	public void onScannedRobot(ScannedRobotEvent e) {
		double absoluteBearing = Utils.normalAbsoluteAngleDegrees(e.getBearing() + getHeading());
		double enemyX = HTFUtil.projectX(getX(), absoluteBearing, e.getDistance());
		double enemyY = HTFUtil.projectY(getY(), absoluteBearing, e.getDistance());
		
		double changeInHeading = (e.getHeading() - lastHeading) / (getTime() - lastTime); 
		lastHeading = e.getHeading();
		lastTime = getTime();
		
		double bulletPower = 1.5;
		double bulletVelocity = HTFUtil.getBulletVelocityFromPower(bulletPower);

		double expectedHeading = e.getHeading();
		for (int i = 0; i < 100; i++) {
			double bulletDistanceTraveled = i * bulletVelocity;
			expectedHeading += changeInHeading;
			enemyX = HTFUtil.projectX(enemyX, expectedHeading, e.getVelocity());
			enemyY = HTFUtil.projectY(enemyY, expectedHeading, e.getVelocity());
			
			double enemyDistance = HTFUtil.distanceTo(getX(), getY(), enemyX, enemyY);
			if (enemyDistance < bulletDistanceTraveled) {
				double bearingToNewLocation = HTFUtil.computeAbsoluteBearing(getX(), getY(), enemyX, enemyY);
				double gunOffset = Utils.normalRelativeAngleDegrees(bearingToNewLocation - getGunHeading());
				setTurnGunRight(gunOffset);
				setFire(bulletPower);
				break;
			}
		}
	}
}
