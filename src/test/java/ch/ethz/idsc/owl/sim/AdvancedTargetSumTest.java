// code by jph
package ch.ethz.idsc.owl.sim;

import junit.framework.TestCase;

public class AdvancedTargetSumTest extends TestCase {
  public void testSimple() {
    int nums1[] = { 2, 5, 10, 4 }; // true
    assertTrue(AdvancedTargetSum.check(nums1, 19));
    int nums2[] = { 2, 5, 10, 4 }; // true
    assertTrue(AdvancedTargetSum.check(nums2, 17));
    int nums5[] = { 9 }; // true
    assertTrue(AdvancedTargetSum.check(nums5, 0));
  }

  public void testForce() {
    int nums3[] = { 2, 5, 10, 4 }; // false
    assertFalse(AdvancedTargetSum.check(nums3, 12));
    assertFalse(AdvancedTargetSum.check(nums3, 7));
  }

  public void testCoverage() {
    int nums0[] = { 5, 1 };
    assertTrue(AdvancedTargetSum.check(nums0, 1));
    int nums1[] = { 1, 5 };
    assertTrue(AdvancedTargetSum.check(nums1, 5));
  }

  public void testJans() {
    int nums0[] = { 3, 2, 6, 8, 1 }; // true
    assertTrue(AdvancedTargetSum.check(nums0, 10));
  }
}
