package rampancy.util.gun;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;

import rampancy.RampantRobot;
import rampancy.util.REnemyRobot;
import rampancy.util.RPoint;
import rampancy.util.RRobotState;
import rampancy.util.RUtil;
import rampancy.util.data.kdTree.KDPoint;
import rampancy.util.data.kdTree.KDTree;
import rampancy.util.wave.RBulletWave;
import robocode.util.Utils;

// This is only suited for 1v1 battles!
public class RDynamicClusteringGun extends RGun {

	public static final String NAME = "Dynamic clustering gun";
	public static final int BUCKET_SIZE = 9;
	public static final int MAX_TREE_SIZE = 1000;
	public static final int NUM_NEIGHBORS = 11;
	
	protected KDTree<DCGunPoint> tree; 
	public RDynamicClusteringGun() {
		super(NAME);
		tree = new KDTree<DCGunPoint>(BUCKET_SIZE);
	}
	
	public void update(RampantRobot reference, RBulletWave wave) {
		double factor1 = wave.getGuessFactorForLargest();
		double factor2 = wave.getGuessFactorForSmallest();
		double max = Math.max(factor1, factor2);
		double min = Math.min(factor1, factor2);
		DCGunPoint value = new DCGunPoint(min, max, reference.getTime());
		KDPoint<DCGunPoint> observation = new KDPoint<DCGunPoint>(value, getCoordinateForEnemyState(wave.getInitialTargetState()));
		tree.add(observation);
	}
	
	public void updateEndOfRound(RampantRobot reference) {
		int pointsToKeep = Math.min(MAX_TREE_SIZE, tree.size());
		reference.out.println("Rebalancing kd tree with " + pointsToKeep + " of " + tree.size() + " points");
		tree.rebalance(MAX_TREE_SIZE);
	}

	@Override
	public RFiringSolution getFiringSolution(RampantRobot reference, REnemyRobot enemy) {
		long time = reference.getTime();
		KDPoint<DCGunPoint> query = new KDPoint<DCGunPoint>(null, getCoordinateForEnemyState(enemy.getCurrentState()));
		ArrayList<KDPoint<DCGunPoint>> neighbors = tree.kNearestNeighbors(query, NUM_NEIGHBORS);
		if (neighbors.isEmpty()) {
			return new Solution(this, enemy, 1.95, enemy.getCurrentState().absoluteBearing);
		}
		
		double mu = 0;
		double minGuessFactor = Double.POSITIVE_INFINITY;
		double maxGuessFactor = Double.NEGATIVE_INFINITY;
		for (KDPoint<DCGunPoint> neighbor : neighbors) {
			if (neighbor.value.min < minGuessFactor) {
				minGuessFactor = neighbor.value.min;
			}
			
			if (neighbor.value.max > maxGuessFactor) {
				maxGuessFactor = neighbor.value.max;
			}
			
			mu += neighbor.value.sum;
		}
	
		double sigma = 0;
		for (KDPoint<DCGunPoint> neighbor : neighbors) {
			sigma += neighbor.value.deviationSum(mu);
		}
		sigma = Math.sqrt(1.0 / (neighbors.size() * 3) * sigma);
		double bandwidth = (1.06 * sigma) * Math.pow(neighbors.size() * 3, -1.0/5.0);

		double bestDensity = Double.NEGATIVE_INFINITY;
		double bestFactor = 0;
		ArrayList<RPoint> densities = new ArrayList<RPoint>();
		for (double factor = -1.0; factor <= 1.0; factor += 0.01) {
			double density = 0;
			for (KDPoint<DCGunPoint> neighbor : neighbors) {
				density += neighbor.value.kernel(factor, bandwidth, time);
			}
			density = (1.0 / (bandwidth * neighbors.size() * 3)) * density;
			densities.add(new RPoint(factor, density));
			if (density > bestDensity) {
				bestDensity = density;
				bestFactor = factor;
			}
		}

		RRobotState state = enemy.getCurrentState();
		double bulletPower = 1.95;
		if (this.stat.shotsFired > 50 && this.getHitPercentage() < 20) {
			bulletPower = 1.2;
		}
		double bulletVelocity = RUtil.computeBulletVelocity(bulletPower);
		double escapeAngleClockwise = RUtil.computePreciseMaxEscapeAngle(RampantRobot.getGlobalBattlefield(), reference.getCurrentState(), state, bulletVelocity, 1 * state.directionTraveling);
		double escapeAngleCounterClockwise = RUtil.computePreciseMaxEscapeAngle(RampantRobot.getGlobalBattlefield(), reference.getCurrentState(), state, bulletVelocity, -1 * state.directionTraveling);
		
		double offset = escapeAngleClockwise * bestFactor * state.directionTraveling;
		if (bestFactor < 0) {
			offset = escapeAngleCounterClockwise * bestFactor * state.directionTraveling;
		}
		double angle = Utils.normalAbsoluteAngle(enemy.getCurrentState().absoluteBearing + offset);
		Solution solution = new Solution(this, enemy, bulletPower, angle);
		solution.setDensities(densities);
		return solution;
	}
	
