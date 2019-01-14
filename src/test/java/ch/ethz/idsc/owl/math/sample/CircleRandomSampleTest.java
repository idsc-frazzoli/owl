// code by jph
package ch.ethz.idsc.owl.math.sample;

import java.util.Random;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class CircleRandomSampleTest extends TestCase {
  private static final Random RANDOM = new Random();

  public void testSimple() {
    CircleRandomSample circleRandomSample = new CircleRandomSample(Tensors.vector(0, 0), RealScalar.ONE);
    for (int count = 0; count < 100; ++count) {
      Tensor loc = circleRandomSample.randomSample(RANDOM);
      Scalar rad = Norm._2.ofVector(loc);
      assertTrue(Scalars.lessEquals(rad, RealScalar.ONE));
    }
  }

  public void testQuantity() {
    CircleRandomSample circleRandomSample = new CircleRandomSample(Tensors.fromString("{10[m],20[m]}"), Quantity.of(2, "m"));
    Tensor tensor = circleRandomSample.randomSample(RANDOM);
    ScalarUnaryOperator scalarUnaryOperator = QuantityMagnitude.SI().in("m");
    tensor.map(scalarUnaryOperator);
  }
}
