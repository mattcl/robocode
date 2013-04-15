package rampancy.util.movement;

import rampancy.RampantRobot;
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
