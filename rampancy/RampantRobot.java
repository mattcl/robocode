package rampancy;

import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.LinkedList;

import rampancy.util.RBattlefield;
import rampancy.util.REnemyManager;
import rampancy.util.REnemyRobot;
import rampancy.util.RPoint;
import rampancy.util.RRobotState;
import rampancy.util.gun.RCircularTargetingGun;
import rampancy.util.gun.RFiringSolution;
import rampancy.util.gun.RGun;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public abstract class RampantRobot extends AdvancedRobot {
	
	public static RBattlefield globalBattlefield;
	public static REnemyManager enemyManager;
	
	public static RBattlefield getGlobalBattlefield() {
		return globalBattlefield;
	}
	
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
	
		if (enemyManager == null) {
			enemyManager = new REnemyManager();
		}
		
		setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
		location = new RPoint(getX(), getY());
		while (true) {
			scan();
		}
	}
	
	public void onScannedRobot(ScannedRobotEvent e) {
		doRadar(e);
		String name = e.getName();
		if (!enemyManager.contains(name)) {
			enemyManager.add(name);
		}
		REnemyRobot enemy = enemyManager.get(name);
		enemy.update(this, globalBattlefield, e);
		
		RGun gun = new RCircularTargetingGun();
		RFiringSolution firingSolution = gun.getFiringSolution(this, enemy);
		if (firingSolution != null) {
			setTurnGunRightRadians(Utils.normalRelativeAngle(firingSolution.firingAngle - getGunHeadingRadians()));
			setFireBullet(firingSolution.power);
		}
	}
	
	public void doRadar(ScannedRobotEvent e) {
		double factor = 2.0;
		double radarTurn = getHeadingRadians() + e.getBearingRadians() - getRadarHeadingRadians();
		setTurnRadarRightRadians(factor * Utils.normalRelativeAngle(radarTurn));
	}
	
	public void onPaint(Graphics2D g) {
		globalBattlefield.draw(g);
		
		Iterator<REnemyRobot> iter = enemyManager.iterator();
		while (iter.hasNext()) {
			iter.next().draw(g);
		}
	}
	
	public RRobotState getCurrentState() {
		return null; // TDOO
	}
	
	public RPoint getLocation() {
		return location;
	}
}
