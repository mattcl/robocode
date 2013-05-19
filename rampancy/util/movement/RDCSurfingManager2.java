package rampancy.util.movement;

import rampancy.RampantRobot;
import rampancy.util.RRobotState;
import rampancy.util.RUtil;
import rampancy.util.data.kdTree.KDPoint;
import rampancy.util.data.kdTree.KDTree;
import rampancy.util.wave.REnemyWave;
import robocode.Rules;

public class RDCSurfingManager2 implements RMovementManager {
	public static final int BUCKET_SIZE = 10;
	public static final int NUM_NEIGHBORS = 13;
	protected KDTree<DCSurfingPoint> tree;

	public RDCSurfingManager2() {
		tree = new KDTree<DCSurfingPoint>(BUCKET_SIZE);
	}

	@Override
	public void update(RampantRobot reference, REnemyWave wave) {
	    double guessFactor = wave.getHitGuessFactor();
	    DCSurfingPoint value = new DCSurfingPoint(guessFactor, reference.getTime());
	    KDPoint<DCSurfingPoint> observation = new KDPoint<DCSurfingPoint>(value, getCoordinateForState(wave.getInitialTargetState()));
	    tree.add(observation);
	}
	
	protected double[] getCoordinateForState(RRobotState state) {
        double[] query = {
				RUtil.normalize(state.lateralVelocity, -Rules.MAX_VELOCITY, Rules.MAX_VELOCITY),
				RUtil.normalize(state.advancingVelocity, -Rules.MAX_VELOCITY, Rules.MAX_VELOCITY),
				RUtil.normalize(state.distance, 0, RampantRobot.getGlobalBattlefield().getMaxDistance()),
                RUtil.normalizeTime(state.timeSinceDirectionChange),
                RUtil.normalizeTime(state.timeSinceVelocityChange)
            };
        return query;
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
	public RMovementChoice getMovementChoice(RampantRobot reference,
			REnemyWave wave) {
		// TODO Auto-generated method stub
		return null;
	}
	
	class DCSurfingPoint {
	    public long recordedTime;
		public double guessFactor;
		
		public DCSurfingPoint(double guessFactor, long recordedTime) {
			this.guessFactor = guessFactor;
			this.recordedTime = recordedTime;
		}
		
		public double deviationSum(double mu) {
			return Math.pow(guessFactor - mu, 2);
		}
		
		public double kernel(double testPoint, double bandwidth, long currentTime) {
			double diff = (testPoint - guessFactor) / bandwidth;
			return RUtil.GAUSSIAN_COEFFICIENT * Math.exp(-0.5 * diff * diff);
		}
	}

}
