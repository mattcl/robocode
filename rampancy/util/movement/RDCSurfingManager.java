package rampancy.util.movement;

import rampancy.RampantRobot;
import rampancy.util.RPoint;
import rampancy.util.RUtil;
import rampancy.util.data.kdTree.KDTree;

public class RDCSurfingManager implements RMovementManager {
	public static final int BUCKET_SIZE = 10;
	protected KDTree<Double> tree;
	
	public RDCSurfingManager() {
		tree = new KDTree<Double>(BUCKET_SIZE);
	}

	@Override
	public void update(RampantRobot reference) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateEndOfRound(RampantRobot reference) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public RMovementChoice getMovementChoice(RampantRobot reference) {
		// TODO Auto-generated method stub
		// 1. Determine the wave that poses the greatest danger to us right now
		// 2. Compute our current max escape angle in each direction for that wave
		// 3. Determine the safest guess factors (make sure to take into account the bot width
		// 4. Select the best guess factor to move to, given the current situation
		//		a. we may need to flatten our current movement profile,
		//         perhaps we select a more dangerous location with the intent
		//         of distributing our movement profile
	    // 5. If possible, select our orbit angle path to maintain our desired
	    //    distance. Possibly move in for the attack if the opponent has a
	    //    low enough hit percentage
		// 6. return the movement choice
		
		/*
		 * How to prevent us from moving back and forth? Maybe don't surf more
		 * than one wave at a time? Don't move into the danger zone of another
		 * wave? 
		 */
	    
		return null;
	}
	
	protected RPoint getOrbitLocation() {
		return null;
	}
	
	class DCSurfingPoint {
		public double guessFactor;
		
		public DCSurfingPoint(double guessFactor) {
			this.guessFactor = guessFactor;
		}
		
		public double deviationSum(double mu) {
			return Math.pow(guessFactor - mu, 2);
		}
		
		public double kernel(double testPoint, double bandwidth) {
			double diff = (testPoint - guessFactor) / bandwidth;
			return RUtil.GAUSSIAN_COEFFICIENT * Math.exp(-0.5 * diff * diff);
		}
	}
}
