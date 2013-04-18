/**
 * Util.java
 */
package rampancy.util;

import java.awt.Graphics2D;
import java.util.ArrayList;

import rampancy.util.external.MovementPredictor;
import rampancy.util.movement.RMovementPath;
import robocode.AdvancedRobot;
import robocode.util.Utils;

public abstract class RUtil {
    
    public static final double WALL_STICK = 150;
	public static final double GAUSSIAN_COEFFICIENT = 1.0 / Math.sqrt(2 * Math.PI);
    
    public static double computeRequiredBulletPower(RPoint source, RPoint target, int time) {
        double distance = source.distance(target);
        double requiredVelocity = distance / (double) time;
        return computeBulletPower(requiredVelocity);
    }
    
    /**
     * Computes the absolute bearing from the source to the target
     */
    public static double computeAbsoluteBearing(RPoint source, RPoint target) {
        return Utils.normalAbsoluteAngle(Math.atan2(target.x - source.x, target.y - source.y));
    }
    
    /**
     * Computes the proper guess factor for the statistics tracking array
     */
    public static int computeBin(double factor, int numBins) {
        double value = (factor * ((numBins - 1) / 2)) + ((numBins - 1) / 2);
        return (int) limit(0, value, numBins -1);
    }
    
    public static double computeBulletPower(double velocity) {
        return Math.max(0.1, (20.0 - velocity) / 3.0);
    }
    
    public static double computeBulletVelocity(double bulletPower) {
        return (20.0 - (3.0 * bulletPower));
    }
    
    /**
     * Determines the max escape angle given bullet velocity
     * @param velocity
     * @return the max escape angle
     */
    public static double computeMaxEscapeAngle(double velocity) {
        return Math.asin(8.0 / velocity);
    }
    
    public static double computePreciseMaxEscapeAngle(RBattlefield battlefield, RRobotState shooterState, RRobotState targetState, double bulletVelocity, int direction) {
    	return computePreciseMaxEscapeAngle(battlefield, shooterState, targetState, bulletVelocity, direction, null);
    }
    
    public static double computePreciseMaxEscapeAngle(RBattlefield battlefield, RRobotState shooterState, RRobotState targetState, double bulletVelocity, int direction, RMovementPath movementPath) {
    	ArrayList<RPoint> path = simulateMovement(battlefield, shooterState, targetState, bulletVelocity, direction);
    	if (movementPath != null) {
	    	movementPath.setPath(path);
    	}
    	if (path.isEmpty()) {
    		return 0;
    	}
    	double finalLocationAbsB = shooterState.location.computeAbsoluteBearingTo(path.get(path.size() - 1));
    	double escapeAngle = Utils.normalRelativeAngle(finalLocationAbsB - targetState.absoluteBearing);
    	return Math.abs(escapeAngle);
    }
   
    public static ArrayList<RPoint> simulateMovement(RBattlefield battlefield, RRobotState shooterState, RRobotState targetState, double bulletVelocity, int movDir) {
        ArrayList<RPoint> locations = new ArrayList<RPoint>();
    	MovementPredictor.PredictionStatus status = new MovementPredictor.PredictionStatus(targetState.location.x, targetState.location.y, targetState.heading, targetState.velocity, 0);
    	long time = 0;
    	do {
	    	double orbitAngle = computeOrbitAngle(battlefield, shooterState.location, status, 0.0, movDir);
    		status = MovementPredictor.predict(status, orbitAngle);
    		locations.add(status);
    		time++;
    	} while (bulletVelocity * time < status.distance(shooterState.location));
    	return locations;
    }
    
    /**
     * @param location
     * @param wave
     * @param attackAngle
     * @param direction
     * @return the appropriate orbit angle
     *
    public static double computeOrbitAngle(RPoint location, REnemyWave wave, double attackAngle, int direction) {
        double goAngle = RUtil.computeAbsoluteBearing(wave.getOrigin(), location);
        goAngle = RUtil.wallSmoothing(location, goAngle + (direction * (Math.PI / 2 + attackAngle)), direction, wave.getOrigin().distance(location));
        return goAngle;
    }*/
    
