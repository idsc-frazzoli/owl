package ch.ethz.idsc.sophus.filter;

import java.util.NavigableMap;
import java.util.TreeMap;

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
    Scalar radius = RealScalar.of(Math.random());
    Scalar key = RealScalar.of(1);
    // --
    Tensor actual = nonuniformFixedRadiusGeodesicCenter.apply(navigableMap, key, radius);
    Tensor expected = Tensors.vector(1, 1, 0);
    Assert.assertEquals(actual.get(0), actual.get(1));
    Chop._09.requireClose(expected, actual);
  }

  public void testUniform() {
    // TODO OB: there is an error hidden somewhere in NFRGC
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    for (int index = 1; index < 10; ++index) {
      navigableMap.put(RealScalar.of(index), Tensors.of(RealScalar.of(index), RealScalar.of(index), RealScalar.ZERO));
    }
    // ---
    NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter = new NonuniformFixedRadiusGeodesicCenter(Se2Geodesic.INSTANCE);
    Scalar radius = RealScalar.of(5);
    Scalar key = RealScalar.of(5);
    // ---
    Tensor actual = nonuniformFixedRadiusGeodesicCenter.apply(navigableMap, key, radius);
    Tensor expected = Tensors.vector(5, 5, 0);
    Chop._09.requireClose(expected, actual);
  }

  public void testNonuniformlySpaced() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    for (int index = 1; index < 10; ++index) {
      navigableMap.put(RealScalar.of(index * index), Tensors.of(RealScalar.of(index * index), RealScalar.of(index * index), RealScalar.ZERO));
    }
    // ---
    NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter = new NonuniformFixedRadiusGeodesicCenter(Se2Geodesic.INSTANCE);
    Scalar radius = RealScalar.of(9 * 9 / 2);
    Scalar key = RealScalar.of(9 * 9 / 2 + 1);
    // ---
    Tensor actual = nonuniformFixedRadiusGeodesicCenter.apply(navigableMap, key, radius);
    Assert.assertEquals(actual.get(0), actual.get(1));
  }

  // TODO OB: Does this test make sense? If yes -> translate to NFIGC
  // This needs no be solved more beatifully
  public void testNonuniformlyAbove() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    navigableMap.put(RealScalar.ZERO, Tensors.vector(0, 0, 0));
    navigableMap.put(RealScalar.ONE, Tensors.vector(1, 0, 0));
    navigableMap.put(RealScalar.of(10), Tensors.vector(2, 0, 0));
    // ---
    NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter = new NonuniformFixedRadiusGeodesicCenter(Se2Geodesic.INSTANCE);
    Scalar radius = RealScalar.of(1);
    Scalar key = RealScalar.of(1);
    // ---
    Tensor actual = nonuniformFixedRadiusGeodesicCenter.apply(navigableMap, key, radius);
    Boolean compared = Scalars.lessEquals(actual.Get(0), RealScalar.ONE);
    Assert.assertEquals(compared, 1);
  } // TODO OB: Does this test make sense? If yes -> translate to NFIGC
  // This needs no be solved more beatifully

  public void testNegativeTime() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    navigableMap.put(RealScalar.of(-1), Tensors.vector(0, 0, 0));
    // ---
    NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter = new NonuniformFixedRadiusGeodesicCenter(Se2Geodesic.INSTANCE);
    Scalar radius = RealScalar.of(1);
    Scalar key = RealScalar.of(-1);
    try {
      nonuniformFixedRadiusGeodesicCenter.apply(navigableMap, key, radius);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFail() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    navigableMap.put(RealScalar.ONE, Tensors.vector(1, 1, 0));
    NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter = new NonuniformFixedRadiusGeodesicCenter(Se2Geodesic.INSTANCE);
    Scalar radius = RealScalar.of(-1);
    Scalar key = RealScalar.of(1);
    try {
      nonuniformFixedRadiusGeodesicCenter.apply(navigableMap, key, radius);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
