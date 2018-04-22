// code by jph
package ch.ethz.idsc.owl.glc.std;

import junit.framework.TestCase;

public class EmptyPlannerConstraintTest extends TestCase {
  public void testSimple() {
    assertTrue(EmptyPlannerConstraint.INSTANCE.isSatisfied(null, null, null));
  }
}
