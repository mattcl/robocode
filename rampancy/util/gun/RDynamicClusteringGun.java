package rampancy.util.gun;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import rampancy.RampantRobot;
import rampancy.util.REnemyRobot;
import rampancy.util.RMovementPath;
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
	public static final int NUM_NEIGHBORS = 9;
	public static final double GAUSSIAN_COEFFICIENT = 1.0 / Math.sqrt(2 * Math.PI);
	
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
		DCGunPoint value = new DCGunPoint(min, max);
		KDPoint<DCGunPoint> observation = new KDPoint<DCGunPoint>(value, getCoordinateForEnemyState(wave.getInitialState()));
		tree.add(observation);
	}
	
	public void updateEndOfRound(RampantRobot reference) {
		int pointsToKeep = Math.min(MAX_TREE_SIZE, tree.size());
		reference.out.println("Rebalancing kd tree with " + pointsToKeep + " of " + tree.size() + " points");
		tree.rebalance(MAX_TREE_SIZE);
	}

	@Override
	public RFiringSolution getFiringSolution(RampantRobot reference, REnemyRobot enemy) {
		KDPoint<DCGunPoint> query = new KDPoint<DCGunPoint>(null, getCoordinateForEnemyState(enemy.getCurrentState()));
		ArrayList<KDPoint<DCGunPoint>> neighbors = tree.kNearestNeighbors(query, NUM_NEIGHBORS);
		if (neighbors.isEmpty()) {
			return new Solution(this, enemy, 1.95, enemy.getCurrentState().absoluteBearing, null, null);
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
		sigma = Math.sqrt(1.0 / neighbors.size() * sigma);
		double bandwidth = (1.06 * sigma) * Math.pow(neighbors.size() * 3, -1.0/5.0);
	
		double bestDensity = Double.NEGATIVE_INFINITY;
		double bestFactor = 0;
		for (double factor = minGuessFactor; factor <= maxGuessFactor; factor += 0.01) {
			double density = 0;
			for (KDPoint<DCGunPoint> neighbor : neighbors) {
				density += neighbor.value.kernel(factor, bandwidth);
			}
			density = (1.0 / (bandwidth * neighbors.size() * 3)) * density;
			if (density > bestDensity) {
				bestDensity = density;
				bestFactor = factor;
			}
		}

		double bulletPower = 1.95;
		double bulletVelocity = RUtil.computeBulletVelocity(bulletPower);
		RMovementPath path1 = new RMovementPath(Color.green);
		RMovementPath path2 = new RMovementPath();
		double escapeAngleForward = RUtil.computePreciseMaxEscapeAngle(RampantRobot.getGlobalBattlefield(), reference.getCurrentState(), enemy.getCurrentState(), bulletVelocity, 1, path1);
		double escapeAngleBackward = RUtil.computePreciseMaxEscapeAngle(RampantRobot.getGlobalBattlefield(), reference.getCurrentState(), enemy.getCurrentState(), bulletVelocity, -1, path2);
		
		double offset = escapeAngleForward * bestFactor * enemy.getCurrentState().directionTraveling;
		if (bestFactor < 0) {
			offset = -escapeAngleBackward * bestFactor * enemy.getCurrentState().directionTraveling;
		}
		double angle = Utils.normalAbsoluteAngle(enemy.getCurrentState().absoluteBearing + offset);
		return new Solution(this, enemy, 1.95, angle, path1, path2);
	}
	
	protected double[] getCoordinateForEnemyState(RRobotState state) {
		double[] query = {
				RUtil.normalize(state.lateralVelocity),
				RUtil.normalize(state.advancingVelocity),
				RUtil.normalize(state.distance),
				RUtil.normalize(state.timeSinceDirectionChange),
				RUtil.normalize(state.timeSinceVelocityChange)
			};
		return query;
	}
	
	class DCGunPoint {
		public double min;
		public double max;
		public double mid; 
		public double sum;
		
		public DCGunPoint(double min, double max) {
			this.min = min;
			this.max = max;
			this.mid = (max - min) / 2.0;
			this.sum = this.max + this.min + this.mid;
		}
		
		public double deviationSum(double mu) {
			return Math.pow(mid - mu, 2) + Math.pow(min - mu, 2) + Math.pow(max - mu, 2);
		}
		
		public double kernel(double testPoint, double bandwidth) {
			if (testPoint >= min && testPoint <= max) {
				double diff = (testPoint - mid) / bandwidth;
				return GAUSSIAN_COEFFICIENT * Math.exp(-0.5 * diff * diff) + GAUSSIAN_COEFFICIENT;
			}
			double comparisonPoint = min;
			if (testPoint > max) {
				comparisonPoint = max;
			}
			double diff = (testPoint - comparisonPoint) / bandwidth;
			return GAUSSIAN_COEFFICIENT * Math.exp(-0.5 * diff * diff);
		}
	}
	
	class Solution extends RFiringSolution {
		RMovementPath path1;
		RMovementPath path2;

		public Solution(RGun gun, REnemyRobot target, double power, double firingAngle, RMovementPath path1, RMovementPath path2) {
			super(gun, target, power, firingAngle);
			this.path1 = path1;
			this.path2 = path2;
		}
		
		public void draw(Graphics2D g) {
			/*
			if (path1 != null) {
				path1.draw(g);
			}
			if (path2 != null) {
				path2.draw(g);
			}*/
		}
	}

}
