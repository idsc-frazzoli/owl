// code by jph
package ch.ethz.idsc.sophus.filter;

import java.io.IOException;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.group.Se2CoveringGeodesic;
import ch.ethz.idsc.sophus.math.IntegerTensorFunction;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import junit.framework.TestCase;

public class GeodesicCenterTest extends TestCase {
  private static final IntegerTensorFunction CONSTANT = //
      i -> Array.of(k -> RationalScalar.of(1, 2 * i + 1), 2 * i + 1);

  public void testSimple() {
    // function generates window to compute mean: all points in window have same weight
    TensorUnaryOperator tensorUnaryOperator = GeodesicCenter.of(RnGeodesic.INSTANCE, CONSTANT);
    for (int index = 0; index < 9; ++index) {
      Tensor apply = tensorUnaryOperator.apply(UnitVector.of(9, index));
      assertEquals(apply, RationalScalar.of(1, 9));
    }
  }

  public void testEvenFail() {
    TensorUnaryOperator tensorUnaryOperator = GeodesicCenter.of(RnGeodesic.INSTANCE, CONSTANT);
    for (int index = 0; index < 9; ++index)
      try {
        tensorUnaryOperator.apply(Array.zeros(2 * index));
        fail();
      } catch (Exception exception) {
        // ---
      }
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    TensorUnaryOperator tensorUnaryOperator = GeodesicCenter.of(Se2CoveringGeodesic.INSTANCE, CONSTANT);
    Serialization.copy(tensorUnaryOperator);
  }

  public void testFail() {
    try {
      GeodesicCenter.of(RnGeodesic.INSTANCE, null);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      GeodesicCenter.of(null, CONSTANT);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
