package htf;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.TurnCompleteCondition;
import robocode.util.Utils;

public class ReactionBot extends AdvancedRobot {
	
	boolean requestedShot;
	long fireTime;
	double requestedPower;
	double lastHeading;
	double lastTime;

	public void run() {
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);
		
		lastHeading = 0;
		lastTime = 0;
	
		// whenever the radar turn is 0, start it moving again
		
		while (true) {
			shootGun();
			if (getRadarTurnRemaining() == 0) {
				setTurnRadarRight(Double.POSITIVE_INFINITY);
			}
			execute();
		}
	}
	
	public void onScannedRobot(ScannedRobotEvent event) {
		doRadar(event);
		doMovement(event);
		doGun(event);
	}
	
	public void doRadar(ScannedRobotEvent event) {
		// nothing;
	}
	
	public void doMovement(ScannedRobotEvent event) {
		setTurnRight(100);
		setAhead(100);
	}
	
	public void doGun(ScannedRobotEvent event) {
		double absoluteBearing = Utils.normalAbsoluteAngleDegrees(event.getBearing() + getHeading());
		shootWithAngleAndPower(absoluteBearing - getGunHeading(), 1.4);
	}
	
	public void shootWithAngleAndPower(double angleToTurn, double power) {
		angleToTurn = Utils.normalRelativeAngleDegrees(angleToTurn);
		setTurnGunRight(angleToTurn);
		if (!requestedShot) {
			requestedShot = true;
			requestedPower = power;
			fireTime = getTime() + 1;
		}
	}
	
	protected void shootGun() {
		if (requestedShot && getTime() >= fireTime && getGunTurnRemaining() == 0) {
			setFire(requestedPower);
			requestedShot = false;
		}
	}
}
