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
		
		// We want to know the coordinates of the enemy robot we've just scanned
		// We can use the utility functions projectX and projectY to determine
		// the x and y coordinate of the enemy robot
		double enemyX = HTFUtil.projectX(getX(), absoluteBearing, e.getDistance());
		double enemyY = HTFUtil.projectY(getY(), absoluteBearing, e.getDistance());
	
		// We're interested in how much the enemy robot has changed it's heading
		// since the last time we scanned it.
		double changeInHeading = (e.getHeading() - lastHeading) / (getTime() - lastTime); 
		
		// Now that we've computed the change in heading, we need to store the
		// current heading and current time (planning ahead for the next time
		// we scan this robot
		lastHeading = e.getHeading();
		lastTime = getTime();
	
		// We're just using a default bullet power here. Can you think of a 
		// better way to do this?
		double bulletPower = 1.5;
		
		// Now that we've chosen the bullet power we want to use, we're 
		// interested in how fast this bullet will travel in the game world.
		double bulletVelocity = HTFUtil.getBulletVelocityFromPower(bulletPower);

		// we're storing this for convenience, since we will attempt to
		// "simulate" the enemy robot's movements. This will require us to 
		// store a simulated heading
		double heading = e.getHeading();
		for (int i = 0; i < 100; i++) {
			double bulletDistanceTraveled = i * bulletVelocity;
			heading += changeInHeading;
			enemyX = HTFUtil.projectX(enemyX, heading, e.getVelocity());
			enemyY = HTFUtil.projectY(enemyY, heading, e.getVelocity());
			
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
