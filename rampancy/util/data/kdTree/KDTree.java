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
		NNSearch<T> search = new NNSearch<T>(query);
		recursiveNearestNeighbor(this.root, search);
		return search.best;
	}
	
	// find nearest neighbor leaf
	// unwind recursion
	
	protected void recursiveNearestNeighbor(KDNode<T> root, NNSearch<T> search) {
		if (root == null) {
			return;
		}
	
		int featureIndex = root.featureIndex;
	
		// if the bucket hasn't split yet, so we're done here after finding the
		// best if it exists
		double distance = 0;
		if (featureIndex < 0) {
			for (KDPoint<T> point : root.bucket) {
				distance = point.distanceTo(search.query);
				if (distance < search.bestDistance) {
					search.bestDistance = distance;
					search.best = point;
				}
			}
			return;
		}
		
		distance = root.value.distanceTo(search.query);
		if (distance < search.bestDistance) {
			search.bestDistance = distance;
			search.best = root.value;
		}
		
		KDNode<T> opposite = null;
		if (search.query.features[featureIndex] < root.value.features[featureIndex]) {
			recursiveNearestNeighbor(root.left, search);
			opposite = root.right;
		} else {
			recursiveNearestNeighbor(root.right, search);
			opposite = root.left;
		}
		
		if (Math.abs(search.query.features[featureIndex] - root.value.features[featureIndex]) < search.bestDistance) {
			recursiveNearestNeighbor(opposite, search);
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