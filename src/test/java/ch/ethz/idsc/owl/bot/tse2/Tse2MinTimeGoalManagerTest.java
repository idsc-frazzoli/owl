// code by jph
package ch.ethz.idsc.owl.bot.tse2;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class Tse2MinTimeGoalManagerTest extends TestCase {
  public void testSimple() {
    Tse2ComboRegion tse2ComboRegion = Tse2ComboRegion.spherical(Tensors.vector(1, 2, 3, 1), Tensors.vector(1, 1, 0.1, 1));
    Scalar MAX_TURNING_PLAN = Degree.of(30); // 45
    FlowsInterface flowsInterface = Tse2CarFlows.of(MAX_TURNING_PLAN, Tensors.vector(-2, 0, 2));
    Tse2MinTimeGoalManager tse2MinTimeGoalManager = //
        new Tse2MinTimeGoalManager(tse2ComboRegion, flowsInterface.getFlows(3), RealScalar.of(2));
    Scalar minCostToGoal = tse2MinTimeGoalManager.minCostToGoal(Tensors.fromString("{1,13,3,1}"));
    assertTrue(Scalars.lessEquals(RealScalar.of(5), minCostToGoal));
  }

  public void testQuantity() {
    Tse2ComboRegion tse2ComboRegion = Tse2ComboRegion.spherical( //
        Tensors.fromString("{10[m], 20[m], 3, 2[m*s^-1]}"), //
        Tensors.fromString("{1[m], 1[m], 1, 1[m*s^-1]}"));
    // Scalar MAX_TURNING_PLAN = Degree.of(30); // 45
    FlowsInterface flowsInterface = Tse2CarFlows.of(Quantity.of(.3, ""), Tensors.fromString("{-1[m*s^-2],0[m*s^-2],1/2[m*s^-2]}"));
    // FIXME JPH doesn't work
    @SuppressWarnings("unused")
    Tse2MinTimeGoalManager tse2MinTimeGoalManager = //
        new Tse2MinTimeGoalManager(tse2ComboRegion, flowsInterface.getFlows(3), Quantity.of(2, "m*s^-1"));
    // Scalar minCostToGoal = tse2MinTimeGoalManager.minCostToGoal( //
    // Tensors.fromString("{1[m],13[m],3,5[m*s^-1]}"));
    // assertTrue(Scalars.lessEquals(RealScalar.of(5), minCostToGoal));
  }
}
