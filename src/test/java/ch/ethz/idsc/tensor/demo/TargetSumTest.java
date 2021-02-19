// code by jph
package ch.ethz.idsc.tensor.demo;

import junit.framework.TestCase;

public class TargetSumTest extends TestCase {
  public void testSimple() {
    int nums1[] = { 2, 5, 10, 4 }; // true
    assertTrue(TargetSum.check(nums1, 0));
    assertTrue(TargetSum.check(nums1, 11));
    assertTrue(TargetSum.check(nums1, 12));
    assertTrue(TargetSum.check(nums1, 19));
    assertFalse(TargetSum.check(nums1, 1));
    assertFalse(TargetSum.check(nums1, 3));
    assertFalse(TargetSum.check(nums1, 8));
  }

  public void testDuplicates() {
    int nums1[] = { 2, 5, 10, 2 }; // true
    assertTrue(TargetSum.check(nums1, 0));
    assertTrue(TargetSum.check(nums1, 9));
    assertTrue(TargetSum.check(nums1, 12));
    assertTrue(TargetSum.check(nums1, 19));
    assertFalse(TargetSum.check(nums1, 1));
    assertFalse(TargetSum.check(nums1, 3));
    assertFalse(TargetSum.check(nums1, 8));
  }
}
