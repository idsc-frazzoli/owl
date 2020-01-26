// code by jph
package ch.ethz.idsc.owl.bot.kl;

import junit.framework.TestCase;

public class HuarongObstacleRegionTest extends TestCase {
  public void testSimple() {
    for (KlotskiProblem klotskiProblem : Huarong.values())
      assertFalse(HuarongObstacleRegion.INSTANCE.isMember(klotskiProblem.getBoard()));
    for (KlotskiProblem klotskiProblem : Pennant.values())
      assertFalse(HuarongObstacleRegion.INSTANCE.isMember(klotskiProblem.getBoard()));
  }
}
