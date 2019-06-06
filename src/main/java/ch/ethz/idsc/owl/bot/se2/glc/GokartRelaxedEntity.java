// code by astoll
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.bot.se2.Se2ComboRegion;
import ch.ethz.idsc.owl.bot.se2.Se2MinTimeGoalManager;
import ch.ethz.idsc.owl.glc.adapter.VectorCostGoalAdapter;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.rl2.RelaxedTrajectoryPlanner;
import ch.ethz.idsc.owl.glc.rl2.StandardRelaxedLexicographicPlanner;
import ch.ethz.idsc.owl.math.region.ConeRegion;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.region.So2Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Degree;

public class GokartRelaxedEntity extends GokartEntity {
  public static GokartRelaxedEntity createRelaxedGokartEntity(StateTime stateTime, Tensor slacks) {
    return new GokartRelaxedEntity(stateTime, slacks);
  }

  // ---
  private final Tensor slacks;
  private CostFunction costFunction = null;

  private GokartRelaxedEntity(StateTime stateTime, Tensor slacks) {
    super(stateTime);
    this.slacks = slacks;
  }

  /** @param costFunction for instance, corner cutting costs */
  public void setAdditionalCostFunction(CostFunction costFunction) {
    this.costFunction = Objects.requireNonNull(costFunction);
  }

  @Override
  public final RelaxedTrajectoryPlanner createTrajectoryPlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    // define goal region
    goalRegion = getGoalRegionWithDistance(goal);
    Se2ComboRegion se2ComboRegion = new Se2ComboRegion(goalRegion, So2Region.periodic(goal.Get(2), goalRadius.Get(2)));
    // define Se2MinTimeGoalManager
    Se2MinTimeGoalManager timeCosts = new Se2MinTimeGoalManager(se2ComboRegion, controls);
    // construct cost vector
    List<CostFunction> costVector = Arrays.asList(timeCosts, costFunction);
    GoalInterface goalInterface = new VectorCostGoalAdapter(costVector, se2ComboRegion);
    // --
    return new StandardRelaxedLexicographicPlanner( //
        stateTimeRaster(), FIXEDSTATEINTEGRATOR, controls, plannerConstraint, goalInterface, slacks);
  }

  @Override
  public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
    return new ConeRegion(goal, Degree.of(18));
  }

  public Tensor getSlack() {
    return this.slacks;
  }

  @Override
  public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    getEdgeRender().setCollection(trajectoryPlanner.getQueue());
  }
}
