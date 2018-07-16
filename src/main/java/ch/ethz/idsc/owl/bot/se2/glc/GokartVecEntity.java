// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.se2.Se2ComboRegion;
import ch.ethz.idsc.owl.bot.se2.Se2MinTimeGoalManager;
import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.glc.adapter.LexicographicRelabelDecision;
import ch.ethz.idsc.owl.glc.adapter.VectorCostGoalAdapter;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.math.region.So2Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** gokart that operates on vector costs */
public class GokartVecEntity extends GokartEntity {
  public GokartVecEntity(StateTime stateTime) {
    super(stateTime);
  }

  private List<CostFunction> costVector = new ArrayList<>();
  private List<Double> slackVector = new ArrayList<>();
  // ---
  private Optional<Integer> timeCostPriority = Optional.empty();
  private Optional<Double> timeCostSlack = Optional.empty();

  @Override
  public final TrajectoryPlanner createTrajectoryPlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    goalRegion = getGoalRegionWithDistance(goal);
    Se2ComboRegion se2ComboRegion = new Se2ComboRegion(goalRegion, new So2Region(goal.Get(2), goalRadius.Get(2)));
    //  ---
    // costs with higher priority come first
    List<CostFunction> costs = new ArrayList<>();
    List<Double> slacks = new ArrayList<>();
    costs.addAll(costVector);
    slacks.addAll(slackVector);
    // ---
    if (timeCostPriority.isPresent() && timeCostSlack.isPresent()) {
      slacks.add(timeCostPriority.get(), timeCostSlack.get());
      costs.add(timeCostPriority.get(), new Se2MinTimeGoalManager(se2ComboRegion, controls));
    }
    // ---
    GoalInterface goalInterface = new VectorCostGoalAdapter(costs, se2ComboRegion);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster(), FIXEDSTATEINTEGRATOR, controls, plannerConstraint, goalInterface);
    ((StandardTrajectoryPlanner) trajectoryPlanner).relabelDecision = //
        new LexicographicRelabelDecision(Tensors.vector(slacks));
    return trajectoryPlanner;
  }

  /** Sets the cost vector and their respective slacks. Lower indices have higher priority.
   * @param costVector
   * @param slackVector */
  public void setCostVector(List<CostFunction> costVector, List<Double> slackVector) {
    GlobalAssert.that(costVector.size() == slackVector.size());
    this.costVector = costVector;
    this.slackVector = slackVector;
  }

  /** Add time cost to the cost vector
   * @param priority
   * @param slack */
  public void addTimeCost(int priority, Double slack) {
    this.timeCostPriority = Optional.of(priority);
    this.timeCostSlack = Optional.of(slack);
  }
}
