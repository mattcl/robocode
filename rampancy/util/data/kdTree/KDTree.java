package rampancy.util.data.kdTree;

import java.util.ArrayList;
import java.util.List;

public class KDTree<T> {
	protected int maxBucketSize;
	protected ArrayList<KDPoint<T>> dataPoints;
	protected KDNode<T> root;

	public KDTree(int maxBucketSize) {
		this.maxBucketSize = maxBucketSize;
		this.dataPoints = new ArrayList<KDPoint<T>>();
		this.root = null;
	}
	
	public KDTree(int maxBucketSize, List<KDPoint<T>> seed) {
		this.maxBucketSize = maxBucketSize;
		this.dataPoints = new ArrayList<KDPoint<T>>(seed);
		this.rebalance();
	}
	
	public void add(KDPoint<T> value) {
		this.dataPoints.add(value);
		if (this.root == null) {
			this.root = new KDNode<T>(this.maxBucketSize);
		}
		this.root.add(value);
	}

	public void rebalance() {
		this.root = null;
		this.root = new KDNode<T>(this.maxBucketSize, this.dataPoints);
	}
	
	public void rebalance(int pointCap) {
		if (this.dataPoints.size() > pointCap) {
			int diff = this.dataPoints.size() - pointCap;
			this.dataPoints = new ArrayList<KDPoint<T>>(this.dataPoints.subList(diff, this.dataPoints.size()));
		}
		rebalance();
	}

	public KDPoint<T> nearestNeighbor(KDPoint<T> query) {
		NNSearch<T> search = new NNSearch<T>(query);
		recursiveNearestNeighbor(this.root, search);
		return search.best;
	}
	
	public ArrayList<KDPoint<T>> kNearestNeighbors(KDPoint<T> query, int k) {
		KNNSearch<T> search = new KNNSearch<T>(query, k);
		recursiveKNearestNeighbor(this.root, search);
		return search.getResult();
	}
	
	// separate these for speed
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

	protected void recursiveKNearestNeighbor(KDNode<T> root, KNNSearch<T> search) {
		if (root == null) {
			return;
		}
	
		int featureIndex = root.featureIndex;
	
		double distance = 0;
		
		if (featureIndex < 0) {
			for (KDPoint<T> point : root.bucket) {
				distance = point.distanceTo(search.query);
				if (distance < search.worstDistance || search.candidates.size() < search.numNeighbors) {
					search.addCandidate(point, distance);
				}
			}
			return;
		}
		
		distance = root.value.distanceTo(search.query);
		if (distance < search.worstDistance || search.candidates.size() < search.numNeighbors) {
			search.addCandidate(root.value, distance);
		}
		
		KDNode<T> opposite = null;
		if (search.query.features[featureIndex] < root.value.features[featureIndex]) {
			recursiveKNearestNeighbor(root.left, search);
			opposite = root.right;
		} else {
			recursiveKNearestNeighbor(root.right, search);
			opposite = root.left;
		}
		
		if (Math.abs(search.query.features[featureIndex] - root.value.features[featureIndex]) < search.worstDistance) {
			recursiveKNearestNeighbor(opposite, search);
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
	KDPoint<T> query;
	ArrayList<KDPoint<T>> candidates;
	ArrayList<Double> distances;
	double bestDistance;
	double worstDistance;
	
	public KNNSearch(KDPoint<T> query, int numNeighbors) {
		this.numNeighbors = numNeighbors;
		this.query = query;
		this.candidates = new ArrayList<KDPoint<T>>();
		this.distances = new ArrayList<Double>();
		this.bestDistance = Double.POSITIVE_INFINITY;
		this.worstDistance = Double.POSITIVE_INFINITY;
	}
	
	public void addCandidate(KDPoint<T> point, double distance) {
		if (distance < this.bestDistance) {
			this.bestDistance = distance;
		}
		int size = candidates.size();
		int i = 0;
		for (; i < size; i++) {
			if (distance < this.distances.get(i)) {
				break;
			}
		}
		
		this.candidates.add(i, point);
		this.distances.add(i, distance);
	
		int worstIndex = Math.min(candidates.size(), numNeighbors) - 1;
		if (worstIndex >= 0) {
			this.worstDistance = distances.get(worstIndex);
		}
	}
	
	public ArrayList<KDPoint<T>> getResult() {
		if (this.candidates.size() > this.numNeighbors) {
			return new ArrayList<KDPoint<T>>(this.candidates.subList(0, this.numNeighbors));
		}
		return this.candidates;
	}
}