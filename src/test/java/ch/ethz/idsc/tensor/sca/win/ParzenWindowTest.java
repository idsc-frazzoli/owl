// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import junit.framework.TestCase;

public class ParzenWindowTest extends TestCase {
  public void testSimple() {
    assertEquals(ParzenWindow.function().apply(RationalScalar.of(1, 10)), RationalScalar.of(101, 125));
    assertEquals(ParzenWindow.function().apply(RationalScalar.of(3, 10)), RationalScalar.of(16, 125));
  }

  public void testIsZero() {
    assertTrue(ParzenWindow.function().isZero());
  }
}
