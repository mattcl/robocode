package htf;

import java.awt.Color;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.TurnCompleteCondition;
import robocode.util.Utils;

/*
 * While the normal Robot class can only do one thing at a time (moving,
 * turning, firing, etc.), the AdvancedRobot class can do multiple things at
 * once. This requires a different way of thinking.
 * 
 * You'll notice that instead of calling "turnRight," we call "setTurnRight."
 * Instead of calling "ahead," we call "setAhead," etc.. These functions tell
 * the robocode runner that, "during my turn, I want to do as much of these
 * things as possible."
 * 
 */
public class AdvancedBot extends AdvancedRobot {

	boolean requestedShot;
	long fireTime;
	double requestedPower;
	double lastHeading;
	double lastTime;

	public void run() {
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);
		setColors();
		// setup instance variables
		lastHeading = 0;
		lastTime = 0;
		
		while (true) {
			// you can ignore this line, it's a helper function to deal with the
			// "firing pitfall" described more below. Do not change this line!
			shootGun();
		
			// turn the radar all the way around. We can only turn our radar a
			// max of 45 degrees per turn
			setTurnRadarRight(360);
			
			// We can only really move 8 "units" per turn, so this will just
			// result in us moving 8 units
			setAhead(10000);
			
			// Turn rate is a factor of the speed that you're traveling, here
			// we attempt to turn 10 degrees every turn, but the actual maximum
			// angle that we can turn is a more complex calculation than you
			// need to care about
			setTurnRight(10);
			
			// we've told the game about the movements we want to make, so we
			// want to wait until our next turn to give more instructions
			// (otherwise the game can skip your turn if you're calling a "set"
			// function too many times
			waitFor(new TurnCompleteCondition(this));
		}
	}
	
	public void setColors() {
		/* available colors: 
		 * black, blue, cyan, darkGrey, gray, green, lightGray,
		 * magenta, orange, pink, red, white, yellow 
		 */
		this.setColors(
				Color.green, // body color
				Color.magenta, // gun color
				Color.blue, // radar color
				Color.white, // bullet color 
				Color.white // scan arc color
				);
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
		
		// We're just using a default bullet power here. Can you think of a 
		// better way to do this?
		double bulletPower = 1.5;

		// We want to know how much we need to turn our gun so that we're 
		// pointing at the enemy robot, so we calculate the difference between 
		// our current gun heading and the absolute bearing to the enemy robot
		double gunOffset = Utils.normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
		
		// We can only change our gun heading by 20 degrees per turn, but we're
		// not worrying about that for now (you may want to do something about
		// this, but it isn't that important for now).
		shootWithAngleAndPower(gunOffset, bulletPower);
	}
	
	/*
	 * This is a helper method for dealing with the "firing pitfall" All you
	 * really need to know is that you pass this method the angle you want
	 * the gun to turn and the power you want the bullet to have
	 */
    public void shootWithAngleAndPower(double angle, double power) {
        angle = Utils.normalRelativeAngleDegrees(angle);
        setTurnGunRight(angle);
        if (!requestedShot) {
	        requestedShot = true;
	        requestedPower = power;
	        fireTime = getTime() + 1;
        }

    }

	/*
	 * You don't really have to worry about this method. It's here to take care
	 * of the "firing pitfall" that plagues Advanced Robots. In robocode,
	 * bullets are fired before you move your gun, so you have to wait for the
	 * gun to be done turning. If you want, I can explain this more, but for now
	 * you can just ignore it.
	 */
	protected void shootGun() {
		if(requestedShot && getTime() >= fireTime && getGunTurnRemaining() == 0) {
            setFire(requestedPower);
            requestedShot = false;
        }
	}
}
