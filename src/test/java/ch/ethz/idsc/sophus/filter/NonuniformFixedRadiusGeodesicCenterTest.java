// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.NavigableMap;
import java.util.TreeMap;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.Assert;
import junit.framework.TestCase;

public class NonuniformFixedRadiusGeodesicCenterTest extends TestCase {
  public void testTrivial() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    navigableMap.put(RealScalar.ONE, Tensors.vector(1, 1, 0));
    NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter = new NonuniformFixedRadiusGeodesicCenter(Se2Geodesic.INSTANCE);
    Scalar key = RealScalar.of(1);
    // --
    Tensor actual = nonuniformFixedRadiusGeodesicCenter.apply(navigableMap, key);
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
    NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter = new NonuniformFixedRadiusGeodesicCenter(Se2Geodesic.INSTANCE);
    Scalar key = RealScalar.of(5);
    // ---
    Tensor actual = nonuniformFixedRadiusGeodesicCenter.apply(navigableMap, key);
    Tensor expected = Tensors.vector(5, 5, 0);
    Chop._09.requireClose(expected, actual);
  }

  public void testNonuniformlySpacedR2() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    for (int index = 1; index < 10; ++index) {
      navigableMap.put(RealScalar.of(index * index), Tensors.of(RealScalar.of(index * index), RealScalar.of(index * index)));
    }
    // ---
    NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter = new NonuniformFixedRadiusGeodesicCenter(RnGeodesic.INSTANCE);
    Scalar key = RealScalar.of(9 * 9 / 2 + 1);
    // ---
    // FIXME OB
    // Tensor actual = nonuniformFixedRadiusGeodesicCenter.apply(navigableMap, key);
    // Assert.assertEquals(actual.get(0), actual.get(1));
  }

  public void testNonuniformlySpacedSE2() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    for (int index = 1; index < 10; ++index) {
      navigableMap.put(RealScalar.of(index * index), Tensors.of(RealScalar.of(index * index), RealScalar.of(index * index), RealScalar.ZERO));
    }
    // ---
    NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter = new NonuniformFixedRadiusGeodesicCenter(Se2Geodesic.INSTANCE);
    Scalar key = RealScalar.of(9 * 9 / 2 + 1);
    // ---
    // FIXME OB
    // Tensor actual = nonuniformFixedRadiusGeodesicCenter.apply(navigableMap, key);
    // Assert.assertEquals(actual.get(0), actual.get(1));
  }

  // TODO OB: Does this test make sense? rewrite this test more beautiful
  public void testNonuniformlyAbove() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    navigableMap.put(RealScalar.ZERO, Tensors.vector(0, 0, 0));
    navigableMap.put(RealScalar.ONE, Tensors.vector(1, 0, 0));
    navigableMap.put(RealScalar.of(10), Tensors.vector(2, 0, 0));
    // ---
    NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter = new NonuniformFixedRadiusGeodesicCenter(Se2Geodesic.INSTANCE);
    Scalar key = RealScalar.of(1);
    // ---
    Tensor result = nonuniformFixedRadiusGeodesicCenter.apply(navigableMap, key);
    boolean actual = Scalars.lessThan(result.Get(0), RealScalar.ONE);
    // FIXME OB
    // assertTrue(actual);
  }

  public void testNegativeTime() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    navigableMap.put(RealScalar.of(-1), Tensors.vector(0, 0, 0));
    // ---
    NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter = new NonuniformFixedRadiusGeodesicCenter(Se2Geodesic.INSTANCE);
    Scalar key = RealScalar.of(-1);
    try {
      nonuniformFixedRadiusGeodesicCenter.apply(navigableMap, key);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testAsymmetricFail() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    navigableMap.put(RealScalar.ONE, Tensors.vector(1, 1, 0));
    navigableMap.put(RealScalar.ONE, Tensors.vector(2, 2, 0));
    navigableMap.put(RealScalar.ONE, Tensors.vector(3, 3, 0));
    navigableMap.put(RealScalar.ONE, Tensors.vector(4, 4, 0));
    NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter = new NonuniformFixedRadiusGeodesicCenter(Se2Geodesic.INSTANCE);
    Scalar key = RealScalar.of(2);
    try {
      nonuniformFixedRadiusGeodesicCenter.apply(navigableMap, key);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
