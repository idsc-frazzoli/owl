// code by jph
package ch.ethz.idsc.owl.gui.ren;

import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testSimple() {
    assertEquals(StaticHelper.length2(Tensors.vector()), Tensors.vector(0, 0));
    assertEquals(StaticHelper.length2(Tensors.vector(1)), Tensors.vector(1, 0));
    assertEquals(StaticHelper.length2(Tensors.vector(1, 2)), Tensors.vector(1, 2));
    assertEquals(StaticHelper.length2(Tensors.vector(1, 2, 3)), Tensors.vector(1, 2));
  }
}
