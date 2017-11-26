// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.DoubleScalar;
import junit.framework.TestCase;

public class DegreeTest extends TestCase {
  public void testSimple() {
    assertEquals(Degree.of(360), DoubleScalar.of(Math.PI * 2));
  }
}
