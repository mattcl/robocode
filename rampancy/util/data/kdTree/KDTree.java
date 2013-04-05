package rampancy.util.data.kdTree;

import java.util.ArrayList;

public class KDTree<T> {
	protected int maxBucketSize;
	protected ArrayList<KDPoint<T>> dataPoints;
	protected KDNode<T> root;

	public KDTree(int maxBucketSize) {
		this.dataPoints = new ArrayList<KDPoint<T>>();
		this.root = null;
	}
	
	public void add(KDPoint<T> value) {
		dataPoints.add(value);
		if (this.root == null) {
			this.root = new KDNode<T>(this.maxBucketSize);
		}
		this.root.add(value);
	}

	// TODO: maybe determine if I have to cap the list
	public void rebalance() {
		this.root = null;
		this.root = new KDNode<T>(this.maxBucketSize, dataPoints);
	}

	public KDPoint<T> nearestNeighbor(KDPoint<T> query) {
		return null;
	}
	
	protected void recursiveNearestNeighbor(KDNode<T> root, NNSearch<T> search) {
		if (root.left == null && root.right == null) {
			for (KDPoint<T> point : root.bucket) {
				double currentDist = point.distanceTo(search.query);
				if (currentDist < search.bestDistance) {
					search.bestDistance = currentDist;
					search.best = point;
				}
			}
			return;
		}
		if (search.query.features[root.featureIndex] < root.value.features[root.featureIndex]) {
			
		} else {
			
		}
	}
}	

class NNSearch<T> {
	KDPoint<T> query;
	KDPoint<T> best;
	double bestDistance;
	
	public NNSearch(KDPoint<T> query) {
		this.query = query;
		this.best = null;
		this.bestDistance = Double.POSITIVE_INFINITY;
	}
}

class KNNSearch<T> {
	int numNeighbors;
	KDPoint<T> target;
	ArrayList<KDNode<T>> path;
	ArrayList<KDPoint<T>> candidates;
	double currentBest;
	double currentWorst;
	
	public KNNSearch(int numNeighbors, KDPoint<T> target) {
		this.numNeighbors = numNeighbors;
		this.target = target;
		this.path = new ArrayList<KDNode<T>>();
		this.candidates = new ArrayList<KDPoint<T>>();
		this.currentBest = -1;
		this.currentWorst = -1;
	}
}