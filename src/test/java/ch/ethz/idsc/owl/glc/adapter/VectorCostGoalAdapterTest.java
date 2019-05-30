// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.owl.bot.se2.Se2ComboRegion;
import ch.ethz.idsc.owl.bot.se2.Se2MinTimeGoalManager;
import ch.ethz.idsc.owl.bot.se2.glc.Se2CarFlows;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.region.Regions;
import ch.ethz.idsc.sophus.VectorScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class VectorCostGoalAdapterTest extends TestCase {
  public void testSimple() {
    List<CostFunction> costs = new ArrayList<>();
    costs.add(new Se2MinTimeGoalManager( //
        Se2ComboRegion.spherical(Tensors.vector(2, 1, Math.PI * -1), Tensors.vector(0.1, 0.1, 10 / 180 * Math.PI)), //
        Se2CarFlows.standard(RealScalar.of(1), Degree.of(35)).getFlows(10)));
    costs.add(new Se2MinTimeGoalManager( //
        Se2ComboRegion.spherical(Tensors.vector(2, 1, Math.PI * -1), Tensors.vector(0.1, 0.1, 10 / 180 * Math.PI)), //
        Se2CarFlows.standard(RealScalar.of(2), Degree.of(35)).getFlows(10)));
    GoalInterface goalInterface = new VectorCostGoalAdapter(costs, Regions.emptyRegion());
    Scalar minCostToGoal = goalInterface.minCostToGoal(Tensors.vector(0, 0, 0));
    VectorScalar vs = (VectorScalar) minCostToGoal;
    Tensor vector = vs.vector();
    assertTrue(Chop._13.close(vector.Get(0).subtract(vector.Get(1)), vector.Get(1)));
  }
}
