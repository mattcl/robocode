package rampancy.util.data.kdTree;

import java.util.ArrayList;
import java.util.List;

public class KDTree<T> {
	protected int maxBucketSize;
	protected ArrayList<KDDataPoint<T>> dataPoints;
	protected KDNode<T> root;

	public KDTree(int maxBucketSize) {
		this.dataPoints = new ArrayList<KDDataPoint<T>>();
		this.root = null;
	}
	
	public void add(KDDataPoint<T> value) {
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
	
	public List<KDDataPoint<T>> nearstNeighbors(KDDataPoint<T> point, int numNeighbors) {
		ArrayList<KDNode<T>> path = pathToNearestNeighbor(point);
		ArrayList<KDDataPoint<T>> neighbors = new ArrayList<KDDataPoint<T>>();
		for (int i = path.size() - 1; i >= 0; i++) {
			KDNode<T> current = path.get(i);
			if (current.bucket != null) {
				neighbors.addAll(current.bucket);
			} else {
				neighbors.add(current.value);
			}
		}
		return neighbors;
	}
	
	protected ArrayList<KDNode<T>> pathToNearestNeighbor(KDDataPoint<T> point) {
		ArrayList<KDNode<T>> path = new ArrayList<KDNode<T>>();
		KDNode<T> current = this.root;
		while (current != null) {
			path.add(current);
		
			// the node has not split yet, we're done
			if (current.value == null) {
				break;
			}
			
			if (point.features[current.featureIndex] < current.value.features[current.featureIndex]) {
				current = current.left;
			} else {
				current = current.right;
			}
		}
		return path;
	}
}

class NNSearch<T> {
	int numNeighbors;
	KDDataPoint<T> target;
	ArrayList<KDNode<T>> path;
	ArrayList<KDDataPoint<T>> candidates;
	double currentBest;
	double currentWorst;
	
	public NNSearch(int numNeighbors, KDDataPoint<T> target) {
		this.numNeighbors = numNeighbors;
		this.target = target;
		this.path = new ArrayList<KDNode<T>>();
		this.candidates = new ArrayList<KDDataPoint<T>>();
		this.currentBest = -1;
		this.currentWorst = -1;
	}
}