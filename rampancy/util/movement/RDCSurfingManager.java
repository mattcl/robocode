package rampancy.util.movement;

import java.util.ArrayList;

import rampancy.RampantRobot;
import rampancy.util.RPoint;
import rampancy.util.RRobotState;
import rampancy.util.RUtil;
import rampancy.util.data.kdTree.KDPoint;
import rampancy.util.data.kdTree.KDTree;
import rampancy.util.wave.REnemyWave;
import robocode.util.Utils;

public class RDCSurfingManager implements RMovementManager {
	public static final int BUCKET_SIZE = 10;
	public static final int NUM_NEIGHBORS = 13;
	protected KDTree<DCSurfingPoint> tree;
	
	public RDCSurfingManager() {
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
                RUtil.normalize(state.lateralVelocity),
                RUtil.normalize(state.advancingVelocity),
                RUtil.normalize(state.distance),
                RUtil.normalize(state.timeSinceDirectionChange),
                RUtil.normalize(state.timeSinceVelocityChange),
                RUtil.normalize(state.distanceFromWallCategory)
            };
        return query;
    }

	@Override
	public void updateEndOfRound(RampantRobot reference) {
	    tree.rebalance();
	}
	
	@Override
	public RMovementChoice getMovementChoice(RampantRobot reference) {
		return null;
	}

	@Override
	public RMovementChoice getMovementChoice(RampantRobot reference, REnemyWave wave) {
	    double escapeAngleClockwise = wave.getEscapeAngleClockwise();
	    double escapeAngleCounterClockwise = wave.getEscapeAngleCounterClockwise();
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
		long time = reference.getTime();
		KDPoint<DCSurfingPoint> query = new KDPoint<DCSurfingPoint>(null, getCoordinateForState(wave.getInitialTargetState()));
		ArrayList<KDPoint<DCSurfingPoint>> neighbors = tree.kNearestNeighbors(query, NUM_NEIGHBORS);
		if (neighbors.isEmpty()) {
		    return null; // TODO: other movement
		}
		
		// determine the absB for this guess factor
        double mu = 0;
		for (KDPoint<DCSurfingPoint> neighbor : neighbors) {
			mu += neighbor.value.guessFactor;
		}
	
		double sigma = 0;
		for (KDPoint<DCSurfingPoint> neighbor : neighbors) {
			sigma += neighbor.value.deviationSum(mu);
		}
		sigma = Math.sqrt(sigma / neighbors.size());
		double bandwidth = (1.06 * sigma) * Math.pow(neighbors.size(), -1.0/5.0);
		reference.out.println(bandwidth);
		double bestDensity = Double.POSITIVE_INFINITY;
		double desiredGuessFactor = 0;
		ArrayList<RPoint> densities = new ArrayList<RPoint>();
		for (double factor = -1.0; factor <= 1.0; factor += 0.05) {
			double density = 0;
			for (KDPoint<DCSurfingPoint> neighbor : neighbors) {
				density += neighbor.value.kernel(factor, bandwidth, time);
			}
			density = (1.0 / (bandwidth * neighbors.size())) * density;
			int guessFactorDirection = RUtil.nonZeroSign(factor);
			int realDirection = guessFactorDirection * wave.getInitialTargetState().directionTraveling;
			double escapeAngle = escapeAngleClockwise;
			if (realDirection < 0) {
			   escapeAngle = escapeAngleCounterClockwise; 
			}
			double offset = Utils.normalRelativeAngle(factor * escapeAngle);
			densities.add(new RPoint(offset, density));
			if (density < bestDensity) {
				bestDensity = density;
				desiredGuessFactor = factor;
			}
		}
		
		wave.setDangerMap(densities);
	
		double currentAbsBFromOrigin = wave.getOrigin().computeAbsoluteBearingTo(reference.getCurrentState().location);
		double currentGuessFactor = wave.getGuessFactor(currentAbsBFromOrigin);
		
		/*
		 * if desiregGuessFactor - currentGuessFactor > 0, we need to move towards that guess factor
		 *    if initial direction is 1, then we need to move clockwise
		 */
	    double orbitAngleClockwise = RUtil.computeOrbitAngle(RampantRobot.getGlobalBattlefield(), wave.getOrigin(), reference.getCurrentState().location, 0, 1);
	    double orbitAngleCounterClockwise = RUtil.computeOrbitAngle(RampantRobot.getGlobalBattlefield(), wave.getOrigin(), reference.getCurrentState().location, 0, -1);
	   
	    double guessFactorDiff = currentGuessFactor - desiredGuessFactor;
	    int guessFactorDirection = RUtil.nonZeroSign(guessFactorDiff);
	    
		if (Math.abs(guessFactorDiff) < 0.05) {
    		return new RMovementChoice(orbitAngleClockwise, 0);
		} else if(guessFactorDirection * wave.getInitialTargetState().directionTraveling > 0) {
    		return new RMovementChoice(orbitAngleClockwise, 200);
		} else {
    		return new RMovementChoice(orbitAngleCounterClockwise, 200);
		}
	}
	
	protected RPoint getOrbitLocation() {
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
