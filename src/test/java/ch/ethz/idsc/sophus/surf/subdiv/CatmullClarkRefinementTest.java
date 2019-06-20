// code by jph
package ch.ethz.idsc.sophus.surf.subdiv;

import junit.framework.TestCase;

public class CatmullClarkRefinementTest extends TestCase {
  public void testFailNull() {
    try {
      CatmullClarkRefinement.of(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
