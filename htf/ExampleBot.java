package htf;

/*
 * imports in Java bring in external libraries that we might need.
 */
import java.awt.Color;

import robocode.HitByBulletEvent;
import robocode.HitWallEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class ExampleBot extends Robot {

	public void run() {
		// When we call super, we are calling the run() method of the parent
		// class. In this case, super.run() sets up some basic things that you
		// shouldn't need to worry about for your robot. If you're curious, you
		// can look at the code for HTFRobot
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);
		setColors();
		// This is the main "loop." everything in the loop will be run over and
		// over again. Here, we are just moving in a "circle"
		while (true) {
			turnLeft(20);
			ahead(100);
			
			// every time we have turned and moved, spin the radar all the way
			// around to try and find an enemy robot
			turnRadarRight(360);
		}
	}
	
	public void setColors() {
		/* available colors: 
		 * black, blue, cyan, darkGrey, gray, green, lightGray,
		 * magenta, orange, pink, red, white, yellow 
		 */
		this.setColors(
				Color.red, // body color
				Color.cyan, // gun color
				Color.blue, // radar color
				Color.green, // bullet color 
				Color.white // scan arc color
				);
	}

	/*
	 * This function is called whenever our radar scans another robot.
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		super.onScannedRobot(e);
		
		// If I were to draw a line from my robot north, and another line from
		// my robot to the scanned robot, then the "absolute bearing" would be
		// the angle (clockwise) from the line north to the line to the other
		// robot.
		// 
		// Because e.getBearing() returns the bearing to the scanned robot
		// relative to our current heading, we need to add our current heading
		// to get the absolute bearing
		double absoluteBearing = Utils.normalAbsoluteAngleDegrees(e.getBearing() + this.getHeading());
		
		// We want to know how far we need to turn our gun so that it's facing
		// the robot we've just scanned. We can compute this by subtracting our
		// current gun heading from the absolute bearing
		double gunOffset = Utils.normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
	
		// Turn the gun to face the scanned robot and fire. We could do
		// something much more complicated here (attempting to lead the enemy
		// robot for instance), but that's up to you!
		turnGunRight(gunOffset);
		
		// We can select to fire a bullet with a power greater than 0 and less
		// than or equal to 3. Here, we're justing using power 2, but you can
		// write code to decide on the best power for a given situation. More
		// power equals more damage but a slower bullet speed. Less power means
		// a faster bullet but less damage.
		fire(2);
	}

	/*
	 * Whenever you are hit by a bullet, this function is called. Here, we're
	 * using it to turn 90 degrees and move ahead 200. This will hopefully
	 * move us out of the way of another shot.
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		turnRight(90);
		ahead(200);
	}

	/*
	 * Whenever your robot runs into a wall, this event is triggered. Here, we
	 * use it to move backwards 100 and turn 90 degrees. This should hopefully
	 * prevent us from hitting a wall on our next set of moves.
	 */
	public void onHitWall(HitWallEvent e) {
		ahead(-100); // this moves the robot backwards 100
		// you could also call back(100);
		turnRight(90);
	}
}
