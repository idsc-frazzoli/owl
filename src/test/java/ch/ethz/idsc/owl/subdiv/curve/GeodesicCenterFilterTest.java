// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.UnitVector;
import junit.framework.TestCase;

public class GeodesicCenterFilterTest extends TestCase {
  public void testSimple() {
    GeodesicCenter geodesicCenter = new GeodesicCenter(RnGeodesic.INSTANCE, FilterMask.BINOMIAL);
    GeodesicCenterFilter geodesicCenterFilter = new GeodesicCenterFilter(geodesicCenter, 3);
    Tensor linear = Range.of(0, 10);
    Tensor result = geodesicCenterFilter.apply(linear);
    assertEquals(result, linear);
    assertTrue(ExactScalarQ.all(result));
  }

  public void testKernel3() {
    GeodesicCenter geodesicCenter = new GeodesicCenter(RnGeodesic.INSTANCE, FilterMask.BINOMIAL);
    GeodesicCenterFilter geodesicCenterFilter = new GeodesicCenterFilter(geodesicCenter, 3);
    Tensor signal = UnitVector.of(9, 4);
    Tensor result = geodesicCenterFilter.apply(signal);
    assertTrue(ExactScalarQ.all(result));
    assertEquals(result, Tensors.fromString("{0, 0, 1/16, 15/64, 5/16, 15/64, 1/16, 0, 0}"));
  }

  public void testKernel1() {
    GeodesicCenter geodesicCenter = new GeodesicCenter(RnGeodesic.INSTANCE, FilterMask.BINOMIAL);
    GeodesicCenterFilter geodesicCenterFilter = new GeodesicCenterFilter(geodesicCenter, 1);
    Tensor signal = UnitVector.of(5, 2);
    Tensor result = geodesicCenterFilter.apply(signal);
    assertTrue(ExactScalarQ.all(result));
    assertEquals(result, Tensors.fromString("{0, 1/4, 1/2, 1/4, 0}"));
  }
}
