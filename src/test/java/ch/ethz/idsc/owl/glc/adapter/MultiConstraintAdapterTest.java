// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import junit.framework.TestCase;

public class MultiConstraintAdapterTest extends TestCase {
  public void testFailNull() {
    try {
      MultiConstraintAdapter.of(null);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
