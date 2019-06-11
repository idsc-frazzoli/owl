// code by jph
package ch.ethz.idsc.sophus.lie.rn;

import junit.framework.TestCase;

public class RnGroupTest extends TestCase {
  public void testFailNull() {
    try {
      RnGroup.INSTANCE.element(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
  // public void testFailMatrix() {
  // try {
  // RnGroup.INSTANCE.element(IdentityMatrix.of(3));
  // fail();
  // } catch (Exception exception) {
  // // ---
  // }
  // }
}
