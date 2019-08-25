// code by jph
package ch.ethz.idsc.sophus.hs.sn;

import ch.ethz.idsc.sophus.hs.s2.S2Geodesic;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class SnGeodesicTest extends TestCase {
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._2);

  public void testSimple() {
    Tensor p = UnitVector.of(3, 0);
    Tensor q = UnitVector.of(3, 1);
    Tensor split = SnGeodesic.INSTANCE.split(p, q, RationalScalar.HALF);
    assertEquals(Norm._2.of(split), RealScalar.ONE);
    assertEquals(split.Get(0), split.Get(1));
    assertTrue(Scalars.isZero(split.Get(2)));
  }

  public void test2D() {
    ScalarTensorFunction scalarTensorFunction = //
        SnGeodesic.INSTANCE.curve(UnitVector.of(2, 0), UnitVector.of(2, 1));
    for (int n = 3; n < 20; ++n) {
      Tensor points = Subdivide.of(0, 4, n).map(scalarTensorFunction);
      Tensor circle = CirclePoints.of(n);
      Chop._12.requireClose(points.extract(0, n), circle);
    }
  }

  public void test4D() {
    ScalarTensorFunction scalarTensorFunction = //
        SnGeodesic.INSTANCE.curve(UnitVector.of(4, 0), UnitVector.of(4, 1));
    Tensor ZEROS = Array.zeros(2);
    for (int n = 3; n < 20; ++n) {
      Tensor points = Subdivide.of(0, 4, n).map(scalarTensorFunction);
      Tensor circle = Tensor.of(CirclePoints.of(n).stream().map(t -> Join.of(t, ZEROS)));
      Chop._12.requireClose(points.extract(0, n), circle);
    }
  }

  public void testRatio() {
    Tensor p = UnitVector.of(3, 0);
    Tensor q = UnitVector.of(3, 1);
    Tensor split2 = S2Geodesic.INSTANCE.split(p, q, RationalScalar.of(1, 3));
    Tensor splitn = SnGeodesic.INSTANCE.split(p, q, RationalScalar.of(1, 3));
    Chop._12.requireClose(split2, splitn);
  }

  public void testSame() {
    Tensor p = UnitVector.of(3, 2);
    Tensor q = UnitVector.of(3, 2);
    Tensor split = SnGeodesic.INSTANCE.split(p, q, RandomVariate.of(NormalDistribution.standard()));
    ExactTensorQ.require(split);
    assertEquals(split, p);
  }

  public void testOpposite() {
    Tensor p = UnitVector.of(3, 2);
    Tensor q = UnitVector.of(3, 2).negate();
    Scalar scalar = RandomVariate.of(NormalDistribution.standard());
    try {
      SnGeodesic.INSTANCE.split(p, q, scalar);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testComparison() {
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 10; ++index) {
      Tensor p = NORMALIZE.apply(RandomVariate.of(distribution, 3));
      Tensor q = NORMALIZE.apply(RandomVariate.of(distribution, 3));
      Scalar scalar = RandomVariate.of(distribution);
      Tensor split2 = S2Geodesic.INSTANCE.split(p, q, scalar);
      Tensor splitn = SnGeodesic.INSTANCE.split(p, q, scalar);
      Chop._10.requireClose(split2, splitn);
    }
  }

  public void testEndPoints() {
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 10; ++index) {
      Tensor p = NORMALIZE.apply(RandomVariate.of(distribution, 3));
      Tensor q = NORMALIZE.apply(RandomVariate.of(distribution, 3));
      Chop._14.requireClose(p, SnGeodesic.INSTANCE.split(p, q, RealScalar.ZERO));
      Tensor r = SnGeodesic.INSTANCE.split(p, q, RealScalar.ONE);
      Chop._12.requireClose(q, r);
      Chop._14.requireClose(Norm._2.of(r), RealScalar.ONE);
    }
  }

  public void testArticle() {
    Tensor p = Tensors.vector(1, 0, 0);
    Tensor q = Tensors.vector(0, 1 / Math.sqrt(5), 2 / Math.sqrt(5));
    Tensor tensor = SnGeodesic.INSTANCE.split(p, q, RealScalar.of(.4));
    // in sync with Mathematica
    Tensor expect = Tensors.vector(0.8090169943749473, 0.2628655560595668, 0.5257311121191336);
    Chop._12.requireClose(tensor, expect);
  }

  public void testFail() {
    try {
      SnGeodesic.INSTANCE.split(UnitVector.of(4, 0), UnitVector.of(3, 1), RealScalar.ZERO);
      fail();
    } catch (Exception exception) {
      // ---
    }
    SnGeodesic.INSTANCE.split(Tensors.vector(1, 2, 3), Tensors.vector(4, 5, 6), RationalScalar.HALF);
  }
}
