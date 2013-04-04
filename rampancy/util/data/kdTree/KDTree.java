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
	
	public List<KDDataPoint<T>> nearstNeighbors(double[] features, int numNeighbors) {
		ArrayList<KDNode<T>> path = pathToNearestNeighbor(features);
		ArrayList<KDDataPoint<T>> neighbors = new ArrayList<KDDataPoint<T>>();
		for (int i = path.size() - 1; i >= 0 && neighbors.size() < numNeighbors; i++) {
			KDNode<T> current = path.get(i);
			if (current.bucket != null) {
				neighbors.addAll(current.bucket);
			} else {
				
			}
		}
		return neighbors;
	}
	
	protected ArrayList<KDNode<T>> pathToNearestNeighbor(double[] features) {
		ArrayList<KDNode<T>> path = new ArrayList<KDNode<T>>();
		KDNode<T> current = this.root;
		while (current != null) {
			path.add(current);
		
			// the node has not split yet, we're done
			if (current.value == null) {
				break;
			}
			
			if (features[current.featureIndex] < current.value.features[current.featureIndex]) {
				current = current.left;
			} else {
				current = current.right;
			}
		}
		return path;
	}
}