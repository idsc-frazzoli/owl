// code by jph
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class GeodesicBSplineCenterTest extends TestCase {
  public void testSimple3() {
    GeodesicBSplineCenter geodesicBSplineCenter = //
        new GeodesicBSplineCenter(RnGeodesic.INSTANCE);
    Tensor tensor = geodesicBSplineCenter.apply(Tensors.vector(1, 2, 3));
    assertEquals(tensor, RealScalar.of(2));
  }

  public void testSimple5() {
    GeodesicBSplineCenter geodesicBSplineCenter = //
        new GeodesicBSplineCenter(RnGeodesic.INSTANCE);
    Tensor tensor = geodesicBSplineCenter.apply(Tensors.vector(1, 2, 3, 4, 5));
    assertEquals(tensor, RealScalar.of(3));
  }

  public void testEvenFail() {
    GeodesicBSplineCenter geodesicBSplineCenter = //
        new GeodesicBSplineCenter(RnGeodesic.INSTANCE);
    try {
      geodesicBSplineCenter.apply(Tensors.vector(1, 2, 3, 4));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testScalarFail() {
    GeodesicBSplineCenter geodesicBSplineCenter = //
        new GeodesicBSplineCenter(RnGeodesic.INSTANCE);
    try {
      geodesicBSplineCenter.apply(RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
