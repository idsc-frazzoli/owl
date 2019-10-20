// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class AffineAppendTest extends TestCase {
  public void testSimple() {
    Tensor tensor = AffineAppend.of(Tensors.vector(0.2, -0.3));
    Chop._12.requireClose(tensor, Tensors.vector(0.2, -0.3, 1.1));
  }

  public void testEmpty() {
    Tensor vector = Tensors.empty();
    Tensor tensor = AffineAppend.of(vector);
    Chop._12.requireClose(tensor, Tensors.vector(1));
    assertEquals(vector, Tensors.empty());
  }
}
