package htf;

/*
 * imports in Java bring in external libraries that we might need.
 */
import java.awt.Color;

import robocode.Robot;
import robocode.ScannedRobotEvent;

public class VariablePowerBot extends Robot {

	public void run() {
		setColors();
		// This is the main "loop." everything in the loop will be run over and
		// over again. Here, we are just moving in a "square"
		while (true) {
			turnLeft(90);
			turnGunRight(360);
			ahead(100);
		}
	}
	
	public void setColors() {
		/* available colors: 
		 * black, blue, cyan, darkGrey, gray, green, lightGray,
		 * magenta, orange, pink, red, white, yellow 
		 */
		this.setColors(
				Color.blue, // body color
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
	    stop();
	    /*
	     * use more powerful bullets if the enemy is closer
	     */
	    if (e.getDistance() < 100) {
    		fire(3.0);
	    } else if (e.getDistance() < 300) {
	        fire(1.5);
	    } else {
    		fire(0.5);
	    }
	}
}