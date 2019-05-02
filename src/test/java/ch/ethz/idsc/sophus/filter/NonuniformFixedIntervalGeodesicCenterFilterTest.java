//code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.NavigableMap;
import java.util.TreeMap;

import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.Assert;
import junit.framework.TestCase;

public class NonuniformFixedIntervalGeodesicCenterFilterTest extends TestCase {
  public void testTrivial() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    navigableMap.put(RealScalar.ONE, Tensors.vector(1, 1, 1));
    // ---
    SmoothingKernel smoothingKernel = SmoothingKernel.GAUSSIAN;
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    Scalar interval = RealScalar.ONE;
    Scalar samplingFrequency = RealScalar.ONE;
    // ---
    NonuniformFixedIntervalGeodesicCenter nonuniformFixedIntervalGeodesicCenter = NonuniformFixedIntervalGeodesicCenter.of(geodesicInterface, smoothingKernel);
    Tensor actual = Tensor.of(NonuniformFixedIntervalGeodesicCenterFilter.of(nonuniformFixedIntervalGeodesicCenter, interval.divide(samplingFrequency))
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
    SmoothingKernel smoothingKernel = SmoothingKernel.GAUSSIAN;
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    Scalar interval = RealScalar.of(1.1);
    Scalar samplingFrequency = RealScalar.ONE;
    // ---
    NonuniformFixedIntervalGeodesicCenter nonuniformFixedIntervalGeodesicCenter = NonuniformFixedIntervalGeodesicCenter.of(geodesicInterface, smoothingKernel);
    Tensor actual = Tensor.of(NonuniformFixedIntervalGeodesicCenterFilter.of(nonuniformFixedIntervalGeodesicCenter, interval.divide(samplingFrequency))
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
    SmoothingKernel smoothingKernel = SmoothingKernel.GAUSSIAN;
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    Scalar interval = RealScalar.of(3);
    Scalar samplingFrequency = RealScalar.ONE;
    // ---
    NonuniformFixedIntervalGeodesicCenter nonuniformFixedIntervalGeodesicCenter = NonuniformFixedIntervalGeodesicCenter.of(geodesicInterface, smoothingKernel);
    Tensor actual = Tensor.of(NonuniformFixedIntervalGeodesicCenterFilter.of(nonuniformFixedIntervalGeodesicCenter, interval.divide(samplingFrequency))
        .apply(navigableMap).values().stream());
    Tensor expected = Tensors.fromString(
        "{{0.0, 0.0, 0.0}, {0.8895784526281458, 0.7276528633124004, 0.8004148013462675}, {1.8895784526281458, 1.7276528633124004, 1.8004148013462675}, {3.0, 3.0, 3.0}, {4.0, 4.0, 4.0}}");
    Assert.assertEquals(expected, actual);
  }
}
