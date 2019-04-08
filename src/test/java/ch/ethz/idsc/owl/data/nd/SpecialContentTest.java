// code by jph
package ch.ethz.idsc.owl.data.nd;

import java.io.IOException;

import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class SpecialContentTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    SpecialContent sc = new SpecialContent();
    SpecialContent cp = Serialization.copy(sc);
    assertEquals(cp.value, Tensors.vector(1, 2, 3));
    assertEquals(cp.handled, Tensors.vector(99, 100));
  }
}
