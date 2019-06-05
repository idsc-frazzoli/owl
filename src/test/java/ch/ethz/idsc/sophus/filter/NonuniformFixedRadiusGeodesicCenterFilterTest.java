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
import junit.framework.TestCase;

public class NonuniformFixedRadiusGeodesicCenterFilterTest extends TestCase {
  public void testTrivial() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    navigableMap.put(RealScalar.ONE, Tensors.vector(1, 1, 1));
    // ---
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    int radius = 1;
    // ---
    NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter = NonuniformFixedRadiusGeodesicCenter.of(geodesicInterface);
    Tensor actual = Tensor.of(NonuniformFixedRadiusGeodesicCenterFilter.of(nonuniformFixedRadiusGeodesicCenter, radius).apply(navigableMap).values().stream());
    Tensor expected = Tensors.of(Tensors.vector(1, 1, 1));
    assertEquals(expected, actual);
  }

  public void testUniform() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    for (int index = 0; index < 5; ++index) {
      navigableMap.put(RealScalar.of(index), Tensors.vector(index, index, 0));
    }
    // ---
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    int radius = 1;
    // ---
    NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter = NonuniformFixedRadiusGeodesicCenter.of(geodesicInterface);
    Tensor actual = Tensor.of(NonuniformFixedRadiusGeodesicCenterFilter.of(nonuniformFixedRadiusGeodesicCenter, radius).apply(navigableMap).values().stream());
    Tensor expected = Tensor.of(navigableMap.values().stream());
    assertEquals(expected, actual);
  }

  public void testNonuniform() {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    for (int index = 0; index < 5; ++index) {
      navigableMap.put(RealScalar.of(index * index), Tensors.vector(index, index, index));
    }
    // ---
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    int radius = 3;
    // ---
    NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter = NonuniformFixedRadiusGeodesicCenter.of(geodesicInterface);
    Tensor actual = Tensor.of(NonuniformFixedRadiusGeodesicCenterFilter.of(nonuniformFixedRadiusGeodesicCenter, radius).apply(navigableMap).values().stream());
    System.out.println(actual);
    Tensor expected = Tensors.fromString(
        "{{0.0, 0.0, 0.0}, {1.2557148460542482, 0.7442851539457515, 1.0}, {3.1242087383516894, 1.3354843829315397, 2.1666666666666665}, {3.2553808967607205, 2.7446191032392786, 3.0}, {4.0, 4.0, 4.0}}");
    // assertEquals(expected, actual);
  }
}
