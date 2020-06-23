// code by jph
package ch.ethz.idsc.owl.sim;

import junit.framework.TestCase;

public class TargetSumTest extends TestCase {
  public void testSimple() {
    int nums1[] = { 2, 5, 10, 4 }; // true
    assertTrue(TargetSum.check(nums1, 19));
    int nums2[] = { 2, 5, 10, 4 }; // true
    assertTrue(TargetSum.check(nums2, 17));
    int nums5[] = { 9 }; // true
    assertTrue(TargetSum.check(nums5, 0));
  }

  public void testForce() {
    int nums3[] = { 2, 5, 10, 4 }; // false
    assertFalse(TargetSum.check(nums3, 12));
    assertFalse(TargetSum.check(nums3, 7));
  }

  public void testCoverage() {
    int nums0[] = { 5, 1 };
    assertTrue(TargetSum.check(nums0, 1));
    int nums1[] = { 1, 5 };
    assertTrue(TargetSum.check(nums1, 5));
  }

  public void testJans() {
    int nums0[] = { 3, 2, 6, 8, 1 }; // true
    assertTrue(TargetSum.check(nums0, 10));
  }
}
