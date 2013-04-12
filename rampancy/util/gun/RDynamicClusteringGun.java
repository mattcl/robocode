package rampancy.util.gun;

import java.util.ArrayList;
import java.util.HashMap;

import rampancy.RampantRobot;
import rampancy.util.REnemyRobot;
import rampancy.util.RRobotState;
import rampancy.util.RUtil;
import rampancy.util.data.kdTree.KDPoint;
import rampancy.util.data.kdTree.KDTree;
import rampancy.util.wave.RBulletWave;

public class RDynamicClusteringGun extends RGun {

	public static final String NAME = "Dynamic clustering gun";
	public static final int BUCKET_SIZE = 9;
	
	protected HashMap<String, KDTree<DCGunPoint>> kdTrees;
	
	public RDynamicClusteringGun() {
		super(NAME);
		kdTrees = new HashMap<String, KDTree<DCGunPoint>>();
	}
	
	public void update(RampantRobot reference, RBulletWave wave) {
		REnemyRobot enemy = wave.getFiringSolution().target;
		double factor1 = wave.getGuessFactorForLargest();
		double factor2 = wave.getGuessFactorForSmallest();
		double max = Math.max(factor1, factor2);
		double min = Math.min(factor1, factor2);
		DCGunPoint value = new DCGunPoint(new double[]{min, max});
		KDPoint<DCGunPoint> observation = new KDPoint<DCGunPoint>(value, getCoordinateForEnemyState(wave.getInitialState()));
		getTreeForEnemy(enemy).add(observation);
	}

	@Override
	public RFiringSolution getFiringSolution(RampantRobot reference, REnemyRobot enemy) {
		KDTree<DCGunPoint> tree = getTreeForEnemy(enemy);
		KDPoint<DCGunPoint> query = new KDPoint<DCGunPoint>(null, getCoordinateForEnemyState(enemy.getCurrentState()));
		ArrayList<KDPoint<DCGunPoint>> neighbors = tree.kNearestNeighbors(query, 15);
	
		double angle = enemy.getCurrentState().absoluteBearing;
		return new Solution(this, enemy, 1.95, angle);
	}
	
	protected KDTree<DCGunPoint> getTreeForEnemy(REnemyRobot enemy) {
		if (!kdTrees.containsKey(enemy.getName())) {
			kdTrees.put(enemy.getName(), new KDTree<DCGunPoint>(BUCKET_SIZE));
		}
		return kdTrees.get(enemy.getName());
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
		double[] guessFactors;
		
		public DCGunPoint(double[] guessFactors) {
			this.guessFactors = guessFactors;
		}
	}
	
	class Solution extends RFiringSolution {

		public Solution(RGun gun, REnemyRobot target, double power, double firingAngle) {
			super(gun, target, power, firingAngle);
		}
	}

}
