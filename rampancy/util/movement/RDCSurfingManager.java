package rampancy.util.movement;

import java.util.ArrayList;
import java.util.List;

import rampancy.RampantRobot;
import rampancy.util.RPoint;
import rampancy.util.RRobotState;
import rampancy.util.RUtil;
import rampancy.util.data.kdTree.KDPoint;
import rampancy.util.data.kdTree.KDTree;
import rampancy.util.wave.REnemyWave;
import robocode.Rules;
import robocode.util.Utils;

public class RDCSurfingManager implements RMovementManager {
	public static final int BUCKET_SIZE = 10;
	public static final int NUM_NEIGHBORS = 13;
	protected KDTree<DCSurfingPoint> tree;
	protected ROrbitManager orbitManager; // fallback on this
	
	public RDCSurfingManager() {
		tree = new KDTree<DCSurfingPoint>(BUCKET_SIZE);
		orbitManager = new ROrbitManager();
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
	    tree.rebalance();
	}
	
	@Override
	public RMovementChoice getMovementChoice(RampantRobot reference) {
		return null;
	}

	@Override
	public RMovementChoice getMovementChoice(RampantRobot reference, List<REnemyWave> waves) {
	    ArrayList<RPoint> totalDangerMap = null;
	    for (REnemyWave wave : waves) {
	        if (!wave.hasDangerMap()) {
	            ArrayList<RPoint> dangerMap = getDangerMapForWave(reference, wave);
	            if (dangerMap == null) {
	            	// No dangers yet, so just orbit
	                return orbitManager.getMovementChoice(reference, waves);
	            }
        		wave.setDangerMap(dangerMap);
	        }
            if (totalDangerMap == null) {
                totalDangerMap = new ArrayList<RPoint>(wave.getDangerMap());
            } else {
                for (int i = 0; i < totalDangerMap.size(); i++) {
                    totalDangerMap.get(i).y += wave.getDangerMap().get(i).y;
                }
            }
	    }
	    
	    REnemyWave wave = waves.get(0);
	
		double currentAbsBFromOrigin = wave.getOrigin().computeAbsoluteBearingTo(reference.getCurrentState().location);
		double currentGuessFactor = wave.getGuessFactor(currentAbsBFromOrigin);
		
		double desiredGuessFactor = 0;
		double lowestDanger = Double.POSITIVE_INFINITY;
		for (RPoint danger : wave.getDangerMap()) {
			if (danger.y < lowestDanger) {
				lowestDanger = danger.y;
				desiredGuessFactor = danger.x;
			}
		}
		
		/*
		 * if desiregGuessFactor - currentGuessFactor > 0, we need to move towards that guess factor
		 *    if initial direction is 1, then we need to move clockwise
		 */
	    double orbitAngleClockwise = RUtil.computeOrbitAngle(RampantRobot.getGlobalBattlefield(), wave.getOrigin(), reference.getCurrentState().location, 0, 1);
	    double orbitAngleCounterClockwise = RUtil.computeOrbitAngle(RampantRobot.getGlobalBattlefield(), wave.getOrigin(), reference.getCurrentState().location, 0, -1);
	    
	    if (desiredGuessFactor > 0 && wave.getInitialTargetState().directionTraveling > 0) {
	    	return new RMovementChoice(orbitAngleClockwise, 100);
	    }
	    
	    if (desiredGuessFactor > 0 && wave.getInitialTargetState().directionTraveling < 0) {
	    	return new RMovementChoice(orbitAngleCounterClockwise, 100);
	    }
	    
	    if (desiredGuessFactor < 0 && wave.getInitialTargetState().directionTraveling < 0) {
	    	return new RMovementChoice(orbitAngleClockwise, 100);
	    }
	    
	    if (desiredGuessFactor < 0 && wave.getInitialTargetState().directionTraveling > 0) {
	    	return new RMovementChoice(orbitAngleCounterClockwise, 100);
	    }
	    
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
	
	protected ArrayList<RPoint> getDangerMapForWave(RampantRobot reference, REnemyWave wave) {
	    double escapeAnglePositive = wave.getEscapeAnglePositive();
	    double escapeAngleNegative = wave.getEscapeAngleNegative();
	    
		long time = reference.getTime();
		KDPoint<DCSurfingPoint> query = new KDPoint<DCSurfingPoint>(null, getCoordinateForState(wave.getInitialTargetState()));
		ArrayList<KDPoint<DCSurfingPoint>> neighbors = tree.kNearestNeighbors(query, NUM_NEIGHBORS);
		if (neighbors.isEmpty()) {
		    return null;
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
		if (bandwidth == 0) {
			bandwidth = 0.5;
		}
		
		ArrayList<RPoint> densities = new ArrayList<RPoint>();
		for (double factor = -1.0; factor <= 1.0; factor += 0.05) {
			double density = 0;
			for (KDPoint<DCSurfingPoint> neighbor : neighbors) {
				density += neighbor.value.kernel(factor, bandwidth, time);
			}
			density = (1.0 / (bandwidth * neighbors.size())) * density;
			int guessFactorDirection = RUtil.nonZeroSign(factor);
			int realDirection = guessFactorDirection * wave.getInitialTargetState().directionTraveling;
			double escapeAngle = escapeAnglePositive;
			if (realDirection < 0) {
			   escapeAngle = escapeAngleNegative; 
			}
			double offset = Utils.normalRelativeAngle(factor * escapeAngle);
			densities.add(new RPoint(offset, density));
		}
		return densities;
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
