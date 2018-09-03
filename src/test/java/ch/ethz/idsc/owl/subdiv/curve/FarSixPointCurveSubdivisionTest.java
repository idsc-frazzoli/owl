// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class FarSixPointCurveSubdivisionTest extends TestCase {
  public void testP1() {
    FarSixPointCurveSubdivision spcs = new FarSixPointCurveSubdivision(RnGeodesic.INSTANCE);
    Tensor p1 = spcs.center(RealScalar.ONE, RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO);
    assertEquals(p1, RationalScalar.of(3, 256));
  }

  public void testP2() {
    FarSixPointCurveSubdivision spcs = new FarSixPointCurveSubdivision(RnGeodesic.INSTANCE);
    Tensor p1 = spcs.center(RealScalar.ZERO, RealScalar.ONE, RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO);
    assertEquals(p1, RationalScalar.of(-25, 256));
  }

  public void testP3() {
    FarSixPointCurveSubdivision spcs = new FarSixPointCurveSubdivision(RnGeodesic.INSTANCE);
    Tensor p1 = spcs.center(RealScalar.ZERO, RealScalar.ZERO, RealScalar.ONE, RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO);
    assertEquals(p1, RationalScalar.of(150, 256));
  }

  public void testP4() {
    FarSixPointCurveSubdivision spcs = new FarSixPointCurveSubdivision(RnGeodesic.INSTANCE);
    Tensor p1 = spcs.center(RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, RealScalar.ONE, RealScalar.ZERO, RealScalar.ZERO);
    assertEquals(p1, RationalScalar.of(150, 256));
  }

  public void testP5() {
    FarSixPointCurveSubdivision spcs = new FarSixPointCurveSubdivision(RnGeodesic.INSTANCE);
    Tensor p1 = spcs.center(RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, RealScalar.ONE, RealScalar.ZERO);
    assertEquals(p1, RationalScalar.of(-25, 256));
  }

  public void testP6() {
    FarSixPointCurveSubdivision spcs = new FarSixPointCurveSubdivision(RnGeodesic.INSTANCE);
    Tensor p1 = spcs.center(RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, RealScalar.ONE);
    assertEquals(p1, RationalScalar.of(3, 256));
  }

  public void testString5() {
    FarSixPointCurveSubdivision spcs = new FarSixPointCurveSubdivision(RnGeodesic.INSTANCE);
    Tensor string = spcs.string(Tensors.vector(1, 0, 0, 0, 0));
    Scalar scalar = string.Get(4);
    assertTrue(Scalars.isZero(scalar));
    assertEquals(string.length(), 5 + 4);
  }

  public void testString6() {
    FarSixPointCurveSubdivision spcs = new FarSixPointCurveSubdivision(RnGeodesic.INSTANCE);
    Tensor string = spcs.string(Tensors.vector(1, 0, 0, 0, 0, 0));
    Scalar scalar = string.Get(5);
    assertTrue(Scalars.nonZero(scalar));
    assertEquals(string.length(), 6 + 5);
  }
}
