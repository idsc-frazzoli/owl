// code by jph
package ch.ethz.idsc.owl.bot.kl;

import junit.framework.TestCase;

public class KlotskiObstacleRegionTest extends TestCase {
  public void testHuarong() {
    for (Huarong huarong : Huarong.values()) {
      KlotskiProblem klotskiProblem = huarong.create();
      assertFalse(KlotskiObstacleRegion.huarong().isMember(klotskiProblem.getState()));
    }
    for (Pennant pennant : Pennant.values()) {
      KlotskiProblem klotskiProblem = pennant.create();
      assertFalse(KlotskiObstacleRegion.huarong().isMember(klotskiProblem.getState()));
    }
  }

  public void testTrafficJam() {
    for (TrafficJam trafficJam : TrafficJam.values()) {
      KlotskiProblem klotskiProblem = trafficJam.create();
      assertFalse(KlotskiObstacleRegion.fromSize(klotskiProblem.size()).isMember(klotskiProblem.getState()));
    }
  }
}
