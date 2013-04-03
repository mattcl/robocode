/**
 * REnemyListener.java
 */
package rampancy.util;

/**
 * @author Matthew Chun-Lum
 *
 */
public interface REnemyListener {

    /**
     * Invoked when an enemyRobot is updated
     * @param enemy
     */
    public abstract void enemyUpdated(REnemyRobot enemy);
    
    /**
     * Invoked when an enemyRobot fires a shot
     * @param enemy
     */
    public abstract void shotFired(REnemyRobot enemy);
}
