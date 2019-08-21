// code by jph
package ch.ethz.idsc.owl.bot.r2;

import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class R2ImageRegionsTest extends TestCase {
  public void testSimple() {
    R2ImageRegionWrap r2irw = R2ImageRegions._0F5C_2182;
    assertTrue(r2irw.region().isMember(Tensors.vector(1, 2)));
    assertFalse(r2irw.region().isMember(Tensors.vector(7, 5)));
  }
}
