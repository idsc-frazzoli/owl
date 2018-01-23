// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.glc.adapter.GlcNodes;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class Se2ShiftCostFunctionTest extends TestCase {
  public void testSimple() {
    CostFunction costFunction = new Se2ShiftCostFunction(Quantity.of(100, "CHF"));
    GlcNode glcNode = GlcNodes.createRoot(new StateTime(Tensors.vector(1, 2), RealScalar.ONE), costFunction);
    Scalar scalar = costFunction.costIncrement(glcNode, null, null);
    assertEquals(scalar, Quantity.of(0, "CHF"));
  }

  public void testSerializable() throws Exception {
    CostFunction costFunction = new Se2ShiftCostFunction(Quantity.of(100, "CHF"));
    Serialization.copy(costFunction);
  }
}
