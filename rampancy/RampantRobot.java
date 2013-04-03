package rampancy;

import java.util.LinkedList;
import robocode.util.*;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

import rampancy.util.*;

public abstract class RampantRobot extends AdvancedRobot {
	
	public static RBattlefield globalBattlefield;
	
	protected RPoint location;
	protected LinkedList<RRobotState> stateHistory;

	public RampantRobot() {
		super();
		stateHistory = new LinkedList<RRobotState>();
	}
	
	public void run() {
		super.run();
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);
		globalBattlefield = new RBattlefield((int) getBattleFieldWidth(), (int) getBattleFieldHeight());
		turnRadarRightRadians(Double.POSITIVE_INFINITY);
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
	
	
}
