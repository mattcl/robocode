package rampancy.util.data.kdTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class KDNode<T> {
	int maxBucketSize;
	ArrayList<KDDataPoint<T>> bucket;
	KDDataPoint<T> value;
	KDNode<T> left;
	KDNode<T> right;
	int featureIndex;
	
	public KDNode(int maxBucketSize) {
		this(maxBucketSize, new ArrayList<KDDataPoint<T>>());
	}
	
	public KDNode(int maxBucketSize, List<KDDataPoint<T>> seedBucket) {
		this.value = null;
		this.maxBucketSize = maxBucketSize;
		this.left = null;
		this.right = null;
		this.featureIndex = -1;
		this.bucket = new ArrayList<KDDataPoint<T>>(seedBucket);
		if (this.bucket.size() > this.maxBucketSize) {
			splitBucket();
		}
	}
	
	public T getValue() {
		return this.value.value;
	}

	// this needs to handle the edge case where there was no left or right node created
	public void add(KDDataPoint<T> value) {
		if (this.bucket == null) {
			// This node has at least one child, there are no more values being inserted into the bucket
			if (value.features[featureIndex] < this.value.features[featureIndex]) {
				if (this.left == null) {
					this.left = new KDNode<T>(this.maxBucketSize);
				}
				this.left.add(value);
			} else {
				if (this.right == null) {
					this.right = new KDNode<T>(this.maxBucketSize);
				}
				this.right.add(value);
			}
		} else {
			this.bucket.add(value);
			if (this.bucket.size() > this.maxBucketSize) {
				splitBucket();
			}
		}
	}
	
	public void splitBucket() {
		this.featureIndex = getBestSplittingFeature();
		// sort on the best feature
		sortBucket(this.featureIndex);
		
		// find the index of the median element
		int median = findMedianIndex(this.featureIndex);
		
		// the median element becomes the value
		this.value = this.bucket.get(median);
		
		// make the left leaf
		if (median > 0) {
			this.left = new KDNode<T>(this.maxBucketSize, this.bucket.subList(0, median));
		}
		
		// make the right leaf
		if (median < this.bucket.size() - 1) {
			this.right = new KDNode<T>(this.maxBucketSize, this.bucket.subList(median + 1, this.bucket.size()));
		}
		
		// delete the current bucket
		this.bucket = null;
	}
	
	protected void sortBucket(final int feature) {
		Collections.sort(this.bucket, new Comparator<KDDataPoint<T>>() {
			public int compare(KDDataPoint<T> o1, KDDataPoint<T> o2) {
				if (o1.features[feature] > o2.features[feature]) {
					return 1;
				} else if (o1.features[feature] == o2.features[feature]) {
					return 0;
				}
				return -1;
			}
		});
	}

	public int findMedianIndex(int featureIndex) {
		int candidate = this.bucket.size() / 2;
		for (int i = candidate - 1; i >= 0; i--) {
			if (this.bucket.get(candidate).features[featureIndex] != this.bucket.get(i).features[featureIndex]) {
				break;
			}
			candidate = i;
		}
		return candidate;
	}
	
	public int getBestSplittingFeature() {
		// split the bucket
		// compute mu for each in list
		int numFeatures = this.bucket.get(0).features.length;
		double[] sums = new double[numFeatures];
		for(KDDataPoint<T> point : this.bucket) {
			for (int i = 0; i < numFeatures; i++) {
				sums[i] += point.features[i];
			}
		}
		
		for (int i = 0; i < numFeatures; i++) {
			sums[i] /= numFeatures;
		}
	
		double[] sigmas = new double[numFeatures];
		for(int i = 0; i < this.bucket.size(); i++) {
			KDDataPoint<T> point = this.bucket.get(i);
			for (int j = 0; j < numFeatures; j++) {
				double sigmaPoint = point.features[j] - sums[j];
				sigmas[j] += sigmaPoint * sigmaPoint;
			}
		}
		
		int best = 0;
		double max = 0;
		for (int i = 0; i < numFeatures; i++) {
			if (sigmas[i] > max) {
				best = i;
				max = sigmas[i];
			}
		}
		return best;
	}
}
