// code by jph
package ch.ethz.idsc.owl.bot.rn;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.r2.R2Flows;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class RnMinTimeGoalManagerTest extends TestCase {
  public void testSimple() {
    R2Flows r2Flows = new R2Flows(Quantity.of(2, "m*s^-1"));
    Collection<Flow> controls = r2Flows.getFlows(10);
    Tensor center = Tensors.fromString("{3[m], 6[m]}");
    Scalar radius = Quantity.of(1, "m");
    RegionWithDistance<Tensor> regionWithDistance = new SphericalRegion(center, radius);
    GoalInterface goalInterface = RnMinTimeGoalManager.create(regionWithDistance, controls);
    // Scalar cost = ;
    assertEquals(goalInterface.minCostToGoal(Tensors.fromString("{3[m], 6[m]}")), Quantity.of(0, "s"));
    assertEquals(goalInterface.minCostToGoal(Tensors.fromString("{2[m], 6[m]}")), Quantity.of(0, "s"));
    Chop._14.requireClose(goalInterface.minCostToGoal( //
        Tensors.fromString("{0[m], 6[m]}")), Quantity.of(1, "s"));
  }
}
