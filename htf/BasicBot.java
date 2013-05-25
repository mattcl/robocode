package htf;

/*
 * imports in Java bring in external libraries that we might need.
 */
import java.awt.Color;

import robocode.*;

public class BasicBot extends Robot {

	public void run() {
		setColors();
		// This is the main "loop." everything in the loop will be run over and
		// over again. Here, we are just moving in a "circle"
		while (true) {
			turnLeft(90);
			ahead(100);
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
		fire(1.5);
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
	 * use it to move backwards 150 and turn 90 degrees. This should hopefully
	 * prevent us from hitting a wall on our next set of moves.
	 */
	public void onHitWall(HitWallEvent e) {
		ahead(-150); // this moves the robot backwards 150
		// you could also call back(150);
		turnRight(90);
	}
}