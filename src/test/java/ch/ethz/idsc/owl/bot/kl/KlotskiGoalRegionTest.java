// code by jph
package ch.ethz.idsc.owl.bot.kl;

import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class KlotskiGoalRegionTest extends TestCase {
  public void testSimple() {
    KlotskiGoalRegion klotskiGoalRegion = new KlotskiGoalRegion(Tensors.vector(0, 4, 2));
    for (Huarong huarong : Huarong.values())
      assertFalse(klotskiGoalRegion.isMember(huarong.create().startState()));
  }
}
