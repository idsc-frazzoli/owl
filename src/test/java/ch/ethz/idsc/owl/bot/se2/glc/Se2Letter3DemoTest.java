// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.owl.bot.util.DemoInterfaceHelper;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.qty.Degree;
import junit.framework.TestCase;

public class Se2Letter3DemoTest extends TestCase {
  public void testSimple() {
    DemoInterfaceHelper.brief(new Se2Letter3Demo());
  }

  public void testDegree() {
    assertEquals(RealScalar.of(Math.PI / 6), Degree.of(180 / 6));
    assertEquals(RealScalar.of(Math.PI / 6), Degree.of(30));
  }
}
