// code by jph
package ch.ethz.idsc.owl.bot.se2.tse2;

import ch.ethz.idsc.owl.bot.tse2.Tse2CarFlows;
import ch.ethz.idsc.owl.bot.tse2.Tse2ComboRegion;
import ch.ethz.idsc.owl.bot.tse2.Tse2MinTimeGoalManager;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Degree;
import junit.framework.TestCase;

public class Tse2MinTimeGoalManagerTest extends TestCase {
  public void testSimple() {
    Tse2ComboRegion tse2ComboRegion = Tse2ComboRegion.spherical(Tensors.vector(1, 2, 3, 1), Tensors.vector(1, 1, 0.1, 1));
    Scalar MAX_TURNING_PLAN = Degree.of(30); // 45
    FlowsInterface flowsInterface = Tse2CarFlows.of(MAX_TURNING_PLAN, Tensors.vector(-2, 0, 2));
    @SuppressWarnings("unused")
    Tse2MinTimeGoalManager tse2MinTimeGoalManager = //
        new Tse2MinTimeGoalManager(tse2ComboRegion, flowsInterface.getFlows(3), RealScalar.of(2));
  }
}
