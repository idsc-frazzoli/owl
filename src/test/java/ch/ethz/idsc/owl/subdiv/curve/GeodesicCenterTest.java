// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.util.function.Function;

import ch.ethz.idsc.owl.math.group.RnGeodesic;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import junit.framework.TestCase;

public class GeodesicCenterTest extends TestCase {
  private static final Function<Integer, Tensor> CONSTANT = //
      i -> Array.of(k -> RationalScalar.of(1, 2 * i + 1), 2 * i + 1);

  public void testSimple() {
    // function generates window to compute mean: all points in window have same weight
    TensorUnaryOperator geodesicCenter = GeodesicCenter.of(RnGeodesic.INSTANCE, CONSTANT);
    for (int index = 0; index < 9; ++index) {
      Tensor apply = geodesicCenter.apply(UnitVector.of(9, index));
      assertEquals(apply, RationalScalar.of(1, 9));
    }
  }

  public void testFail() {
    try {
      GeodesicCenter.of(RnGeodesic.INSTANCE, null);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
    try {
      GeodesicCenter.of(null, CONSTANT);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
