// code by jph
package ch.ethz.idsc.sophus.math;

import java.util.Optional;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.VectorAngle;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class ArcTan2DTest extends TestCase {
  public void testZero() {
    assertEquals(ArcTan2D.of(Array.zeros(10)), RealScalar.ZERO);
  }

  public void testVectorXY() {
    assertEquals(ArcTan2D.of(Tensors.vector(-1, -2)), ArcTan.of(-1, -2));
    assertEquals(ArcTan2D.of(Tensors.vector(-1, -2, 3)), ArcTan.of(-1, -2));
  }

  public void testVectorAngle() {
    Distribution distribution = UniformDistribution.of(-1, 1);
    Tensor v = UnitVector.of(2, 0);
    for (int count = 0; count < 10; ++count) {
      Tensor u = RandomVariate.of(distribution, 2);
      Optional<Scalar> optional = VectorAngle.of(u, v);
      Scalar scalar = ArcTan2D.of(u);
      Chop._10.requireClose(scalar.abs(), optional.get());
    }
  }

  public void testComplex() {
    Scalar scalar = ArcTan2D.of(Tensors.fromString("{1 + I, 2 - 3*I}"));
    Scalar expect = ComplexScalar.of(-1.4808695768986575, -0.4023594781085251);
    Chop._12.requireClose(scalar, expect);
  }

  public void testVectorXYFail() {
    try {
      ArcTan2D.of(RealScalar.ZERO);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      ArcTan2D.of(Tensors.vector(1));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      ArcTan2D.of(Array.zeros(3, 3, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
