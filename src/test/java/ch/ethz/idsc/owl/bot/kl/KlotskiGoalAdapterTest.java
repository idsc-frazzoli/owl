// code by jph
package ch.ethz.idsc.owl.bot.kl;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import junit.framework.TestCase;

public class KlotskiGoalAdapterTest extends TestCase {
  public void testSimple() {
    for (KlotskiProblem klotskiProblem : Huarong.values()) {
      KlotskiGoalAdapter klotskiGoalAdapter = new KlotskiGoalAdapter(klotskiProblem.getGoal());
      Scalar minCostToGoal = klotskiGoalAdapter.minCostToGoal(klotskiProblem.getBoard());
      assertEquals(minCostToGoal, RealScalar.of(3));
    }
  }

  public void testPennant() {
    for (KlotskiProblem klotskiProblem : Pennant.values()) {
      KlotskiGoalAdapter klotskiGoalAdapter = new KlotskiGoalAdapter(klotskiProblem.getGoal());
      Scalar minCostToGoal = klotskiGoalAdapter.minCostToGoal(klotskiProblem.getBoard());
      assertEquals(minCostToGoal, RealScalar.of(3));
    }
  }

  public void testTrafficJam() {
    for (KlotskiProblem klotskiProblem : TrafficJam.values()) {
      KlotskiGoalAdapter klotskiGoalAdapter = new KlotskiGoalAdapter(klotskiProblem.getGoal());
      Scalar minCostToGoal = klotskiGoalAdapter.minCostToGoal(klotskiProblem.getBoard());
      assertEquals(minCostToGoal, RealScalar.of(7));
    }
  }
}
