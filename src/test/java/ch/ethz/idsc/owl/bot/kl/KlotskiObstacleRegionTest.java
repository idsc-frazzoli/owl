// code by jph
package ch.ethz.idsc.owl.bot.kl;

import junit.framework.TestCase;

public class KlotskiObstacleRegionTest extends TestCase {
  public void testHuarong() {
    for (KlotskiProblem klotskiProblem : Huarong.values())
      assertFalse(KlotskiObstacleRegion.huarong().isMember(klotskiProblem.getBoard()));
    for (KlotskiProblem klotskiProblem : Pennant.values())
      assertFalse(KlotskiObstacleRegion.huarong().isMember(klotskiProblem.getBoard()));
  }

  public void testTrafficJam() {
    for (KlotskiProblem klotskiProblem : TrafficJam.values())
      assertFalse(KlotskiObstacleRegion.fromSize(klotskiProblem.size()).isMember(klotskiProblem.getBoard()));
  }
}
