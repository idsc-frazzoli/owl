// code by jph
package ch.ethz.idsc.owl.bot.kl;

import junit.framework.TestCase;

public class HuarongGoalRegionTest extends TestCase {
  public void testSimple() {
    for (Huarong huarong : Huarong.values())
      assertFalse(HuarongGoalRegion.INSTANCE.isMember(huarong.getBoard()));
  }
}
