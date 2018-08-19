// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class ArrowheadTest extends TestCase {
  public void testExact() {
    assertTrue(ExactScalarQ.all(Arrowhead.of(RealScalar.ONE)));
  }

  public void testLength() {
    assertEquals(Arrowhead.of(6).length(), 3);
  }
}
