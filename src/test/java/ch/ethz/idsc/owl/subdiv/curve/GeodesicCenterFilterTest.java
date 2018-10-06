// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.sca.N;
import junit.framework.TestCase;

public class GeodesicCenterFilterTest extends TestCase {
  public void testSimple() {
    GeodesicCenter geodesicCenter = new GeodesicCenter(RnGeodesic.INSTANCE, BinomialMask.FUNCTION);
    GeodesicCenterFilter geodesicCenterFilter = new GeodesicCenterFilter(geodesicCenter, 3);
    Tensor linear = Range.of(0, 10);
    Tensor result = geodesicCenterFilter.apply(linear);
    assertEquals(result, linear);
    assertTrue(ExactScalarQ.all(result));
  }

  public void testKernel3() {
    GeodesicCenter geodesicCenter = new GeodesicCenter(RnGeodesic.INSTANCE, BinomialMask.FUNCTION);
    GeodesicCenterFilter geodesicCenterFilter = new GeodesicCenterFilter(geodesicCenter, 3);
    Tensor signal = UnitVector.of(9, 4);
    Tensor result = geodesicCenterFilter.apply(signal);
    assertTrue(ExactScalarQ.all(result));
    // FIXME WRONG
    System.out.println(N.DOUBLE.of(result));
  }

  public void testKernel1() {
    GeodesicCenter geodesicCenter = new GeodesicCenter(RnGeodesic.INSTANCE, BinomialMask.FUNCTION);
    GeodesicCenterFilter geodesicCenterFilter = new GeodesicCenterFilter(geodesicCenter, 1);
    Tensor signal = UnitVector.of(5, 2);
    Tensor result = geodesicCenterFilter.apply(signal);
    assertTrue(ExactScalarQ.all(result));
    System.out.println(N.DOUBLE.of(result));
  }
}
