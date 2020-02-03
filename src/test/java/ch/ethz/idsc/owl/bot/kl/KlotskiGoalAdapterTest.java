// code by jph
package ch.ethz.idsc.owl.bot.kl;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import junit.framework.TestCase;

public class KlotskiGoalAdapterTest extends TestCase {
  public void testSimple() {
    for (Huarong huarong : Huarong.values()) {
      KlotskiProblem klotskiProblem = huarong.create();
      KlotskiGoalAdapter klotskiGoalAdapter = new KlotskiGoalAdapter(klotskiProblem.getGoal());
      Scalar minCostToGoal = klotskiGoalAdapter.minCostToGoal(klotskiProblem.startState());
      assertEquals(minCostToGoal, RealScalar.of(3));
    }
  }

  public void testPennant() {
    for (Pennant pennant : Pennant.values()) {
      KlotskiProblem klotskiProblem = pennant.create();
      KlotskiGoalAdapter klotskiGoalAdapter = new KlotskiGoalAdapter(klotskiProblem.getGoal());
      Scalar minCostToGoal = klotskiGoalAdapter.minCostToGoal(klotskiProblem.startState());
      assertEquals(minCostToGoal, RealScalar.of(3));
    }
  }

  public void testTrafficJam() {
    for (TrafficJam trafficJam : TrafficJam.values()) {
      KlotskiProblem klotskiProblem = trafficJam.create();
      KlotskiGoalAdapter klotskiGoalAdapter = new KlotskiGoalAdapter(klotskiProblem.getGoal());
      Scalar minCostToGoal = klotskiGoalAdapter.minCostToGoal(klotskiProblem.startState());
      assertEquals(minCostToGoal, RealScalar.of(7));
    }
  }
}
