// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.qty.Degree;
import junit.framework.TestCase;

public class GokartShadowPlanning0DemoTest extends TestCase {
  public void testSimple() {
    assertEquals(RealScalar.of(Math.PI / 10), Degree.of(18));
  }
}
