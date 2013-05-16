package rampancy.util.movement;

import rampancy.RampantRobot;
import rampancy.util.REnemyRobot;
import rampancy.util.RRobotState;
import rampancy.util.RUtil;
import rampancy.util.wave.REnemyWave;

public class ROrbitManager implements RMovementManager {
    
    int currentDirection;
    long timeStartCurrentDirection;

    public ROrbitManager() {
        currentDirection = Math.random() > 0.5 ? 1 : -1;
        timeStartCurrentDirection = -1;
    }

    @Override
    public void update(RampantRobot reference, REnemyWave wave) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateEndOfRound(RampantRobot reference) {
        // TODO Auto-generated method stub

    }

    @Override
    public RMovementChoice getMovementChoice(RampantRobot reference) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RMovementChoice getMovementChoice(RampantRobot reference, REnemyWave wave) {
        if (timeStartCurrentDirection < 0) {
            timeStartCurrentDirection = reference.getTime();
        }
        long timeSinceDirectionChange = reference.getTime() - timeStartCurrentDirection;
        double normalizedTimeFactor = timeSinceDirectionChange / 40.0;
        if (Math.random() < normalizedTimeFactor) {
            currentDirection = -currentDirection;
            timeStartCurrentDirection = reference.getTime();
        }
        
        REnemyRobot enemy = wave.getEnemy();
        RRobotState enemyState = enemy.getCurrentState();
        double attackAngle = 0;
        if (enemy.getPreferredSafeDistance() > enemyState.distance) {
            attackAngle = Math.PI / 6.0;
        }
        double orbitAngle = RUtil.computeOrbitAngle(RampantRobot.getGlobalBattlefield(), enemyState.location, reference.getLocation(), attackAngle, currentDirection);
        OrbitMovementChoice movement = new OrbitMovementChoice(orbitAngle, 100);
        return movement;
    }
    
    class OrbitMovementChoice extends RMovementChoice {
        public OrbitMovementChoice(double goAngle, double distance) {
            super(goAngle, distance);
        }
    }
}
