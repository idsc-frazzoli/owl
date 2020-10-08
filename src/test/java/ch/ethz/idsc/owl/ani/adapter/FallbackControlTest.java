// code by jph
package ch.ethz.idsc.owl.ani.adapter;

import java.io.IOException;
import java.util.Optional;

import ch.ethz.idsc.owl.ani.api.EntityControl;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.Serialization;
import junit.framework.TestCase;

public class FallbackControlTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    EntityControl fallbackControl = Serialization.copy(FallbackControl.of(Tensors.vector(1, 2, 3)));
    Optional<Tensor> optional = fallbackControl.control(null, null);
    assertEquals(optional.get(), Tensors.vector(1, 2, 3));
  }
}
