// code by astoll, ynager
package ch.ethz.idsc.owl.glc.rl2;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.glc.core.ControlsIntegrator;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.InvariantFlows;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;

/** Motion planning algorithm using a lexicographic semiorder as preference structure to evaluate which trajectories are deemed better than others.
 * The algorithm is an adaptation of the Generalized Labeling Correcting method.
 * 
 * For a detailed description, see Chapter 7.1 in "Multi-Objective Optimization Using Preference Structures". */
public class StandardRelaxedLexicographicPlanner extends RelaxedTrajectoryPlanner {
  private final StateIntegrator stateIntegrator;
  private final PlannerConstraint plannerConstraint;
  private final GoalInterface goalInterface;
  private final ControlsIntegrator controlsIntegrator;

  public StandardRelaxedLexicographicPlanner(//
      StateTimeRaster stateTimeRaster, //
      StateIntegrator stateIntegrator, //
      Collection<Tensor> controls, //
      PlannerConstraint plannerConstraints, //
      GoalInterface goalInterface, //
      Tensor slacks) {
    super(stateTimeRaster, goalInterface, slacks);
    this.stateIntegrator = stateIntegrator;
    this.plannerConstraint = Objects.requireNonNull(plannerConstraints);
    this.goalInterface = goalInterface;
    controlsIntegrator = new ControlsIntegrator( //
        stateIntegrator, //
        new InvariantFlows(controls), //
        goalInterface);
  }

  @Override // from ExpandInterface
  public void expand(final GlcNode node) {
    Map<GlcNode, List<StateTime>> connectors = controlsIntegrator.from(node);
    // ---
    for (GlcNode next : connectors.keySet()) { // <- order of keys is non-deterministic
      final Tensor domainKey = stateTimeRaster.convertToKey(next.stateTime());
      final List<StateTime> trajectory = connectors.get(next);
      // check if planner constraints are satisfied otherwise discard next
      if (plannerConstraint.isSatisfied(node, trajectory, next.flow())) {
        // potentially add next to domainMap and save eventual discarded nodes
        Collection<GlcNode> discardedNodes = addToDomainMap(domainKey, next);
        // add next to global queue if accept by domainQueue and insert edge
        if (!discardedNodes.contains(next)) {
          addToGlobalQueue(next);
          node.insertEdgeTo(next);
          // check if trajectory went through goal region and to goalDomainQueue if so
          if (goalInterface.firstMember(trajectory).isPresent()) // GOAL check
            offerDestination(next, trajectory);
        }
        if (!discardedNodes.isEmpty() && !discardedNodes.contains(next)) {
          //
          // remove all discarded nodes in GlobalQueue from it
          removeChildren(discardedNodes);
        }
      }
    }
    // RelaxedDebugUtils.closeMatchesCheck(this);
    // RelaxedDebugUtils.globalQueueSubsetOfQueuesInDomainMap(this);
    // RelaxedDebugUtils.nodeAmountCompare(this);
  }

  /** removes the nodes in the collection from the planner as well as its children
   * 
   * @param collection */
  void removeChildren(Collection<GlcNode> collection) {
    for (GlcNode glcNode : collection) {
      removeChildren(glcNode.children()); // recursive call to remove all children
      Nodes.disjoinChild(glcNode); // disconnect from parent
      Tensor domainKey = stateTimeRaster.convertToKey(glcNode.stateTime());
      boolean removed = getGlobalQueue().remove(glcNode); // remove from globalQueue
      if (!removed)
        System.err.println("warning, node not removed");
      removeFromDomainQueue(domainKey, glcNode);
    }
  }

  @Override // from TrajectoryPlanner
  public final StateIntegrator getStateIntegrator() {
    return stateIntegrator;
  }
}
