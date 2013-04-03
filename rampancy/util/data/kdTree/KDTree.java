package rampancy.util.data.kdTree;

import java.util.ArrayList;
import java.util.List;

public class KDTree<T> {
	protected int maxBucketSize;
	protected ArrayList<KDDataPoint<T> > dataPoints;
	protected KDNode<T> root;

	public KDTree(int maxBucketSize) {
		this.dataPoints = new ArrayList<KDDataPoint<T> >();
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
	
	public List<KDDataPoint<T> > nearstNeighbors(KDDataPoint<T> point, int numNeighbors) {
		ArrayList<KDDataPoint<T> > neighbors = new ArrayList<KDDataPoint<T> >();
		if (this.root != null) {
			
		}
		return neighbors;
	}
}