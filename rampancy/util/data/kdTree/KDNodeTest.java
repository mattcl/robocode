package rampancy.util.data.kdTree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

public class KDNodeTest {
	KDNode<String> node;
	
	protected ArrayList<KDDataPoint<String> > getSeed(int length) {
		ArrayList<KDDataPoint<String> > seed = new ArrayList<KDDataPoint<String> >();
		for (int i = 0; i < length; i++) {
			double[] features = {1, 2 + i, 3, 4};
			seed.add(new KDDataPoint<String>("SeedData" + i, features));
		}
		return seed;
	}
	
	@Test
	public void testKDNode() {
		this.node = new KDNode<String>(10);
		assertNull(this.node.value);
		assertNull(this.node.left);
		assertNull(this.node.right);
		assertEquals(10, this.node.maxBucketSize);
		assertTrue(this.node.bucket.isEmpty());
		assertEquals(-1, this.node.featureIndex);
	}

	@Test
	public void testKDNodeWithSeed() {
		ArrayList<KDDataPoint<String> > seed = getSeed(9);
		this.node = new KDNode<String>(10, seed);
		assertNull(this.node.value);
		assertNull(this.node.left);
		assertNull(this.node.right);
		assertEquals(10, this.node.maxBucketSize);
		assertEquals(-1, this.node.featureIndex);
		assertEquals(seed.size(), this.node.bucket.size());
		for (int i = 0; i < seed.size(); i++) {
			assertSame(seed.get(i), this.node.bucket.get(i));
		}
	}

	@Test
	public void testAdd() {
		ArrayList<KDDataPoint<String> > seed = getSeed(9);
		this.node = new KDNode<String>(10, seed);
		double[] features = {1, 2, 3, 4};
		KDDataPoint<String> newElem = new KDDataPoint<String>("hello", features);
		this.node.add(newElem);
		
		assertEquals(10, this.node.bucket.size());
	
		// trigger split
		KDDataPoint<String> newElem2 = new KDDataPoint<String>("hello", features);
		this.node.add(newElem2);
		assertNull(this.node.bucket);
		assertEquals(1, this.node.featureIndex);
	}

	@Test
	public void testAddToSplitNodeAddsToLeaf() {
		ArrayList<KDDataPoint<String> > seed = getSeed(11);
		this.node = new KDNode<String>(10, seed);
		double[] features = {1, 2, 3, 4};
		KDDataPoint<String> newElem = new KDDataPoint<String>("hello", features);
		this.node.add(newElem);
		assertEquals(newElem, this.node.left.bucket.get(this.node.left.bucket.size() - 1));
		
		double[] features2 = {1, 100, 3, 4};
		KDDataPoint<String> newElem2 = new KDDataPoint<String>("goodbye", features2);
		this.node.add(newElem2);
		assertEquals(newElem2, this.node.right.bucket.get(this.node.right.bucket.size() - 1));
	}
	
	@Test
	public void testAddToSplitNodeCreatesLeafIfItDoesNotExist() {
		ArrayList<KDDataPoint<String> > seed = getSeed(11);
		this.node = new KDNode<String>(10, seed);
		this.node.left = null;
		double[] features = {1, 2, 3, 4};
		KDDataPoint<String> newElem = new KDDataPoint<String>("hello", features);
		this.node.add(newElem);
		assertNotNull(this.node.left);
		assertEquals(newElem, this.node.left.bucket.get(this.node.left.bucket.size() - 1));
	
		this.node.right = null;
		double[] features2 = {1, 100, 3, 4};
		KDDataPoint<String> newElem2 = new KDDataPoint<String>("goodbye", features2);
		this.node.add(newElem2);
		assertNotNull(this.node.right);
		assertEquals(newElem2, this.node.right.bucket.get(this.node.right.bucket.size() - 1));
	}

	@Test
	public void testSplitBucket() {
		ArrayList<KDDataPoint<String> > seed = new ArrayList<KDDataPoint<String> >();
		for (int i = 0; i < 9; i++) {
			double[] features = {1 + i, 2, 3, 4};
			seed.add(new KDDataPoint<String>("SeedData" + i, features));
		}
		this.node = new KDNode<String>(10, seed);
		this.node.splitBucket();
		
		assertEquals(0, this.node.featureIndex);
		assertNull(this.node.bucket);
		assertSame(seed.get(4), this.node.value);
		
		KDNode<String> expectedLeft = new KDNode<String>(10, seed.subList(0, 4));
		KDNode<String> expectedRight = new KDNode<String>(10, seed.subList(5, seed.size()));
		
		assertEquals(expectedLeft.bucket, this.node.left.bucket);
		assertEquals(expectedRight.bucket, this.node.right.bucket);
	}
	
	@Test
	public void testKDTreeWithSeedLargerThanMaxBucketSplitsBucket() {
		ArrayList<KDDataPoint<String> > seed = getSeed(20);
		this.node = new KDNode<String>(10, seed);
		assertEquals(seed.get(10), this.node.value);
		
		KDNode<String> expectedLeft = new KDNode<String>(10, seed.subList(0, 10));
		KDNode<String> expectedRight = new KDNode<String>(10, seed.subList(11, seed.size()));
		assertEquals(expectedLeft.bucket, this.node.left.bucket);
		assertEquals(expectedRight.bucket, this.node.right.bucket);
	}

	@Test
	public void testFindMedianIndex() {
		ArrayList<KDDataPoint<String> > seed = new ArrayList<KDDataPoint<String> >();
		for (int i = 0; i < 9; i++) {
			double[] features = {1 + i, 2, 3, 4};
			seed.add(new KDDataPoint<String>("SeedData" + i, features));
		}
		this.node = new KDNode<String>(10, seed);
		assertEquals(4,  this.node.findMedianIndex(0));
		assertEquals(0,  this.node.findMedianIndex(1));
	}

	@Test
	public void testGetBestSplittingFeature() {
		ArrayList<KDDataPoint<String> > seed = new ArrayList<KDDataPoint<String> >();
		for (int i = 0; i < 9; i++) {
			double[] features = {1 + i, 2, 3, 4};
			seed.add(new KDDataPoint<String>("SeedData" + i, features));
		}
		this.node = new KDNode<String>(10, seed);
		assertEquals(0, this.node.getBestSplittingFeature());
	
		// select the wider standard deviation
		seed = new ArrayList<KDDataPoint<String> >();
		for (int i = 0; i < 9; i++) {
			double[] features = {1 + i, 2, 3 + i * 2, 4};
			seed.add(new KDDataPoint<String>("SeedData" + i, features));
		}
		this.node = new KDNode<String>(10, seed);
		assertEquals(2, this.node.getBestSplittingFeature());
	}

}
