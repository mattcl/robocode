/**
 * 
 */
package rampancy.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * @author Matthew Chun-Lum
 *
 */
/**
 * @author Matthew Chun-Lum
 *
 */
public class RBattlefield {
    public static final int AGAINST_WALL   = 0;
    public static final int NEAR_WALL      = 1;
    public static final int AWAY_FROM_WALL = 2;
    
    public static final double AGAINST_WALL_TOLERANCE = 150;
    public static final double NEAR_WALL_TOLERANCE    = AGAINST_WALL_TOLERANCE + 150;
    
    public static final int INNER_DISTANCE = 18;
    
    public Rectangle bfRect;
    public Rectangle innerRect;
    public Rectangle adjustedInnerRect;
    public int width;
    public int height;
    
    /**
     * Constructor
     * @param width
     * @param height
     */
    public RBattlefield(int width, int height) {
        this.width = width;
        this.height = height;
        bfRect = new Rectangle(width, height);
        innerRect = new Rectangle(INNER_DISTANCE, 
                                  INNER_DISTANCE, 
                                  width - INNER_DISTANCE * 2, 
                                  height - INNER_DISTANCE * 2);
        adjustedInnerRect = new Rectangle(INNER_DISTANCE - 9, 
                INNER_DISTANCE - 9, 
                width - INNER_DISTANCE * 2 + 18, 
                height - INNER_DISTANCE * 2 + 18);
    }
    
    /**
     * @param point
     * @return true if the point is contained in the battlefield
     */
    public boolean contains(RPoint point) {
        return bfRect.contains(point);
    }
    
    public double distanceFromTop(RPoint point) {
        return Math.abs(bfRect.height - point.y);
    }
    
    public double distanceFromBot(RPoint point) {
        return point.y;
    }
    
    public double distanceFromLeft(RPoint point) {
        return point.x;
    }
    
    public double distanceFromRight(RPoint point) {
        return Math.abs(bfRect.width - point.x);
    }
    
    public double innerDistanceFromTop(RPoint point) {
        return Math.max(1, distanceFromTop(point) - INNER_DISTANCE);
    }
    
    public double innerDistanceFromBot(RPoint point) {
        return Math.max(1, distanceFromBot(point) - INNER_DISTANCE);
    }
    
    public double innerDistanceFromRight(RPoint point) {
        return Math.max(1, distanceFromRight(point) - INNER_DISTANCE);
    }
    
    public double innerDistanceFromLeft(RPoint point) {
        return Math.max(1, distanceFromLeft(point) - INNER_DISTANCE);
    }
    
    /**
     * @param point
     * @return the closest distance to any wall
     */
    public double distanceFromWall(RPoint point) {
        if(!contains(point))
            return -1;
        
        double distLeft = point.x;
        double distRight = bfRect.width - point.x;
        double distTop = bfRect.height - point.y;
        double distBot = point.y;
        
        return Math.min(Math.min(distRight, distLeft), Math.min(distTop, distBot));
    }
    
    /**
     * @param point
     * @return the category corresponding to the point's
     *         distance from a wall
     */
    public int distanceFromWallCategory(RPoint point) {
        return distanceFromWallCategory(distanceFromWall(point));
    }
    
    /**
     * @param distance
     * @return the category corresponding to a distance from a wall
     */
    public int distanceFromWallCategory(double distance) {
        if(distance < AGAINST_WALL_TOLERANCE)
            return AGAINST_WALL;
        
        if(distance < NEAR_WALL_TOLERANCE)
            return NEAR_WALL;
        
        return AWAY_FROM_WALL;
    }
    
    /**
     * @param point
     * @return
     */
    public boolean validMovePosition(RPoint point) {
        return innerRect.contains(point);
    }
    
    public void draw(Graphics2D g) {
        g.setColor(Color.gray);
        g.drawRect(innerRect.x, innerRect.y, innerRect.width, innerRect.height);
    }
}
