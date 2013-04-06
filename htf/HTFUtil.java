package htf;

import robocode.util.Utils;

/**
* A set of simpler functions for working with angles in robocode.
*
* @author Matt Chun-Lum
*
*/
public abstract class HTFUtil {

    /**
	* Computes the distance to the given location from another location
	* @param x1 the x coordinate of the first location
	* @param y1 the y coordinate of the first location
	* @param x2 the x coordinate of the second location
	* @param y2 the y coordinate of the second location
	* @return the distance between the passed coordinates
	*/
    public static double distanceTo(double x1, double y1, double x2, double y2) {
        x1 -= x2;
        y1 -= y2;
        return Math.sqrt(x1 * x1 + y1 * y1);
    }
    
    public static double projectX(double x, double angle, double length) {
    	return x + HTFUtil.sin(angle) * length;
    }
    
    public static double projectY(double y, double angle, double length) {
    	return y + HTFUtil.cos(angle) * length;
    }
    
    public static double computeAbsoluteBearing(double x1, double y1, double x2, double y2) {
    	return Utils.normalAbsoluteAngleDegrees(HTFUtil.atan2(x2 - x1, y2 - y1));
    }
    
    public static double getBulletVelocityFromPower(double bulletPower) {
    	return (20.0 - (3.0 * bulletPower));
    }
    
    public static double getBulletPowerFromVelocity(double velocity) {
    	return Math.max(0.1, (20 - velocity) / 3.0);
    }
    
    // ---------- Trig Functions ---------- //
    
    /**
	* Computes the bearing from the source to the target locations
	* @param sourceX
	* @param sourceY
	* @param targetX
	* @param targetY
	* @return the absolute bearing in degrees
	*/
    public static double bearingToLocation(double sourceX, double sourceY, double targetX, double targetY) {
        return Math.toDegrees(Math.atan2(targetX - sourceX, targetY - sourceY));
    }
    
    /**
	* Computes the sine of the passed angle (in degrees)
	* @param degrees the angle
	* @return the sine of the passed angle
	*/
    public static double sin(double degrees) {
        return Math.sin(Math.toRadians(degrees));
    }
    
    /**
	* Computes the arc sine (inverse sine) corresponding to the passed
	* value.
	* @param value
	* @return the angle corresponding to the passed value in the range of -90 to 90 degrees
	*/
    public static double asin(double value) {
        return Math.toDegrees(Math.asin(value));
    }
    
    /**
	* Computes the cosine of the passed angle (in degrees)
	* @param degrees the angle
	* @return the cosine of the passed angle
	*/
    public static double cos(double degrees) {
        return Math.cos(Math.toRadians(degrees));
    }
    
    /**
	* Computes the arc cosine of the passed value
	* @param value
	* @return the angle corresponding to the passed value in the range of 0 to 180 degrees
	*/
    public double acos(double value) {
        return Math.toDegrees(Math.acos(value));
    }
    
    /**
	* Computes the tangent of the passed angle (in degrees)
	* @param degrees
	* @return
	*/
    public static double tan(double degrees) {
        return Math.tan(Math.toRadians(degrees));
    }
    
    /**
	* Computes the arc tangent of the passed value
	* @param value
	* @return the angle corresponding to the passed value in the range of -90 to 90 degrees
	*/
    public static double atan(double value) {
        return Math.toDegrees(Math.atan(value));
    }
    
    /**
	* Computes the arc tangent of the passed x and y values
	* @param x
	* @param y
	* @return the angle corresponding to the passed values in the range of -180 to 180 degrees
	*/
    public static double atan2(double x, double y) {
        return Math.toDegrees(Math.atan2(x, y));
    }
}