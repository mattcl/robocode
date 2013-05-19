package rampancy.util.external;

import rampancy.util.RBattlefield;
import rampancy.util.RPoint;
import robocode.util.Utils;

/*
 * Code from the RoboWiki, original author: David Alves
 * Modified to suit my needs
 */
public class WallSmoothing {
	public static double wallSmoothedAngle(RBattlefield bf, RPoint orbitCenter, RPoint position, double direction, double distanceToOrbitCenter) {
		RPoint smoothedPoint = fastWallSmooth(bf, orbitCenter, position, direction, distanceToOrbitCenter);
		double angle = position.computeAbsoluteBearingTo(smoothedPoint);
		return angle;
	}
	
	public static RPoint fastWallSmooth(RBattlefield bf, RPoint orbitCenter, RPoint position, double direction, double distanceToOrbitCenter){
		final double MARGIN = 18;
		final double STICK_LENGTH = 150;
	 
		double fieldWidth = bf.width, fieldHeight = bf.height;
	 
		double stick = Math.min(STICK_LENGTH, distanceToOrbitCenter);
		double stickSquared = square(stick);
	 
		int LEFT = -1, RIGHT = 1, TOP = 1, BOTTOM = -1;
	 
		int topOrBottomWall = 0;
		int leftOrRightWall = 0;
	
		position.computeAbsoluteBearingTo(orbitCenter);
		double desiredAngle = Utils.normalAbsoluteAngle(position.computeAbsoluteBearingTo(orbitCenter) - direction * Math.PI / 2.0);
		RPoint projected = position.projectTo(desiredAngle, stick);
		if(projected.x >= 18 && projected.x <= fieldWidth - 18 && projected.y >= 18 && projected.y <= fieldHeight - 18)
			return projected;
	 
		if(projected.x  > fieldWidth - MARGIN || position.x  > fieldWidth - stick - MARGIN) leftOrRightWall = RIGHT;
		else if (projected.x < MARGIN || position.x < stick + MARGIN) leftOrRightWall = LEFT;
	 
		if(projected.y > fieldHeight - MARGIN || position.y > fieldHeight - stick - MARGIN) topOrBottomWall = TOP;
		else if (projected.y < MARGIN || position.y < stick + MARGIN) topOrBottomWall = BOTTOM;
	 
		if(topOrBottomWall == TOP){
			if(leftOrRightWall == LEFT){
				if(direction > 0)
					//smooth against top wall
					return new RPoint(position.x + direction * Math.sqrt(stickSquared - square(fieldHeight - MARGIN - position.y)), fieldHeight - MARGIN);
				else
					//smooth against left wall
					return new RPoint(MARGIN, position.y + direction * Math.sqrt(stickSquared - square(position.x - MARGIN)));
	 
			} else if(leftOrRightWall == RIGHT){
				if(direction > 0)
					//smooth against right wall
					return new RPoint(fieldWidth - MARGIN, position.y - direction * Math.sqrt(stickSquared - square(fieldWidth - MARGIN - position.x)));
				else 
					//smooth against top wall
					return new RPoint(position.x + direction * Math.sqrt(stickSquared - square(fieldHeight - MARGIN - position.y)), fieldHeight - MARGIN);
	 
			}
			//Smooth against top wall
			return new RPoint(position.x + direction * Math.sqrt(stickSquared - square(fieldHeight - MARGIN - position.y)), fieldHeight - MARGIN); 
		} else if(topOrBottomWall == BOTTOM){
			if(leftOrRightWall == LEFT){
				if(direction > 0)
					//smooth against left wall
					return new RPoint(MARGIN, position.y + direction * Math.sqrt(stickSquared - square(position.x - MARGIN)));
				else
					//smooth against bottom wall
					return new RPoint(position.x - direction * Math.sqrt(stickSquared - square(position.y - MARGIN)), MARGIN);
			} else if(leftOrRightWall == RIGHT){
				if(direction > 0)
					//smooth against bottom wall
					return new RPoint(position.x - direction * Math.sqrt(stickSquared - square(position.y - MARGIN)), MARGIN);
				else
					//smooth against right wall
					return new RPoint(fieldWidth - MARGIN, position.y - direction * Math.sqrt(stickSquared - square(fieldWidth - MARGIN - position.x)));
	 
			}
			//Smooth against bottom wall
			return new RPoint(position.x - direction * Math.sqrt(stickSquared - square(position.y - MARGIN)), MARGIN);
		}
	 
		if(leftOrRightWall == LEFT){
			//smooth against left wall
			return new RPoint(MARGIN, position.y + direction * Math.sqrt(stickSquared - square(position.x - MARGIN)));
		} else if(leftOrRightWall == RIGHT){
			//smooth against right wall
			return new RPoint(fieldWidth - MARGIN, position.y - direction * Math.sqrt(stickSquared - square(fieldWidth - MARGIN - position.x)));
		}
	 
		throw new RuntimeException("This code should be unreachable. position = " + position.x + ", " + position.y + "  orbitCenter = " + orbitCenter.x + ", " + orbitCenter.y + " direction = " + direction);
	}
	 
	public static double square(double x){
		return x*x;
	}
}