    public static double computeOrbitAngle(RBattlefield battlefield, RPoint center, RPoint location, double attackAngle, int direction) {
        double goAngle = RUtil.computeAbsoluteBearing(center, location);
        return RUtil.wallSmoothing(battlefield, location, goAngle + (Math.PI / 2.0 + attackAngle) * direction, direction, location.distance(center));
    }
    
    public static void drawLine(RPoint p1, RPoint p2, Graphics2D g) {
    	g.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
    }
    
    /**
     * Draws an oval using the passed point and radius
     * @param point
     * @param radius
     * @param g
     */
    public static void drawOval(RPoint point, int radius, Graphics2D g) {
        g.drawOval((int) point.x - radius, (int) point.y - radius, radius * 2, radius * 2);
    }
    
    /**
     * Fills an oval using the passed point and radius
     * @param point
     * @param radius
     * @param g
     */
    public static void fillOval(RPoint point, int radius, Graphics2D g) {
        g.fillOval((int) point.x - radius, (int) point.y - radius, radius * 2, radius * 2);
    }
    
    /**
     * @param point
     * @return the Point's distance from the wall
     */
    public static double getDistanceFromWall(RBattlefield battlefield, RPoint point) {
        if(battlefield == null)
            return -1;
        
        return battlefield.distanceFromWall(point);
    }
    
    /**
     * returns the factor index for the statistics array
     *
    public static int getFactorIndex(REnemyWave wave, RPoint target, int numBins) {
        double offsetAngle = wave.computeOffsetAngle(target);
        double factor = Utils.normalRelativeAngle(offsetAngle) / RUtil.computeMaxEscapeAngle(wave.getVelocity()) * wave.getDirection();
        return computeBin(factor, numBins);
    }*/
    
    /**
     * @param index
     * @param arraySize
     * @return a guess factor in the range of -1 to 1
     */
    public static double getGuessFactorForIndex(int index, int arraySize) {
        int offset = index - (arraySize - 1) / 2;
        return (double) offset / (double) ((arraySize - 1) / 2);
    }
    
    /**
     * @param arr
     * @return the index of the largest element in the array
     */
    public static int indexOfLargest(double[] arr) {
        double largest = 0;
        int largestIndex = (arr.length - 1) /2;
        
        for(int i = 0; i < arr.length; i++) {
            if(arr[i] > largest) {
                largest = arr[i];
                largestIndex = i;
            }
        }
        return largestIndex;
    }
    
    /**
     * @param arr
     * @return the index of the smallest element in the array
     */
    public static int indexOfSmallest(double[] arr) {
        double lowest = 50000;
        int lowestIndex = -1;
        
        for(int i = 0; i < arr.length; i++) {
            if(arr[i] < lowest) {
                lowest = arr[i];
                lowestIndex = i;
            }
        }
        return lowestIndex;
    }
    
    /**
     * Determines if the passed value is within the range
     * @param min
     * @param max
     * @param value
     * @return
     */
    public static boolean inRange(double min, double max, double value) {
        return (value >= min && value <= max);
    }
    
    /**
     * Determines if the enemy is advancing (mainly to counter ram bots)
     * @param absoluteBearing
     * @param enemyHeading
     * @param deltaHeading
     * @param velocity
     * @return {@code true} if the enemy is advancing
     */
    public static boolean isAdvancing(double absoluteBearing, double enemyHeading, double deltaHeading, double velocity) {
        if(inRange(-Constants.RAM_TOLERANCE, Constants.RAM_TOLERANCE, Math.abs(enemyHeading - absoluteBearing) - Math.PI)) {
            return (Math.abs(deltaHeading) < Constants.DELTA_TOLERANCE && velocity > 1);
        }
        return false;
    }
    
