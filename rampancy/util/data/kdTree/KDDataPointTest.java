package rampancy.util.data.kdTree;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class KDDataPointTest {
	KDDataPoint<String> point;

	@Test
	public void testDistanceTo() {
		double[] features = {1, 3, 4};
		this.point = new KDDataPoint<String>("testPoint", features);
		
		KDDataPoint<String> target = new KDDataPoint<String>("target", features);
		assertEquals(0.0, this.point.distanceTo(target), 0.0001);
		
		double[] features2 = {2, -3, 4};
		target = new KDDataPoint<String>("target", features2);
		double expected = Math.pow(1 - 2, 2) + Math.pow(3 - -3, 2) + Math.pow(4 - 4, 2);
		assertEquals(expected, this.point.distanceTo(target), 0.0001);
		assertEquals(expected, target.distanceTo(this.point), 0.0001);
	}
}
