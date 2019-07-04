// code by jph
package ch.ethz.idsc.owl.math.sample;

import java.util.Random;

import ch.ethz.idsc.sophus.hs.r3s2.R3S2Geodesic;
import ch.ethz.idsc.sophus.hs.sn.RotationMatrix3D;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.mat.OrthogonalMatrixQ;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class SphereRandomSampleTest extends TestCase {
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._2);
  private static final Random RANDOM = new Random();

  public void testSimple() {
    Tensor center = Tensors.vector(10, 20, 30, 40);
    Scalar radius = RealScalar.of(2);
    RandomSampleInterface rsi = SphereRandomSample.of(center, radius);
    Tensor vector = rsi.randomSample(RANDOM).subtract(center);
    assertTrue(Scalars.lessEquals(Norm._2.ofVector(vector), radius));
  }

  public void test1D() {
    Tensor center = Tensors.vector(15);
    Scalar radius = RealScalar.of(3);
    RandomSampleInterface randomSampleInterface = SphereRandomSample.of(center, radius);
    for (int index = 0; index < 100; ++index) {
      Tensor tensor = randomSampleInterface.randomSample(RANDOM);
      VectorQ.requireLength(tensor, 1);
      Clips.interval(12, 18).requireInside(tensor.Get(0));
    }
  }

  public void test2D() {
    Tensor center = Tensors.vector(10, 20);
    Scalar radius = RealScalar.of(2);
    RandomSampleInterface rsi = SphereRandomSample.of(center, radius);
    assertTrue(rsi instanceof DiskRandomSample);
  }

  public void test3DZeroRadius() {
    Tensor center = Tensors.vector(10, 20, 3);
    Scalar radius = RealScalar.of(0.0);
    RandomSampleInterface randomSampleInterface = SphereRandomSample.of(center, radius);
    assertTrue(randomSampleInterface instanceof ConstantRandomSample);
    assertEquals(randomSampleInterface.randomSample(RANDOM), center);
  }

  public void testQuantity() {
    RandomSampleInterface randomSampleInterface = //
        SphereRandomSample.of(Tensors.fromString("{10[m], 20[m], -5[m]}"), Quantity.of(2, "m"));
    Tensor tensor = randomSampleInterface.randomSample(RANDOM);
    ScalarUnaryOperator scalarUnaryOperator = QuantityMagnitude.SI().in("m");
    tensor.map(scalarUnaryOperator);
  }

  public void testR3S2Geodesic() {
    RandomSampleInterface randomSampleInterface = //
        SphereRandomSample.of(Tensors.vector(0, 0, 0), RealScalar.ONE);
    int fails = 0;
    for (int index = 0; index < 20; ++index) {
      Tensor pn = NORMALIZE.apply(randomSampleInterface.randomSample(RANDOM));
      Tensor qn = NORMALIZE.apply(randomSampleInterface.randomSample(RANDOM));
      Tensor p = Tensors.of(pn, pn);
      Tensor q = Tensors.of(qn, qn);
      try {
        Tensor split = R3S2Geodesic.INSTANCE.split(p, q, RationalScalar.HALF);
        Chop._08.requireClose(split.get(0), split.get(1));
      } catch (Exception exception) {
        ++fails;
      }
    }
    assertTrue(fails < 5);
  }

  public void testRotationMatrix3D() {
    for (int index = 0; index < 50; ++index) {
      RandomSampleInterface randomSampleInterface = //
          SphereRandomSample.of(Tensors.vector(0, 0, 0), RealScalar.ONE);
      Tensor p = NORMALIZE.apply(randomSampleInterface.randomSample(RANDOM));
      Tensor q = NORMALIZE.apply(randomSampleInterface.randomSample(RANDOM));
      Tensor tensor = RotationMatrix3D.of(p, q);
      Chop._10.requireClose(tensor.dot(p), q);
      assertTrue(OrthogonalMatrixQ.of(tensor, Chop._10));
    }
  }

  public void testLarge() {
    RandomSampleInterface randomSampleInterface = //
        SphereRandomSample.of(Array.zeros(SphereRandomSample.MAX_LENGTH), RealScalar.ONE);
    randomSampleInterface.randomSample(RANDOM);
  }

  public void testLargeFail() {
    try {
      SphereRandomSample.of(Array.zeros(SphereRandomSample.MAX_LENGTH + 1), RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
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