    /**
     * Limits the passed value to the range between the passed max
     * and min values
     * @param min
     * @param value
     * @param max
     * @return the limited value
     */
    public static double limit(double min, double value, double max) {
        return Math.max(min, Math.min(value, max));
    }
    
    /**
     * @param values
     * @return the lowest value from the list of values
     */
    public static double lowest(double[] values) {
        double lowest = values[0];
        for(int i = 1; i < values.length; i++)
            if(lowest > values[i])
                lowest = values[i];
        return lowest;
    }
    
    public static int sign(double d) {
        return (d < 0 ? -1 : d > 0 ? 1 : 0);
    }
    
    /**
     * Returns 1 or -1 given the passed value
     * @param d
     * @return
     */
    public static int nonZeroSign(double d) {
        if (d < 0) { return -1; }
        return 1;
    }
    
    /**
     * Projects a point location given distance and angle
     */
    public static RPoint project(RPoint source, double angle, double length) {
        double x = source.x + Math.sin(angle) * length;
        double y = source.y + Math.cos(angle) * length;
        return new RPoint(x, y);
    }
    
    /**
     * Computes the rolling average
     * @param value
     * @param newEntry
     * @param n
     * @param weighting
     * @return
     */
    public static double rollingAvg(double value, double newEntry, double n, double weighting ) {
        return (value * n + newEntry * weighting)/(n + weighting);
    }
    
    /**
     * Rounds the specified value to the passed float precision
     * @param value
     * @param precision
     * @return the rounded value
     */
    public static double roundToPrecision(double value, int precision) {
        int temp = (int) Math.round((value * Math.max(1, Math.pow(10, precision))));
        return ((double) temp) / Math.pow(10, precision);
    }
    
    /**
     * Scales a given value to fall within the given min and max values
     * @param min
     * @param max
     * @param minExpected
     * @param maxExpected
     * @param value
     * @return
     */
    public static double scaleToRange(double min, double max, double minExpected, double maxExpected, double value) {
        return scaleToRange(min, max, minExpected, maxExpected, value, 0);
    }

    /**
     * Scales a given value to fall within the given min and max values
     * @param min
     * @param max
     * @param minExpected
     * @param maxExpected
     * @param value
     * @param offset
     * @return
     */
    public static double scaleToRange(double min, double max, double minExpected, double maxExpected, double value, double offset) {
        double scaleRange = max - min;
        double valueRange = maxExpected - minExpected;
        double percentDiff = scaleRange / valueRange;
        return RUtil.limit(min, (value + offset) * percentDiff, max);
    }
    
    public static double normalize(double value) {
    	return normalize(value, 1);
    }
    
    public static double normalize(double value, double weight) {
    	return 1.0 / (1.0 + weight * value);
    }
    
    /**
     * Credit: Voidious
     * @param robot
     * @param goAngle
     * @param dist
     */
    public static void setBackAsFront(AdvancedRobot robot, double goAngle, double dist) {
        double angle = Utils.normalRelativeAngle(goAngle - robot.getHeadingRadians());
        if (Math.abs(angle) > (Math.PI/2)) {
            if (angle < 0) {
                robot.setTurnRightRadians(Math.PI + angle);
            } else {
                robot.setTurnLeftRadians(Math.PI - angle);
            }
            robot.setBack(dist);
        } else {
            if (angle < 0) {
                robot.setTurnLeftRadians(-1*angle);
           } else {
               robot.setTurnRightRadians(angle);
           }
            robot.setAhead(dist);
        }
    }
    
    
    /**
     * @param value
     * @return the square of the tpassed value
     */
    public static double square(double value) {
        return Math.pow(value, 2);
    }
    
    /**
     * Computes the sum of two vectors
     * @param arr1
     * @param arr2
     * @return the sum of the two vectors
     */
    public static double[] sum(double[] arr1, double[] arr2) {
        double[] sum = new double[arr1.length];
        if(arr1.length != arr2.length) {
            System.err.print("Sum error: arrays not the same length!");
            return null;
        }
        
        for(int i = 0; i < arr1.length; i++)
            sum[i] = arr1[i] + arr2[1];
        return sum;
    }
    
