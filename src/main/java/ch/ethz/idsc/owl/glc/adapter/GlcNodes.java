// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.HeuristicFunction;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.alg.Array;

public enum GlcNodes {
  ;
  /** @param stateTime
   * @param heuristicFunction
   * @return */
  public static GlcNode createRoot(StateTime stateTime, HeuristicFunction heuristicFunction) {
    Scalar minCost = heuristicFunction.minCostToGoal(stateTime.state());
    Scalar rootCost = minCost instanceof VectorScalar ? // TODO implementation
        VectorScalar.of(Array.zeros(((VectorScalar) minCost).vector().length())) : RealScalar.ZERO;
    return GlcNode.of(null, stateTime, rootCost, minCost);
  }

  /** coarse path of {@link StateTime}s of nodes from root to given node
   * 
   * @return path to goal if found, or path to current Node in queue
   * @throws Exception if node is null */
  public static List<StateTime> getPathFromRootTo(GlcNode node) {
    return Nodes.listFromRoot(node).stream() //
        .map(GlcNode::stateTime) //
        .collect(Collectors.toList());
  }

  /** @param trajectoryPlanner
   * @return */
  // TODO not clear why this function exists
  public static Optional<GlcNode> getFinalGoalNode(TrajectoryPlanner trajectoryPlanner) {
    return HeuristicQ.of(trajectoryPlanner.getGoalInterface()) //
        ? trajectoryPlanner.getBestOrElsePeek() //
        : trajectoryPlanner.getBest();
  }

  // TODO location of function here is debatable
  public static boolean isOptimal(TrajectoryPlanner trajectoryPlanner) {
    Optional<GlcNode> best = trajectoryPlanner.getBest();
    return best.isPresent() //
        && Scalars.lessEquals( //
            best.get().costFromRoot(), //
            // in the current implementation the best node is guaranteed in queue
            trajectoryPlanner.getQueue().iterator().next().merit());
  }
}
