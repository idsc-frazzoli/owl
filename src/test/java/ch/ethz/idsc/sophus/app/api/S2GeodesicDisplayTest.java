// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Arrays;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import junit.framework.TestCase;

public class S2GeodesicDisplayTest extends TestCase {
  public void testSimple() {
    Tensor matrix = S2GeodesicDisplay.tangentSpace(Tensors.vector(1, 1, 1));
    assertEquals(Dimensions.of(matrix), Arrays.asList(2, 3));
  }

  public void testInvariant() {
    GeodesicDisplay geodesicDisplay = S2GeodesicDisplay.INSTANCE;
    Tensor xyz = geodesicDisplay.project(Tensors.vector(1, 2, 0));
    Tensor xy = geodesicDisplay.toPoint(xyz);
    ExactTensorQ.require(xy);
    assertEquals(xy, Tensors.vector(1, 2));
  }
}