    public static double[] average(double[] arr1, double[] arr2) {
        double[] sum = sum(arr1, arr2);
        if(sum == null)
            return null;
        
        for(int i = 0; i < sum.length; i++)
            sum[i] /= 2;
        return sum;
    }
    
    /**
     * Handles wall smoothing in one pass, no iterations
     * @param location
     * @param goAngle
     * @param direction
     * @param distanceToCenterOfOrbit
     * @return the wall smoothed angle
     */
    public static double wallSmoothing(RBattlefield bf, RPoint location, double goAngle, int direction, double distanceToCenterOfOrbit) {
        double wallStick = Math.min(distanceToCenterOfOrbit, WALL_STICK);
        
        RPoint projectedLocation = project(location, goAngle, wallStick);
        if(bf.validMovePosition(projectedLocation)) 
            return goAngle; // no change needed
        
        double topDist    = bf.innerDistanceFromTop(location);
        double rightDist  = bf.innerDistanceFromRight(location);
        double leftDist   = bf.innerDistanceFromLeft(location);
        double botDist    = bf.innerDistanceFromBot(location);
        
        boolean top   = topDist <= wallStick;
        boolean bot   = botDist <= wallStick;
        boolean right = rightDist <= wallStick;
        boolean left  = leftDist <= wallStick;
        
        boolean clockwise = direction > 0;
        
        boolean smoothTop   = top   && (!(right || left) || (right && !clockwise) || (left  && clockwise));
        boolean smoothBot   = bot   && (!(right || left) || (left  && !clockwise) || (right && clockwise));
        boolean smoothRight = right && (!(top   || bot)  || (bot   && !clockwise) || (top   && clockwise));
        boolean smoothLeft  = left  && (!(top   || bot)  || (top   && !clockwise) || (bot   && clockwise));
        
        double newAngle;
        
        double tolerance = 2;
        
        if(smoothTop) {
            newAngle = (topDist < tolerance ? Math.PI / 2 : Math.acos(topDist / wallStick)) * direction;
        } else if(smoothBot) {
            newAngle = (botDist < tolerance ? Math.PI / 2 : Math.acos(botDist / wallStick)) * direction + Math.PI;
        } else if(smoothRight) {
            newAngle = (rightDist < tolerance ? Math.PI / 2 : Math.acos(rightDist / wallStick)) * direction + Math.PI / 2;
        } else if(smoothLeft) {
            newAngle = (leftDist < tolerance ? Math.PI / 2 : Math.acos(leftDist / wallStick)) * direction + 3 * Math.PI / 2;
        } else {
            System.err.println("Smoothing Error!");
            return goAngle;
        }
        return newAngle;
    }

    /**
     * @param deltaH
     * @param deltaH2
     * @return
     */
    public static double percentDifference(double v1, double v2) {
        double sum = (v1 + v2) / 2;
        if(sum == 0)
            return 1;
        
        return Math.abs((v1 - v2) / sum);
    }
   
    public static boolean pointOnRobotPoint(RPoint point, RPoint robotLocation) {
        int radius = REnemyRobot.BOT_RADIUS;
        return (point.x >= robotLocation.x - radius && point.x <= robotLocation.x + radius) && (point.y >= robotLocation.y - radius && point.y <= robotLocation.y + radius);
    }
    
    public static boolean pointOnRobot(RPoint point, RRobot enemy) {
        RPoint enemyLocation = enemy.getCurrentState().location;
        int radius = REnemyRobot.BOT_RADIUS;
        return (point.x >= enemyLocation.x - radius && point.x <= enemyLocation.x + radius) && (point.y >= enemyLocation.y - radius && point.y <= enemyLocation.y + radius);
    }
}
