// code by jph
package ch.ethz.idsc.owl.bot.r2;

import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.HeuristicQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class R2NoiseCostFunctionTest extends TestCase {
  public void testSimple() {
    CostFunction costFunction = new R2NoiseCostFunction(RealScalar.of(.2));
    assertFalse(HeuristicQ.of(costFunction));
  }

  public void testSerializable() throws Exception {
    CostFunction costFunction = new R2NoiseCostFunction(RealScalar.of(-0.5));
    Serialization.copy(costFunction);
  }
}
