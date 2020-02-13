// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Arrays;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import junit.framework.TestCase;

public class S2GeodesicDisplayTest extends TestCase {
  public void testSimple() {
    Tensor matrix = S2GeodesicDisplay.frame(Tensors.vector(1, 1, 1));
    assertEquals(Dimensions.of(matrix), Arrays.asList(3, 3));
  }
}
