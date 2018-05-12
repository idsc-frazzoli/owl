// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class Se2ComboDistanceTest extends TestCase {
  public void testSimple() {
    Se2ComboRegion.spherical(Tensors.vector(1, 2, 3), Tensors.vector(1, 1, 3));
    try {
      Se2ComboRegion.spherical(Tensors.vector(1, 2, 3), Tensors.vector(1, 2, 3));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }

  public void testIsMember() {
    Se2ComboRegion se2ComboRegion = Se2ComboRegion.spherical(Tensors.vector(1, 2, 3), Tensors.vector(1, 1, 0.1));
    assertTrue(se2ComboRegion.isMember(Tensors.vector(1, 2, 3)));
    assertFalse(se2ComboRegion.isMember(Tensors.vector(-1, 2, 3)));
    assertFalse(se2ComboRegion.isMember(Tensors.vector(1, 2, 3.2)));
  }
}
