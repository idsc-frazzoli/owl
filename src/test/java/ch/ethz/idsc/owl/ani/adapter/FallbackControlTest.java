// code by jph
package ch.ethz.idsc.owl.ani.adapter;

import java.util.Optional;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class FallbackControlTest extends TestCase {
  public void testSimple() {
    FallbackControl fallbackControl = new FallbackControl(Tensors.vector(1, 2, 3));
    Optional<Tensor> optional = fallbackControl.control(null, null);
    assertEquals(optional.get(), Tensors.vector(1, 2, 3));
  }
}
