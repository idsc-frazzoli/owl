// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class Se2ShiftCostFunctionTest extends TestCase {
  public void testSerializable() throws Exception {
    CostFunction costFunction = new Se2ShiftCostFunction(Quantity.of(100, "CHF"));
    Serialization.copy(costFunction);
  }
}
