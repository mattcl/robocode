/**
 * 
 */
package rampancy.util;

import java.awt.geom.Rectangle2D;

import rampancy.util.*;

public class RRectangle extends Rectangle2D.Double {
    
    public static RRectangle getIntersectionRectangle(RPoint p1, RPoint p2) {
        return getIntersectionRectangle(p1, p2, REnemyRobot.BOT_RADIUS, REnemyRobot.BOT_RADIUS);
    }
    
    public static RRectangle getIntersectionRectangle(RPoint p1, RPoint p2, double r1, double r2) {
        RRectangle rect1 = new RRectangle(p1, r1);
        RRectangle rect2 = new RRectangle(p2, r2);
        RRectangle intersection = (RRectangle) rect1.createIntersection(rect2);
        if(intersection.width < 0 || intersection.height < 0)
            return null;
        
        return intersection;
    }

    public RRectangle(RPoint point) {
        this(point, REnemyRobot.BOT_RADIUS);
    }
    
    public RRectangle(RPoint point, double radius) {
        this(point.x - radius, point.y - radius, radius * 2, radius * 2);
    }
    
    public RRectangle(double x, double y, double width, double height) {
        super(x, y, width, height);
    }
    
    public RPoint getCenter() {
        return new RPoint(x + width / 2.0, y + height / 2.0);
    }
    
}
