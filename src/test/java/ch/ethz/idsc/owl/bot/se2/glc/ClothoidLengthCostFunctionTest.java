// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class ClothoidLengthCostFunctionTest extends TestCase {
  public void testSimple() {
    ClothoidLengthCostFunction clothoidLengthCostFunction = new ClothoidLengthCostFunction(s -> true);
    Scalar scalar = clothoidLengthCostFunction.apply(Tensors.vector(1, 2, 3));
    Chop._03.requireClose(scalar, RealScalar.of(3.3943296317986507));
  }
}
