//code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.NavigableMap;
import java.util.TreeMap;

import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.Assert;
import junit.framework.TestCase;

public class NonuniformFixedRadiusGeodesicCenterFilterTest extends TestCase {
  public void testTrivial() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    navigableMap.put(RealScalar.ONE, Tensors.vector(1, 1, 1));
    // ---
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    Scalar Radius = RealScalar.ONE;
    Scalar samplingFrequency = RealScalar.ONE;
    // ---
    NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter = NonuniformFixedRadiusGeodesicCenter.of(geodesicInterface);
    Tensor actual = Tensor.of(NonuniformFixedRadiusGeodesicCenterFilter.of(nonuniformFixedRadiusGeodesicCenter, Radius.divide(samplingFrequency))
        .apply(navigableMap).values().stream());
    Tensor expected = Tensors.of(Tensors.vector(1, 1, 1));
    Assert.assertEquals(expected, actual);
  }

  public void testUniform() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    for (int index = 0; index < 5; ++index) {
      navigableMap.put(RealScalar.of(index), Tensors.vector(index, index, 0));
    }
    // ---
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    Scalar Radius = RealScalar.of(1.1);
    Scalar samplingFrequency = RealScalar.ONE;
    // ---
    NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter = NonuniformFixedRadiusGeodesicCenter.of(geodesicInterface);
    Tensor actual = Tensor.of(NonuniformFixedRadiusGeodesicCenterFilter.of(nonuniformFixedRadiusGeodesicCenter, Radius.divide(samplingFrequency))
        .apply(navigableMap).values().stream());
    Tensor expected = Tensor.of(navigableMap.values().stream());
    Assert.assertEquals(expected, actual);
  }

  public void testNonuniform() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    for (int index = 0; index < 5; ++index) {
      navigableMap.put(RealScalar.of(index * index), Tensors.vector(index, index, index));
    }
    // ---
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    Scalar Radius = RealScalar.of(3);
    Scalar samplingFrequency = RealScalar.ONE;
    // ---
    NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter = NonuniformFixedRadiusGeodesicCenter.of(geodesicInterface);
    Tensor actual = Tensor.of(NonuniformFixedRadiusGeodesicCenterFilter.of(nonuniformFixedRadiusGeodesicCenter, Radius.divide(samplingFrequency))
        .apply(navigableMap).values().stream());
    Tensor expected = Tensors.fromString(
        "{{0.0, 0.0, 0.0}, {0.970838482573737, 0.7515257456099597, 0.8571428571428572}, {2.0426698293378713, 1.8121689054467962, 1.909445202318661}, {3.009957899127149, 2.968480149671003, 2.988352745424293}, {4.0, 4.0, 4.0}}");
    Assert.assertEquals(expected, actual);
  }
}
