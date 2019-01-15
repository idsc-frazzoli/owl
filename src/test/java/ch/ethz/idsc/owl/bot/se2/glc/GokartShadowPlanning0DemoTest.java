// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.owl.bot.util.DemoInterfaceHelper;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.qty.Degree;
import junit.framework.TestCase;

public class GokartShadowPlanning0DemoTest extends TestCase {
  public void testSimple() {
    DemoInterfaceHelper.brief(new GokartShadowPlanning0Demo());
  }

  public void testNumeric() {
    assertEquals(RealScalar.of(Math.PI / 10), Degree.of(18));
  }
}
