// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.util.function.Function;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.UnitVector;
import junit.framework.TestCase;

public class GeodesicCenterTest extends TestCase {
  public void testSimple() {
    // function generates window to compute mean: all points in window have same weight
    Function<Integer, Tensor> function = i -> Array.of(k -> RationalScalar.of(1, 2 * i + 1), 2 * i + 1);
    GeodesicCenter geodesicCenter = new GeodesicCenter(RnGeodesic.INSTANCE, function);
    for (int index = 0; index < 9; ++index) {
      Tensor apply = geodesicCenter.apply(UnitVector.of(9, index));
      assertEquals(apply, RationalScalar.of(1, 9));
    }
  }
}
