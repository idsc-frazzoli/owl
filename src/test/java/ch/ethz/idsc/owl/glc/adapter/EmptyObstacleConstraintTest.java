// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import junit.framework.TestCase;

public class EmptyObstacleConstraintTest extends TestCase {
  public void testSimple() {
    assertTrue(EmptyObstacleConstraint.INSTANCE.isSatisfied(null, null, null));
  }
}
