// code by jph
package ch.ethz.idsc.owl.glc.std;

import ch.ethz.idsc.owl.glc.adapter.EmptyObstacleConstraint;
import junit.framework.TestCase;

public class EmptyPlannerConstraintTest extends TestCase {
  public void testSimple() {
    assertTrue(EmptyObstacleConstraint.INSTANCE.isSatisfied(null, null, null));
  }
}
