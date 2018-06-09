// code by jph
package ch.ethz.idsc.owl.bot.r2;

import ch.ethz.idsc.owl.glc.adapter.HeuristicQ;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class ImageCostFunctionTest extends TestCase {
  public void testSimple() {
    ImageCostFunction costFunction = //
        new ImageCostFunction(Tensors.fromString("{{1,2},{3,4}}"), Tensors.vector(10, 10), RealScalar.ZERO);
    assertFalse(HeuristicQ.of(costFunction));
    assertEquals(costFunction.flipYXTensorInterp.at(Tensors.vector(1, 1)), RealScalar.of(3));
    assertEquals(costFunction.flipYXTensorInterp.at(Tensors.vector(9, 9)), RealScalar.of(2));
    assertEquals(costFunction.flipYXTensorInterp.at(Tensors.vector(1, 9)), RealScalar.of(1));
    assertEquals(costFunction.flipYXTensorInterp.at(Tensors.vector(9, 1)), RealScalar.of(4));
    // TODO evaluate cost
  }

  public void testSerializable() throws Exception {
    CostFunction costFunction = //
        new ImageCostFunction(Tensors.fromString("{{1,2},{3,4}}"), Tensors.vector(10, 10), RealScalar.ZERO);
    Serialization.copy(costFunction);
  }
}
