// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.se2.Se2ComboRegion;
import ch.ethz.idsc.owl.bot.se2.Se2MinTimeGoalManager;
import ch.ethz.idsc.owl.glc.adapter.VectorCostGoalAdapter;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.LexicographicRelabelDecision;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.math.region.So2Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

/** gokart that operates on vector costs */
public class GokartVecEntity extends GokartEntity {
  public GokartVecEntity(StateTime stateTime) {
    super(stateTime);
  }

  @Override
  public final TrajectoryPlanner createTrajectoryPlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    goalRegion = getGoalRegionWithDistance(goal);
    Se2ComboRegion se2ComboRegion = new Se2ComboRegion(goalRegion, new So2Region(goal.Get(2), goalRadius.Get(2)));
    //  ---
    // costs with higher priority come first
    // TODO: add costs / slack from within demo
    List<CostFunction> costs = new ArrayList<>();
    getPrimaryCost().map(costs::add);
    costs.add(new Se2MinTimeGoalManager(se2ComboRegion, controls));
    // ---
    GoalInterface goalInterface = new VectorCostGoalAdapter(costs, se2ComboRegion);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster(), FIXEDSTATEINTEGRATOR, controls, plannerConstraint, goalInterface);
    //  ---
    Tensor slack = Array.zeros(costs.size()); // slack equal to zero for now
    ((StandardTrajectoryPlanner) trajectoryPlanner).relabelDecision = new LexicographicRelabelDecision(slack);
    // ---
    return trajectoryPlanner;
  }

  public Optional<CostFunction> getPrimaryCost() {
    return Optional.empty();
  }
}
