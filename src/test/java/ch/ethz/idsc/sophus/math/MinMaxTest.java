// code by jph
package ch.ethz.idsc.sophus.math;

import java.io.IOException;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class MinMaxTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Tensor tensor = Tensors.fromString("{{1, 9, 3}, {4, 5, -6}}");
    MinMax minMax = Serialization.copy(MinMax.of(tensor));
    assertEquals(minMax.min(), Tensors.vector(1, 5, -6));
    assertEquals(minMax.max(), Tensors.vector(4, 9, 3));
  }

  public void testFail() {
    Tensor tensor = Tensors.fromString("{{1, 9, 3}, {4, 5}}");
    try {
      MinMax.of(tensor);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailScalar() {
    try {
      MinMax.of(RealScalar.ZERO);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
