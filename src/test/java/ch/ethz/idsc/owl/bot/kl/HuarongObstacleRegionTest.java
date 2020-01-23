// code by jph
package ch.ethz.idsc.owl.bot.kl;

import junit.framework.TestCase;

public class HuarongObstacleRegionTest extends TestCase {
  public void testSimple() {
    for (Huarong huarong : Huarong.values())
      assertFalse(HuarongObstacleRegion.INSTANCE.isMember(huarong.getBoard()));
  }
}
