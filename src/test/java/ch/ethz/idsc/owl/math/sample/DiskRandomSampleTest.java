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

public class DiskRandomSampleTest extends TestCase {
  private static final Random RANDOM = new Random();

  public void testSimple() {
    DiskRandomSample diskRandomSample = new DiskRandomSample(Tensors.vector(0, 0), RealScalar.ONE);
    for (int count = 0; count < 100; ++count) {
      Tensor loc = diskRandomSample.randomSample(RANDOM);
      Scalar rad = Norm._2.ofVector(loc);
      assertTrue(Scalars.lessEquals(rad, RealScalar.ONE));
    }
  }

  public void testQuantity() {
    DiskRandomSample diskRandomSample = new DiskRandomSample(Tensors.fromString("{10[m],20[m]}"), Quantity.of(2, "m"));
    Tensor tensor = diskRandomSample.randomSample(RANDOM);
    ScalarUnaryOperator scalarUnaryOperator = QuantityMagnitude.SI().in("m");
    tensor.map(scalarUnaryOperator);
  }
}
