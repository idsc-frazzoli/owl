// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** combines multiple cost functions
 * 
 * @see GoalAdapter */
public final class MultiCostGoalAdapter implements GoalInterface, Serializable {
  /** @param goalInterface
   * @param collection
   * @return */
  public static GoalInterface of(GoalInterface goalInterface, Collection<CostFunction> collection) {
    if (collection.isEmpty())
      return goalInterface;
    List<CostFunction> list = new ArrayList<>(1 + collection.size());
    list.add(goalInterface);
    list.addAll(collection);
    return new MultiCostGoalAdapter(goalInterface, list);
  }

  /***************************************************/
  private final TrajectoryRegionQuery trajectoryRegionQuery;
  private final Collection<CostFunction> collection;

  private MultiCostGoalAdapter(TrajectoryRegionQuery trajectoryRegionQuery, Collection<CostFunction> collection) {
    this.trajectoryRegionQuery = trajectoryRegionQuery;
    this.collection = collection;
  }

  @Override // from TrajectoryRegionQuery
  public Optional<StateTime> firstMember(List<StateTime> trajectory) {
    return trajectoryRegionQuery.firstMember(trajectory);
  }

  @Override // from TrajectoryRegionQuery
  public boolean isMember(StateTime stateTime) {
    return trajectoryRegionQuery.isMember(stateTime);
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return collection.stream() //
        .map(costFunction -> costFunction.costIncrement(glcNode, trajectory, flow)) //
        .reduce(Scalar::add).get();
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    return collection.stream() //
        .map(costFunction -> costFunction.minCostToGoal(x)) //
        .reduce(Scalar::add).get();
  }
}
