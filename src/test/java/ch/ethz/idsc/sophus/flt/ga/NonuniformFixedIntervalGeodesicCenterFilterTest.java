// code by ob
package ch.ethz.idsc.sophus.flt.ga;

import java.io.IOException;
import java.util.NavigableMap;
import java.util.TreeMap;

import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.win.SmoothingKernel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class NonuniformFixedIntervalGeodesicCenterFilterTest extends TestCase {
  public void testTrivial() throws ClassNotFoundException, IOException {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    navigableMap.put(RealScalar.ONE, Tensors.vector(1, 1, 1));
    // ---
    SmoothingKernel smoothingKernel = SmoothingKernel.GAUSSIAN;
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    Scalar interval = RealScalar.ONE;
    Scalar samplingFrequency = RealScalar.ONE;
    // ---
    NonuniformFixedIntervalGeodesicCenter nonuniformFixedIntervalGeodesicCenter = NonuniformFixedIntervalGeodesicCenter.of(geodesicInterface, smoothingKernel);
    NonuniformFixedIntervalGeodesicCenterFilter nonuniformFixedIntervalGeodesicCenterFilter = //
        Serialization.copy(NonuniformFixedIntervalGeodesicCenterFilter.of(nonuniformFixedIntervalGeodesicCenter, interval.divide(samplingFrequency)));
    Tensor actual = Tensor.of(nonuniformFixedIntervalGeodesicCenterFilter.apply(navigableMap).values().stream());
    Tensor expected = Tensors.of(Tensors.vector(1, 1, 1));
    assertEquals(expected, actual);
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
    assertEquals(expected, actual);
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
    expected.set(So2.MOD, Tensor.ALL, 2);
    assertEquals(expected, actual);
  }
}
