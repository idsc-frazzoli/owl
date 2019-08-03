// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import java.io.IOException;

import ch.ethz.idsc.sophus.crv.clothoid.Clothoid.Curvature;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid.Curve;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class ClothoidTest extends TestCase {
  public void testLength() throws ClassNotFoundException, IOException {
    Distribution distribution = NormalDistribution.standard();
    for (int count = 0; count < 100; ++count) {
      Tensor p = RandomVariate.of(distribution, 3);
      Tensor q = RandomVariate.of(distribution, 3);
      Curve curve = Serialization.copy(new Clothoid(p, q)).new Curve();
      Scalar length = Serialization.copy(curve).length();
      Scalar between = Norm._2.between(p.extract(0, 2), q.extract(0, 2));
      assertTrue(Scalars.lessEquals(between, length));
    }
  }

  public void testCurvature() throws ClassNotFoundException, IOException {
    Distribution distribution = NormalDistribution.standard();
    for (int count = 0; count < 100; ++count) {
      Tensor p = RandomVariate.of(distribution, 3);
      Tensor q = RandomVariate.of(distribution, 3);
      Curvature curvature = new Clothoid(p, q).new Curvature();
      Scalar head = Serialization.copy(curvature).head();
      assertTrue(head instanceof RealScalar);
    }
  }

  public void testLengthMid() {
    Distribution distribution = NormalDistribution.standard();
    for (int count = 0; count < 100; ++count) {
      Tensor p = RandomVariate.of(distribution, 3);
      Tensor q = RandomVariate.of(distribution, 3);
      Curve curve = new Clothoid(p, q).new Curve();
      Tensor m = curve.apply(RationalScalar.HALF);
      {
        Scalar l1 = new Clothoid(p, m).new Curve().length();
        Scalar l2 = new Clothoid(m, q).new Curve().length();
        Chop._02.requireClose(l1, l2);
      }
    }
  }

  public void testQuantity() {
    Clothoid clothoid = new Clothoid(Tensors.fromString("{1[m], 2[m], 3}"), Tensors.fromString("{7[m], -2[m], 4}"));
    Curve curve = clothoid.new Curve();
    Tensor tensor = curve.apply(RealScalar.of(0.3));
    Chop._08.requireClose(tensor, Tensors.fromString("{0.90418903396778[m], -0.39687882575701483[m], -0.40076838060546527}"));
    Chop._10.requireClose(curve.length(), Quantity.of(12.394047346728675, "m^1.0"));
    Curvature curvature = clothoid.new Curvature();
    Chop._12.requireClose(curvature.head(), curvature.apply(RealScalar.ZERO));
    Chop._10.requireClose(curvature.head(), Quantity.of(1.8439931156110911, "m^-1"));
    Chop._12.requireClose(curvature.tail(), curvature.apply(RealScalar.ONE));
    Chop._10.requireClose(curvature.tail(), Quantity.of(-1.566643017498476, "m^-1"));
  }
}
