package rampancy.util.movement;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import rampancy.util.RDrawable;
import rampancy.util.RPoint;
import rampancy.util.RUtil;

public class RMovementPath implements RDrawable {
	public static final Color DEFAULT_COLOR = Color.blue;
	protected ArrayList<RPoint> path;
	protected Color color;

    public RMovementPath() {
    	this(DEFAULT_COLOR);
    }
    
    public RMovementPath(Color color) {
    	this.color = color;
    }
    
    public void setPath(ArrayList<RPoint> path) {
    	this.path = path;
    }

    @Override
    public void draw(Graphics2D g) {
    	Color lastColor = g.getColor();
    	g.setColor(color);
    	for (RPoint point : path) {
    		RUtil.drawOval(point, 3, g);
    	}
    	g.setColor(lastColor);
    }

}
