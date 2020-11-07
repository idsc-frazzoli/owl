// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import junit.framework.TestCase;

public class EmptyObstacleConstraintTest extends TestCase {
  public void testSimple() {
    assertTrue(EmptyPlannerConstraint.INSTANCE.isSatisfied(null, null, null));
  }
}
