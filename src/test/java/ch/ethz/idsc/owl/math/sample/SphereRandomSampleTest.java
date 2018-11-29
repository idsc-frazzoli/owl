// code by jph
package ch.ethz.idsc.owl.math.sample;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class SphereRandomSampleTest extends TestCase {
  public void testSimple() {
    Tensor center = Tensors.vector(10, 20, 30, 40);
    Scalar radius = RealScalar.of(2);
    RandomSampleInterface rsi = SphereRandomSample.of(center, radius);
    Tensor vector = rsi.randomSample().subtract(center);
    assertTrue(Scalars.lessEquals(Norm._2.ofVector(vector), radius));
  }

  public void test1D() {
    Tensor center = Tensors.vector(15);
    Scalar radius = RealScalar.of(3);
    RandomSampleInterface randomSampleInterface = SphereRandomSample.of(center, radius);
    for (int index = 0; index < 100; ++index) {
      Tensor tensor = randomSampleInterface.randomSample();
      VectorQ.requireLength(tensor, 1);
      Clip.function(12, 18).requireInside(tensor.Get(0));
    }
  }

  public void test2D() {
    Tensor center = Tensors.vector(10, 20);
    Scalar radius = RealScalar.of(2);
    RandomSampleInterface rsi = SphereRandomSample.of(center, radius);
    assertTrue(rsi instanceof CircleRandomSample);
  }

  public void test3DZeroRadius() {
    Tensor center = Tensors.vector(10, 20, 3);
    Scalar radius = RealScalar.of(0.0);
    RandomSampleInterface randomSampleInterface = SphereRandomSample.of(center, radius);
    assertTrue(randomSampleInterface instanceof ConstantRandomSample);
    assertEquals(randomSampleInterface.randomSample(), center);
  }

  public void testQuantity() {
    RandomSampleInterface randomSampleInterface = //
        SphereRandomSample.of(Tensors.fromString("{10[m],20[m],-5[m]}"), Quantity.of(2, "m"));
    Tensor tensor = randomSampleInterface.randomSample();
    ScalarUnaryOperator scalarUnaryOperator = QuantityMagnitude.SI().in("m");
    tensor.map(scalarUnaryOperator);
  }

  public void testCenterEmptyFail() {
    try {
      SphereRandomSample.of(Tensors.empty(), Quantity.of(2, "m"));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testRadiusNegative2Fail() {
    try {
      SphereRandomSample.of(Tensors.vector(1, 2), RealScalar.of(-1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testRadiusNegative3Fail() {
    try {
      SphereRandomSample.of(Tensors.vector(1, 2, 3), RealScalar.of(-1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testCenterScalarFail() {
    try {
      SphereRandomSample.of(RealScalar.ONE, RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testCenterScalarZeroFail() {
    try {
      SphereRandomSample.of(RealScalar.ONE, RealScalar.ZERO);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
