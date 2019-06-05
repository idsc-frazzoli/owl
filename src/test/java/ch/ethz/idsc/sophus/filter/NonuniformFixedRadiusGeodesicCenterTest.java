// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.NavigableMap;
import java.util.TreeMap;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
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
    assertEquals(actual.get(0), actual.get(1));
    Chop._09.requireClose(expected, actual);
  }

  public void testUniform() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    for (int index = 1; index < 10; ++index) {
      Scalar key = RealScalar.of(index);
      Tensor tensor = Tensors.of(RealScalar.of(index), RealScalar.of(index), RealScalar.ZERO);
      navigableMap.put(key, tensor);
    }
    // ---
    NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter = new NonuniformFixedRadiusGeodesicCenter(Se2Geodesic.INSTANCE);
    Scalar key = RealScalar.of(5);
    // ---
    Tensor actual = nonuniformFixedRadiusGeodesicCenter.apply(navigableMap, key);
    Tensor expected = Tensors.vector(5, 5, 0);
    assertEquals(expected, actual);
  }

  public void testNonuniformlySpacedR2() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    for (int index = 1; index < 10; ++index) {
      navigableMap.put(RealScalar.of(index * index), Tensors.of(RealScalar.of(index * index), RealScalar.of(index * index)));
    }
    // ---
    NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter = new NonuniformFixedRadiusGeodesicCenter(RnGeodesic.INSTANCE);
    Scalar key = RealScalar.of(25);
    Tensor actual = nonuniformFixedRadiusGeodesicCenter.apply(navigableMap, key);
    assertEquals(actual.get(0), actual.get(1));
  }

  public void testNonuniformlySpacedSE2() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    for (int index = 1; index < 10; ++index) {
      navigableMap.put(RealScalar.of(index * index), Tensors.of(RealScalar.of(index * index), RealScalar.of(index * index), RealScalar.ZERO));
    }
    // ---
    NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter = new NonuniformFixedRadiusGeodesicCenter(Se2Geodesic.INSTANCE);
    Scalar key = RealScalar.of(25);
    Tensor actual = nonuniformFixedRadiusGeodesicCenter.apply(navigableMap, key);
    System.out.println(actual);
    assertEquals(actual.get(0), actual.get(1));
  }

  public void testAffineButDifferentlySpaced() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    navigableMap.put(RealScalar.ZERO, Tensors.vector(0, 0, 0));
    navigableMap.put(RealScalar.ONE, Tensors.vector(1, 0, 0));
    navigableMap.put(RealScalar.of(100), Tensors.vector(100, 0, 0));
    // ---
    NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter = new NonuniformFixedRadiusGeodesicCenter(Se2Geodesic.INSTANCE);
    Scalar key = RealScalar.of(1);
    // ---
    Tensor actual = nonuniformFixedRadiusGeodesicCenter.apply(navigableMap, key);
    Tensor expected = Tensors.vector(1, 0, 0);
    Chop._09.requireClose(actual, expected);
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
