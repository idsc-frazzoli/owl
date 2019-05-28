// code by astoll
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.se2.Se2ComboRegion;
import ch.ethz.idsc.owl.bot.se2.Se2MinTimeGoalManager;
import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.glc.adapter.VectorCostGoalAdapter;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.rl2.StandardRelaxedLexicographicPlanner;
import ch.ethz.idsc.owl.math.region.So2Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class GokartRelaxedEntity extends GokartEntity {
  public GokartRelaxedEntity(StateTime stateTime) {
    super(stateTime);
  }

  private List<CostFunction> costVector = new ArrayList<>();
  private Tensor slackVector;
  // ---
  private Optional<Integer> timeCostPriority = Optional.empty();
  private Optional<Scalar> timeCostSlack = Optional.empty();

  @Override
  public final TrajectoryPlanner createTrajectoryPlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    goalRegion = getGoalRegionWithDistance(goal);
    Se2ComboRegion se2ComboRegion = new Se2ComboRegion(goalRegion, So2Region.periodic(goal.Get(2), goalRadius.Get(2)));
    //  ---
    // costs with higher priority come first
    List<CostFunction> costs = new ArrayList<>();
    costs.addAll(costVector);
    Tensor slacks = this.slackVector;
    // ---
    if (timeCostPriority.isPresent() && timeCostSlack.isPresent()) {
      Tensor slackWithTime = slacks.extract(0, timeCostPriority.get())
          .append(timeCostSlack.get().append(slacks.extract(timeCostPriority.get(), slacks.length() + 1)));
      slacks = slackWithTime;
      costs.add(timeCostPriority.get(), new Se2MinTimeGoalManager(se2ComboRegion, controls));
    }
    // ---
    GoalInterface goalInterface = new VectorCostGoalAdapter(costs, se2ComboRegion);
    return new StandardRelaxedLexicographicPlanner( //
        stateTimeRaster(), FIXEDSTATEINTEGRATOR, controls, plannerConstraint, goalInterface, slacks);
  }

  /** Sets the cost vector and their respective slacks. Lower indices have higher priority.
   * @param costVector
   * @param slackVector */
  public void setCostVector(List<CostFunction> costVector, Tensor slackVector) {
    GlobalAssert.that(costVector.size() == slackVector.length());
    this.costVector = costVector;
    this.slackVector = slackVector;
  }

  /** Add time cost to the cost vector insert in slack vector according to priority, where highest priority is 0
   * @param priority
   * @param slack */
  public void addTimeCost(int priority, Scalar slack) {
    this.timeCostPriority = Optional.of(priority);
    this.timeCostSlack = Optional.of(slack);
  }
}
