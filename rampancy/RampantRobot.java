package rampancy;

import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import rampancy.util.RBattlefield;
import rampancy.util.REnemyManager;
import rampancy.util.REnemyRobot;
import rampancy.util.RPoint;
import rampancy.util.RRobot;
import rampancy.util.RRobotState;
import rampancy.util.RUtil;
import rampancy.util.gun.RDisabledGun;
import rampancy.util.gun.RFiringSolution;
import rampancy.util.gun.RGunManager;
import rampancy.util.movement.RMovementChoice;
import rampancy.util.movement.RMovementManager;
import rampancy.util.wave.RBulletWave;
import rampancy.util.wave.REnemyWave;
import rampancy.util.wave.RWaveManager;
import robocode.AdvancedRobot;
import robocode.Bullet;
import robocode.BulletHitEvent;
import robocode.HitByBulletEvent;
import robocode.RoundEndedEvent;
import robocode.ScannedRobotEvent;
import robocode.SkippedTurnEvent;
import robocode.util.Utils;

public abstract class RampantRobot extends AdvancedRobot implements RRobot {

    public static RBattlefield globalBattlefield;
    public static REnemyManager enemyManager;
    public static RWaveManager waveManager;
    public static RMovementManager movementManager;
    public static RGunManager gunManager;

    public static RBattlefield getGlobalBattlefield() {
        return globalBattlefield;
    }

    public static RGunManager getGunManager() {
    	return gunManager;
    }

    protected RPoint location;
    protected LinkedList<RRobotState> stateHistory;
    private boolean processingShot;
    private long fireTime;
    private int lockedSolutionIndex;
    private List<RFiringSolution> lockedSolutions;
    private REnemyRobot lockedEnemy;

    public RampantRobot() {
        super();
        stateHistory = new LinkedList<RRobotState>();
        location = null;
    }
    
    abstract protected void initGunManager(RGunManager gunManager);
    abstract protected void initMovementManager();

    public void run() {
        super.run();
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
        initialSetup();

        globalBattlefield = new RBattlefield((int) getBattleFieldWidth(), (int) getBattleFieldHeight());

        if (enemyManager == null) {
            enemyManager = new REnemyManager();
        }
        
        if (movementManager == null) {
        	initMovementManager();
        }

        if (gunManager == null) {
        	gunManager = new RGunManager();
        	gunManager.add(new RDisabledGun());
        	initGunManager(gunManager);
        }
        
        waveManager = new RWaveManager();
        
        setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
        location = new RPoint(getX(), getY());
        while (true) {
            scan();
        }
    }
    
    abstract public void initialSetup();

    public void onScannedRobot(ScannedRobotEvent e) {
        stateHistory.push(new RRobotState(this, globalBattlefield, e));
        doRadar(e);
        String name = e.getName();
        if (!enemyManager.contains(name)) {
            enemyManager.add(name);
        }
        REnemyRobot enemy = enemyManager.get(name);
        enemy.update(this, globalBattlefield, e);
        if (enemy.shotFired()) {
        	waveManager.add(new REnemyWave(enemy, this, getTime() - 1));
        }
        waveManager.update(this);
        
        // determine movement here
        REnemyWave surfableWave = waveManager.getMostDangerousWave(this);
        if (surfableWave != null) {
	        RMovementChoice movementChoice = movementManager.getMovementChoice(this, surfableWave);
	        if (movementChoice != null) {
	            RUtil.setBackAsFront(this, movementChoice.goAngle, movementChoice.distance);
	        }
        }

        List<RFiringSolution> firingSolutions = gunManager.getFiringSolutions(this, enemy);
        if (!firingSolutions.isEmpty()) {
            lockFiringSolutions(enemy, firingSolutions);
        } else if (!processingShot) {
            setTurnGunRightRadians(enemy.getCurrentState().absoluteBearing - getGunHeadingRadians());
        }

        if (attemptShot()) {
            processingShot = false;
        }
    }

    public void onBulletHit(BulletHitEvent e) {
    	Bullet bullet = e.getBullet();
    	REnemyRobot enemy = enemyManager.get(e.getName());
		if(enemy == null) {
			return;
		}
    }
    
    public void onHitByBullet(HitByBulletEvent e) {
       Bullet bullet = e.getBullet();
       REnemyWave wave = waveManager.getWaveForBullet(bullet);
       if (wave != null) {
           wave.setHitLocation(new RPoint(bullet.getX(), bullet.getY()));
           movementManager.update(this, wave);
       }
    }

    public void onRoundEnded(RoundEndedEvent e) {
    	out.print(gunManager);
    	gunManager.updateEndOfRound(this);
    }
    
	public void onSkippedTurn(SkippedTurnEvent event) {
		super.onSkippedTurn(event);
		System.out.println("Skipped turn!");
        out.println("Skipped turn!");
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
    
    public RRobotState getLastState() {
    	if (stateHistory.size() < 2) {
    		return getCurrentState();
    	}
    	return stateHistory.get(1);
    }
    
    public RRobotState getTargetableState() {
    	if (stateHistory.size() < 3) {
    		return getLastState();
    	}
    	return stateHistory.get(2);
    }

	public RPoint getLocation() {
        return getCurrentState().location;
    }

    protected boolean attemptShot() {
        if(!processingShot) {
            return false;
        }

        if(fireTime <= getTime() && getGunTurnRemainingRadians() == 0) {
            if(setFireBullet(lockedSolutions.get(lockedSolutionIndex).power) != null) {
            	for (int i = 0; i < lockedSolutions.size(); i++) {
	                RBulletWave wave = new RBulletWave(this, lockedSolutions.get(i), this.getTime(), null, i != lockedSolutionIndex);
	                waveManager.add(wave);
            	}
            }
            return true;
        }
        return false;
    }

    protected boolean lockFiringSolutions(REnemyRobot enemy, List<RFiringSolution> firingSolutions) {
        if(!processingShot) {
            lockedEnemy = enemy;
            lockedSolutions = firingSolutions;
            int bestIndex = 0;
            for (int i = 1; i < firingSolutions.size(); i++) {
            	if (firingSolutions.get(i).gun.getHitPercentage() > firingSolutions.get(bestIndex).gun.getHitPercentage()) {
            		bestIndex = i;
            	}
            }
            lockedSolutionIndex = bestIndex;
            setTurnGunRightRadians(Utils.normalRelativeAngle(lockedSolutions.get(lockedSolutionIndex).firingAngle - getGunHeadingRadians()));
            fireTime = getTime() + 1;
            processingShot = true;
            return true;
        }
        return false;
    }
}
