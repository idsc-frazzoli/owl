// code by jph
package ch.ethz.idsc.sophus.crv.spline;

import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGeodesic;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class GeodesicBSplineFunctionTest extends TestCase {
  public void testSimple() {
    Distribution distribution = NormalDistribution.standard();
    int n = 20;
    Tensor domain = Subdivide.of(0, n - 1, 100);
    for (int degree = 1; degree < 7; ++degree) {
      Tensor control = RandomVariate.of(distribution, n, 3);
      GeodesicBSplineFunction mapForward = //
          GeodesicBSplineFunction.of(Se2CoveringGeodesic.INSTANCE, degree, control);
      Tensor forward = domain.map(mapForward);
      GeodesicBSplineFunction mapReverse = //
          GeodesicBSplineFunction.of(Se2CoveringGeodesic.INSTANCE, degree, Reverse.of(control));
      Tensor reverse = Reverse.of(domain.map(mapReverse));
      assertTrue(Chop._10.close(forward, reverse));
    }
  }

  public void testBasisWeights1a() {
    GeodesicBSplineFunction func = GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, 1, UnitVector.of(3, 1));
    Tensor limitMask = Range.of(1, 2).map(func);
    ExactTensorQ.require(limitMask);
    assertEquals(limitMask, Tensors.fromString("{1}"));
  }

  public void testBasisWeights2() {
    GeodesicBSplineFunction func = GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, 2, UnitVector.of(5, 2));
    Tensor limitMask = Range.of(1, 4).map(func);
    ExactTensorQ.require(limitMask);
    assertEquals(limitMask, Tensors.fromString("{1/8, 3/4, 1/8}"));
  }

  public void testBasisWeights3a() {
    GeodesicBSplineFunction func = GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, 3, UnitVector.of(7, 3));
    Tensor limitMask = Range.of(2, 5).map(func);
    ExactTensorQ.require(limitMask);
    assertEquals(limitMask, Tensors.fromString("{1/6, 2/3, 1/6}"));
  }

  public void testBasisWeights3b() {
    GeodesicBSplineFunction func = GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, 3, UnitVector.of(5, 2));
    Tensor limitMask = Range.of(1, 4).map(func);
    ExactTensorQ.require(limitMask);
    assertEquals(limitMask, Tensors.fromString("{1/6, 2/3, 1/6}"));
  }

  public void testBasisWeights4() {
    GeodesicBSplineFunction func = GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, 4, UnitVector.of(9, 4));
    Tensor limitMask = Range.of(2, 7).map(func);
    assertEquals(Total.of(limitMask), RealScalar.ONE);
    ExactTensorQ.require(limitMask);
    assertEquals(limitMask, Tensors.fromString("{1/384, 19/96, 115/192, 19/96, 1/384}"));
  }

  public void testBasisWeights5a() {
    GeodesicBSplineFunction func = GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, 5, UnitVector.of(11, 5));
    Tensor limitMask = Range.of(3, 8).map(func);
    assertEquals(Total.of(limitMask), RealScalar.ONE);
    ExactTensorQ.require(limitMask);
    assertEquals(limitMask, Tensors.fromString("{1/120, 13/60, 11/20, 13/60, 1/120}"));
  }

  public void testBasisWeights5b() {
    GeodesicBSplineFunction func = GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, 5, UnitVector.of(9, 4));
    Tensor limitMask = Range.of(2, 7).map(func);
    assertEquals(Total.of(limitMask), RealScalar.ONE);
    ExactTensorQ.require(limitMask);
    assertEquals(limitMask, Tensors.fromString("{1/120, 13/60, 11/20, 13/60, 1/120}"));
  }

  public void testBasisWeights5c() {
    GeodesicBSplineFunction func = GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, 5, UnitVector.of(7, 3));
    Tensor limitMask = Range.of(1, 6).map(func);
    assertEquals(Total.of(limitMask), RealScalar.ONE);
    ExactTensorQ.require(limitMask);
    assertEquals(limitMask, Tensors.fromString("{1/120, 13/60, 11/20, 13/60, 1/120}"));
  }

  public void testNonUniformKnots() {
    Tensor control = RandomVariate.of(DiscreteUniformDistribution.of(2, 102), 10, 4);
    Tensor domain = RandomVariate.of(UniformDistribution.of(0, 9), 100);
    for (int degree = 1; degree < 6; ++degree) {
      Tensor result = domain.map(GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, degree, control));
      {
        Tensor vector = Range.of(5, 5 + control.length());
        Tensor compar = domain.map(RealScalar.of(5)::add) //
            .map(GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, degree, vector, control));
        Chop._11.requireClose(result, compar);
      }
      {
        Tensor vector = Range.of(5, 5 + control.length()).map(RealScalar.of(2)::multiply);
        Tensor compar = domain.map(RealScalar.of(5)::add).map(RealScalar.of(2)::multiply) //
            .map(GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, degree, vector, control));
        Chop._11.requireClose(result, compar);
      }
    }
  }

  public void testDegreeFail() {
    try {
      GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, -1, UnitVector.of(7, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testKnotsFail() {
    try {
      GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, 3, Range.of(0, 10), Range.of(0, 11));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testKnotsUnsortedFail() {
    try {
      GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, 3, Tensors.vector(3, 2, 1), Tensors.vector(1, 2, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
