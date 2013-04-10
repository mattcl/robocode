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
import rampancy.util.wave.RBulletWave;
import rampancy.util.wave.RWaveManager;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public abstract class RampantRobot extends AdvancedRobot {

    public static RBattlefield globalBattlefield;
    public static REnemyManager enemyManager;
    public static RWaveManager waveManager;

    public static RBattlefield getGlobalBattlefield() {
        return globalBattlefield;
    }

    protected RPoint location;
    protected LinkedList<RRobotState> stateHistory;
    private boolean processingShot;
    private long fireTime;
    private RFiringSolution lockedSolution;
    private REnemyRobot lockedEnemy;

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

        waveManager = new RWaveManager();

        setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
        location = new RPoint(getX(), getY());
        while (true) {
            scan();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        stateHistory.push(new RRobotState(this, globalBattlefield, e));
        doRadar(e);
        String name = e.getName();
        if (!enemyManager.contains(name)) {
            enemyManager.add(name);
        }
        REnemyRobot enemy = enemyManager.get(name);
        enemy.update(this, globalBattlefield, e);

        waveManager.update(this);

        RGun gun = new RCircularTargetingGun();
        RFiringSolution firingSolution = gun.getFiringSolution(this, enemy);
        if (firingSolution != null) {
            lockFiringSolution(enemy, firingSolution);
        } else if (!processingShot) {
            setTurnGunRightRadians(enemy.getCurrentState().absoluteBearing - getGunHeadingRadians());
        }

        if (attemptShot()) {
            processingShot = false;
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

        waveManager.draw(g);
    }

    public RRobotState getCurrentState() {
        if (stateHistory.isEmpty()) {
            return null;
        }
        return stateHistory.get(0);
    }

    public RPoint getLocation() {
        return location;
    }

    protected boolean attemptShot() {
        if(!processingShot) {
            return false;
        }

        if(fireTime <= getTime() && getGunTurnRemainingRadians() == 0) {
            if(setFireBullet(lockedSolution.power) != null) {
                RBulletWave wave = new RBulletWave(this, lockedSolution, this.getTime(), null);
                waveManager.add(wave);
            }
            return true;
        }
        return false;
    }

    protected boolean lockFiringSolution(REnemyRobot enemy, RFiringSolution firingSolution) {
        if(!processingShot) {
            lockedEnemy = enemy;
            lockedSolution = firingSolution;
            setTurnGunRightRadians(Utils.normalRelativeAngle(lockedSolution.firingAngle - getGunHeadingRadians()));
            fireTime = getTime() + 1;
            processingShot = true;
            return true;
        }
        return false;
    }
}
