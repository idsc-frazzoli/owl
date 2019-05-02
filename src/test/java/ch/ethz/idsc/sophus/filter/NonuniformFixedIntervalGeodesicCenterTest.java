// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.NavigableMap;
import java.util.TreeMap;

import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.Assert;
import junit.framework.TestCase;

public class NonuniformFixedIntervalGeodesicCenterTest extends TestCase {
  public void testTrivial() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    navigableMap.put(RealScalar.ONE, Tensors.vector(1, 1, 0));
    NonuniformFixedIntervalGeodesicCenter nonuniformFixedIntervalGeodesicCenter = new NonuniformFixedIntervalGeodesicCenter(Se2Geodesic.INSTANCE,
        SmoothingKernel.GAUSSIAN);
    Scalar interval = RealScalar.of(Math.random());
    Scalar key = RealScalar.of(1);
    // --
    Tensor actual = nonuniformFixedIntervalGeodesicCenter.apply(navigableMap, key, interval);
    Tensor expected = Tensors.vector(1, 1, 0);
    Assert.assertEquals(actual.get(0), actual.get(1));
    Chop._09.requireClose(expected, actual);
  }

  public void testUniform() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    for (int index = 1; index < 10; ++index) {
      navigableMap.put(RealScalar.of(index), Tensors.of(RealScalar.of(index), RealScalar.of(index), RealScalar.ZERO));
    }
    // ---
    NonuniformFixedIntervalGeodesicCenter nonuniformFixedIntervalGeodesicCenter = new NonuniformFixedIntervalGeodesicCenter(Se2Geodesic.INSTANCE,
        SmoothingKernel.GAUSSIAN);
    Scalar interval = RealScalar.of(5);
    Scalar key = RealScalar.of(5);
    // ---
    Tensor actual = nonuniformFixedIntervalGeodesicCenter.apply(navigableMap, key, interval);
    Tensor expected = Tensors.vector(5, 5, 0);
    Chop._09.requireClose(expected, actual);
  }

  public void testNonuniformlySpaced() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    for (int index = 1; index < 10; ++index) {
      navigableMap.put(RealScalar.of(index * index), Tensors.of(RealScalar.of(index * index), RealScalar.of(index * index), RealScalar.ZERO));
    }
    // ---
    NonuniformFixedIntervalGeodesicCenter nonuniformFixedIntervalGeodesicCenter = new NonuniformFixedIntervalGeodesicCenter(Se2Geodesic.INSTANCE,
        SmoothingKernel.GAUSSIAN);
    Scalar interval = RealScalar.of(9 * 9 / 2);
    Scalar key = RealScalar.of(9 * 9 / 2 + 1);
    // ---
    Tensor actual = nonuniformFixedIntervalGeodesicCenter.apply(navigableMap, key, interval);
    Assert.assertEquals(actual.get(0), actual.get(1));
  }

  public void testNegativeInterval() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    navigableMap.put(RealScalar.ONE, Tensors.vector(1, 1, 0));
    NonuniformFixedIntervalGeodesicCenter nonuniformFixedIntervalGeodesicCenter = new NonuniformFixedIntervalGeodesicCenter(Se2Geodesic.INSTANCE,
        SmoothingKernel.GAUSSIAN);
    Scalar interval = RealScalar.of(-1);
    Scalar key = RealScalar.of(1);
    try {
      nonuniformFixedIntervalGeodesicCenter.apply(navigableMap, key, interval);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFail() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    navigableMap.put(RealScalar.ONE, Tensors.vector(1, 1, 0));
    NonuniformFixedIntervalGeodesicCenter nonuniformFixedIntervalGeodesicCenter = new NonuniformFixedIntervalGeodesicCenter(Se2Geodesic.INSTANCE,
        SmoothingKernel.GAUSSIAN);
    Scalar interval = RealScalar.of(-1);
    Scalar key = RealScalar.of(1);
    try {
      nonuniformFixedIntervalGeodesicCenter.apply(navigableMap, key, interval);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
