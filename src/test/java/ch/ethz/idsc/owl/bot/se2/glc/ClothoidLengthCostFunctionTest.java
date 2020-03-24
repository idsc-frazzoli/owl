// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class ClothoidLengthCostFunctionTest extends TestCase {
  public void testSimple() {
    ClothoidLengthCostFunction clothoidLengthCostFunction = new ClothoidLengthCostFunction(s -> true);
    Scalar scalar = clothoidLengthCostFunction.apply(Tensors.vector(1, 2, 3));
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(3.3833822797708275));
  }

  public void testUnits() {
    ClothoidLengthCostFunction clothoidLengthCostFunction = new ClothoidLengthCostFunction(s -> true);
    Scalar scalar = clothoidLengthCostFunction.apply(Tensors.fromString("{1[m], 2[m], 3}"));
    Tolerance.CHOP.requireClose(scalar, Quantity.of(3.3833822797708275, "m"));
  }

  public void testInfinite() {
    {
      ClothoidLengthCostFunction clothoidLengthCostFunction = new ClothoidLengthCostFunction(s -> false);
      Scalar scalar = clothoidLengthCostFunction.apply(Tensors.vector(1, 2, 3));
      assertEquals(DoubleScalar.POSITIVE_INFINITY, scalar);
    }
    {
      ClothoidLengthCostFunction clothoidLengthCostFunction = new ClothoidLengthCostFunction(s -> false);
      Scalar scalar = clothoidLengthCostFunction.apply(Tensors.fromString("{1[m], 2[m], 3}"));
      assertEquals(Quantity.of(DoubleScalar.POSITIVE_INFINITY, "m"), scalar);
    }
  }
}
