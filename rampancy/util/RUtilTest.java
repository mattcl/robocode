package rampancy.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class RUtilTest {

	@Test
	public void testScaleToRange() {
		System.out.println(RUtil.scaleToRange(2.0/3.0, 1.0, 0.5, 1.0, 1.0));
	}

}
