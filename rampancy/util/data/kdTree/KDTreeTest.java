package rampancy.util.data.kdTree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

public class KDTreeTest {
	
	KDTree<String> tree;
	
	protected void buildDefaultTree() {
		ArrayList<KDPoint<String>> seed = new ArrayList<KDPoint<String>>();
		
		seed.add(new KDPoint<String>("a", new double[]{12, 16}));
		seed.add(new KDPoint<String>("b", new double[]{15,  8}));
		seed.add(new KDPoint<String>("c", new double[]{ 5, 18}));
		seed.add(new KDPoint<String>("d", new double[]{18,  5}));
		seed.add(new KDPoint<String>("e", new double[]{16, 15}));
		seed.add(new KDPoint<String>("f", new double[]{ 2,  5}));
		seed.add(new KDPoint<String>("g", new double[]{ 7, 10}));
		seed.add(new KDPoint<String>("h", new double[]{ 8,  7}));
		seed.add(new KDPoint<String>("i", new double[]{ 5,  5}));
		seed.add(new KDPoint<String>("j", new double[]{19, 12}));
		seed.add(new KDPoint<String>("k", new double[]{10,  2}));
		
		this.tree = new KDTree<String>(3, seed);
	}

	@Test
	public void testNearestNeighbor() {
		buildDefaultTree();
		KDPoint<String> query = new KDPoint<String>("x", new double[]{13, 2});
		KDPoint<String> nearest = this.tree.nearestNeighbor(query);
		assertEquals("k", nearest.value);
		
		query = new KDPoint<String>("y", new double[]{10, 8});
		nearest = this.tree.nearestNeighbor(query);
		assertEquals("h", nearest.value);
	}
	
	@Test
	public void testKNearestNeighbors() {
		buildDefaultTree();
		KDPoint<String> query = new KDPoint<String>("x", new double[]{12, 9});
		ArrayList<KDPoint<String>> nearest = this.tree.kNearestNeighbors(query, 2);
		assertEquals(2, nearest.size());
		
		ArrayList<String> expected = new ArrayList<String>();
		expected.add("h");
		expected.add("b");
		
		assertTrue(expected.contains(nearest.get(0).value));
		assertTrue(expected.contains(nearest.get(1).value));
		
		nearest = this.tree.kNearestNeighbors(query, 3);
		assertEquals(3, nearest.size());
		
		expected.add("g");
		assertTrue(expected.contains(nearest.get(0).value));
		assertTrue(expected.contains(nearest.get(1).value));
		assertTrue(expected.contains(nearest.get(2).value));
		
		nearest = this.tree.kNearestNeighbors(query, 11);
		assertEquals(11, nearest.size());
	}
	
	@Test
	public void testRebalanceWithPruning() {
		buildDefaultTree();
		assertEquals(11, this.tree.dataPoints.size());
		assertEquals("a", this.tree.dataPoints.get(0).value);
		assertEquals("b", this.tree.dataPoints.get(1).value);
		
		this.tree.rebalance(9);
		assertEquals(9, this.tree.dataPoints.size());
		assertEquals("c", this.tree.dataPoints.get(0).value);
		assertEquals("d", this.tree.dataPoints.get(1).value);
	}
}
