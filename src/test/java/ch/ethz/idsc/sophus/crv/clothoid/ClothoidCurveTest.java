// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class ClothoidCurveTest extends TestCase {
  private static final Unit METER = Unit.of("m");

  public static Tensor metric(Tensor vector) {
    return Tensors.of( //
        Quantity.of(vector.Get(0), METER), //
        Quantity.of(vector.Get(1), METER), //
        vector.Get(2));
  }

  public void testComparison() {
    Distribution distribution = NormalDistribution.of(0, 0.002);
    for (int count = 0; count < 1000; ++count) {
      Tensor p = RandomVariate.of(distribution, 3);
      Tensor q = RandomVariate.of(distribution, 3);
      Scalar lambda = RandomVariate.of(distribution);
      Tensor r1 = new ClothoidCurve1(p, q).apply(lambda);
      Tensor r2 = new ClothoidCurve2(p, q).apply(lambda);
      Tensor r3 = new ClothoidCurve3(p, q).apply(lambda);
      Chop._03.requireClose(r1, r2);
      Chop._03.requireClose(r1, r3);
    }
  }

  public void testProd() {
    Scalar z = ComplexScalar.of(2, 3);
    Scalar a = ComplexScalar.of(5, 11);
    Tensor vector = Tensors.vector(5, 11);
    Tensor tensor = ClothoidCurve.prod(z, vector);
    assertEquals(tensor, Tensors.vector(-23, 37));
    ExactTensorQ.require(tensor);
    Scalar compare = z.multiply(a);
    assertEquals(compare, ComplexScalar.of(-23, 37));
  }

  public void testDistinct() {
    Tensor p = Array.zeros(3);
    Tensor q = Tensors.vector(-3.7, 0.3, 3.142);
    Tensor m1 = Clothoid1.INSTANCE.curve(p, q).apply(RationalScalar.HALF);
    Tensor m2 = Clothoid2.INSTANCE.curve(p, q).apply(RationalScalar.HALF);
    Tensor m3 = Clothoid3.INSTANCE.curve(p, q).apply(RationalScalar.HALF);
    assertFalse(Chop._01.close(m1, m2));
    assertFalse(Chop._01.close(m1, m3));
    assertFalse(Chop._01.close(m2, m3));
  }
}
