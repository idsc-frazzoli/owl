// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class SixPointCurveSubdivisionTest extends TestCase {
  public void testP1() {
    SixPointCurveSubdivision spcs = new SixPointCurveSubdivision(RnGeodesic.INSTANCE);
    Tensor p1 = spcs.center(RealScalar.ONE, RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO);
    assertEquals(p1, RationalScalar.of(3, 256));
  }

  public void testP2() {
    SixPointCurveSubdivision spcs = new SixPointCurveSubdivision(RnGeodesic.INSTANCE);
    Tensor p1 = spcs.center(RealScalar.ZERO, RealScalar.ONE, RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO);
    assertEquals(p1, RationalScalar.of(-25, 256));
  }

  public void testP3() {
    SixPointCurveSubdivision spcs = new SixPointCurveSubdivision(RnGeodesic.INSTANCE);
    Tensor p1 = spcs.center(RealScalar.ZERO, RealScalar.ZERO, RealScalar.ONE, RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO);
    assertEquals(p1, RationalScalar.of(150, 256));
  }

  public void testP4() {
    SixPointCurveSubdivision spcs = new SixPointCurveSubdivision(RnGeodesic.INSTANCE);
    Tensor p1 = spcs.center(RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, RealScalar.ONE, RealScalar.ZERO, RealScalar.ZERO);
    assertEquals(p1, RationalScalar.of(150, 256));
  }

  public void testP5() {
    SixPointCurveSubdivision spcs = new SixPointCurveSubdivision(RnGeodesic.INSTANCE);
    Tensor p1 = spcs.center(RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, RealScalar.ONE, RealScalar.ZERO);
    assertEquals(p1, RationalScalar.of(-25, 256));
  }

  public void testP6() {
    SixPointCurveSubdivision spcs = new SixPointCurveSubdivision(RnGeodesic.INSTANCE);
    Tensor p1 = spcs.center(RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, RealScalar.ONE);
    assertEquals(p1, RationalScalar.of(3, 256));
  }

  public void testString5() {
    SixPointCurveSubdivision spcs = new SixPointCurveSubdivision(RnGeodesic.INSTANCE);
    Tensor string = spcs.string(Tensors.vector(1, 0, 0, 0, 0));
    Scalar scalar = string.Get(4);
    assertTrue(Scalars.isZero(scalar));
    assertEquals(string.length(), 5 + 4);
  }

  public void testString6() {
    SixPointCurveSubdivision spcs = new SixPointCurveSubdivision(RnGeodesic.INSTANCE);
    Tensor string = spcs.string(Tensors.vector(1, 0, 0, 0, 0, 0));
    Scalar scalar = string.Get(5);
    assertTrue(Scalars.nonZero(scalar));
    assertEquals(string.length(), 6 + 5);
  }
}
