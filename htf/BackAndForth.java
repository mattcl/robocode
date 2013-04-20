package htf;

import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.Robot;
import robocode.util.Utils;

/*
 * This robot does no attacking. It just moves back and forth from EAST to WEST
 */
public class BackAndForth extends Robot {

	// Variables defined here are called "instance variables." They can be
	// "seen" by any method in this file
	double direction;
	
	public void run() {
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);
		// since we want this robot to move back and forth, we need some way
		// to change the direction we're moving in. 
		direction = 1;
		
		while (true) {
			// Turn so that we are facing EAST. We do this by computing the
			// difference between our current heading and 90 degrees (EAST).
			double angleToTurn = Utils.normalRelativeAngleDegrees(90 - getHeading());
			
			// Turn to face EAST
			turnRight(angleToTurn);
			
			// Move ahead by a large amount. We want this robot to move all the
			// way to the wall. Why do we multiply by direction? If direction
			// is equal to 1, then we will move forward. If direction is -1, we
			// will move backward
			ahead(direction * 10000);
		}
	}

	public void setColors() {
		// I like the default colors for this one, so this does nothing
	}
	
	public void onHitWall(HitWallEvent e) {
		// if we hit a wall, move in the opposite direction
		direction = -direction;
		ahead(direction * 10000);
	}
	
	public void onHitRobot(HitRobotEvent e) {
		// if we hit another robot, move in the opposite direction
		direction = -direction;
		ahead(this.direction * 10000);
	}
}