package rampancy;

import java.util.LinkedList;

import rampancy.util.RBattlefield;
import rampancy.util.RPoint;
import rampancy.util.RRobotState;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public abstract class RampantRobot extends AdvancedRobot {
	
	public static RBattlefield globalBattlefield;
	
	protected RPoint location;
	protected LinkedList<RRobotState> stateHistory;

	public RampantRobot() {
		super();
		stateHistory = new LinkedList<RRobotState>();
		location = null;
	}
	
	public void run() {
		super.run();
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);
		globalBattlefield = new RBattlefield((int) getBattleFieldWidth(), (int) getBattleFieldHeight());
		setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
		location = new RPoint(getX(), getY());
		while (true) {
			scan();
		}
	}
	
	public void onScannedRobot(ScannedRobotEvent e) {
		doRadar(e);
	}
	
	public void doRadar(ScannedRobotEvent e) {
		double factor = 2.0;
		double radarTurn = getHeadingRadians() + e.getBearingRadians() - getRadarHeadingRadians();
		setTurnRadarRightRadians(factor * Utils.normalRelativeAngle(radarTurn));
	}
	
	public RRobotState getCurrentState() {
		return null; // TDOO
	}
	
	public RPoint getLocation() {
		return location;
	}
}