	protected double[] getCoordinateForEnemyState(RRobotState state) {
		double[] query = {
				RUtil.normalize(state.lateralVelocity),
				RUtil.normalize(state.advancingVelocity),
				RUtil.normalize(state.distance),
				RUtil.normalize(state.timeSinceDirectionChange),
				RUtil.normalize(state.timeSinceVelocityChange),
			};
		return query;
	}
	
	class DCGunPoint {
		public long recordedTime;
		public double min;
		public double max;
		public double mid; 
		public double sum;
		
		public DCGunPoint(double min, double max, long recordedTime) {
			this.min = min;
			this.max = max;
			this.mid = (max - min) / 2.0;
			this.sum = this.max + this.min + this.mid;
			this.recordedTime = recordedTime;
		}
		
		public double deviationSum(double mu) {
			return Math.pow(mid - mu, 2) + Math.pow(min - mu, 2) + Math.pow(max - mu, 2);
		}
		
		public double kernel(double testPoint, double bandwidth, long currentTime) {
			bandwidth = 0.2;
			double timeFactor = 1.0 / (1 + 0.005 * (currentTime - recordedTime));
			if (testPoint >= min && testPoint <= max) {
				double diff = (testPoint - mid) / bandwidth;
				return timeFactor * (RUtil.GAUSSIAN_COEFFICIENT * Math.exp(-0.5 * diff * diff) + RUtil.GAUSSIAN_COEFFICIENT);
			}
			double comparisonPoint = min;
			if (testPoint > max) {
				comparisonPoint = max;
			}
			double diff = (testPoint - comparisonPoint) / bandwidth;
			return timeFactor * 0.7 * RUtil.GAUSSIAN_COEFFICIENT * Math.exp(-0.5 * diff * diff);
		}
	}
	
	class Solution extends RFiringSolution {
	    double maxAngleForward;
	    double maxAngleBackward;
	    double xScalingFactor;
	    double yScalingFactor;
		public ArrayList<RPoint> densities;
		public Path2D.Double densityGraph;

		public Solution(RGun gun, REnemyRobot target, double power, double firingAngle) {
			super(gun, target, power, firingAngle);
		}
		
		public void setDensities(ArrayList<RPoint> densities) {
			this.densities = densities;
			double maxY = 0.0001;
			for (RPoint density : densities) {
				if (density.y > maxY) {
					maxY = density.y;
				}
			}		
			yScalingFactor = 250.0 / maxY;
			xScalingFactor = 200.0;
		
			densityGraph = new Path2D.Double();
			densityGraph.moveTo(densities.get(0).x * xScalingFactor + 400, 0);
			for (RPoint density : densities) {
				densityGraph.lineTo(density.x * xScalingFactor + 400, density.y * yScalingFactor);
			}
		}
		
		public void draw(Graphics2D g) {
			if (densities != null) {
				g.draw(densityGraph);
			}
		}
	}

}
