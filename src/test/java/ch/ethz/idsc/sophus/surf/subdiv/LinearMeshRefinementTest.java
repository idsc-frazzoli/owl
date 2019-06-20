// code by jph
package ch.ethz.idsc.sophus.surf.subdiv;

import junit.framework.TestCase;

public class LinearMeshRefinementTest extends TestCase {
  public void testFailNull() {
    try {
      LinearMeshRefinement.of(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
