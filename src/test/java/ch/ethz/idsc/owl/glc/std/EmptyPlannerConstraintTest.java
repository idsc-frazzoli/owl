// code by jph
package ch.ethz.idsc.owl.glc.std;

import ch.ethz.idsc.owl.glc.adapter.EmptyPlannerConstraint;
import junit.framework.TestCase;

public class EmptyPlannerConstraintTest extends TestCase {
  public void testSimple() {
    assertTrue(EmptyPlannerConstraint.INSTANCE.isSatisfied(null, null, null));
  }
}
