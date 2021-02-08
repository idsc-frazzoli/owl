// code by ynager
package ch.ethz.idsc.owl.glc.rl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.glc.core.ControlsIntegrator;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.InvariantFlows;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.math.VectorScalars;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

public class StandardRLTrajectoryPlanner extends RLTrajectoryPlanner {
  private final StateIntegrator stateIntegrator;
  private final PlannerConstraint plannerConstraint;
  private final GoalInterface goalInterface;
  private final ControlsIntegrator controlsIntegrator;
  // ---
  private final SlackWrap slackWrap;
  private final Tensor slacks;

  public StandardRLTrajectoryPlanner( //
      StateTimeRaster stateTimeRaster, //
      StateIntegrator stateIntegrator, //
      Collection<Tensor> controls, //
      PlannerConstraint plannerConstraint, //
      GoalInterface goalInterface, //
      Tensor slacks) {
    super(stateTimeRaster, goalInterface, slacks);
    this.slackWrap = new SlackWrap(slacks);
    this.slacks = slacks;
    this.stateIntegrator = stateIntegrator;
    this.plannerConstraint = Objects.requireNonNull(plannerConstraint);
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
    RLDomainQueueMap domainQueueMap = new RLDomainQueueMap(slacks); // holds candidates for insertion
    for (GlcNode next : connectors.keySet()) { // <- order of keys is non-deterministic
      final Tensor domainKey = stateTimeRaster.convertToKey(next.stateTime());
      Optional<RLDomainQueue> formerQueue = getDomainQueue(domainKey);
      if (formerQueue.isPresent()) { // is already some domain queue present from previous exploration ?
        if (isWithinSlack(next, formerQueue.get()) && !StaticHelper.isEqual(next, formerQueue.get()))
          domainQueueMap.addToDomainMap(domainKey, next); // new node lies within slack, potentially better than previous ones
      } else
        domainQueueMap.addToDomainMap(domainKey, next); // node is considered without comparison to any former node
    }
    // ---
    domainQueueMap.mapEntrySetStream() //
        .parallel() // all operations thread safe?
        .forEach(entry -> processCandidates(node, connectors, entry.getKey(), entry.getValue()));
  }

  private boolean isWithinSlack(GlcNode next, RLDomainQueue domainQueue) {
    return slackWrap.isWithin( //
        VectorScalars.vector(next.merit()), //
        domainQueue.getMinValues().get());
  }

  private void processCandidates( //
      GlcNode node, Map<GlcNode, List<StateTime>> connectors, Tensor domainKey, RLDomainQueue domainQueue) {
    // iterate over the candidates in DomainQueue
    for (GlcNode next : domainQueue) {
      final List<StateTime> trajectory = connectors.get(next);
      if (plannerConstraint.isSatisfied(node, trajectory, next.flow())) {
        Optional<RLDomainQueue> former = getDomainQueue(domainKey);
        boolean isPresent = former.isPresent();
        // ---
        synchronized (this) {
          if (isPresent) { // are already nodes present from previous exploration ?
            RLDomainQueue formerQueue = former.get();
            Tensor minValues = formerQueue.getMinValues().get();
            Tensor merit = VectorScalars.vector(next.merit());
            for (int i = 0; i < slacks.length(); ++i) // find nodes outside of bounds
              if (Scalars.lessThan(merit.Get(i), minValues.Get(i))) { // cost lower than prev min?
                Scalar margin = merit.Get(i).add(slacks.Get(i));
                final int j = i;
                List<GlcNode> toRemove = formerQueue.stream() // find nodes to be removed
                    .filter(glcNode -> Scalars.lessThan( //
                        margin, // lhs
                        VectorScalars.at(glcNode.merit(), j) // rhs
                    )).collect(Collectors.toList());
                //
                if (!toRemove.isEmpty()) {
                  boolean removed = queue().removeAll(toRemove); // remove bad nodes from OPEN queue
                  formerQueue.removeAll(toRemove); // remove bad nodes from domain queue
                  toRemove.stream().forEach(Nodes::disjoinChild); // remove edges from parent
                  if (!removed) //
                    System.err.println("miss - nodes to be removed dont exist " + domainKey);
                }
              }
          }
          addToDomainMap(domainKey, next); // insert node to domain queue
          node.insertEdgeTo(next);
          addToOpen(domainKey, next); // insert node into OPEN
          if (goalInterface.firstMember(trajectory).isPresent()) // GOAL check
            offerDestination(next, trajectory);
        }
        break; // as in B. Paden's implementation: leaving loop after first relabel
      }
    }
  }

  @Override // from TrajectoryPlanner
  public final StateIntegrator getStateIntegrator() {
    return stateIntegrator;
  }
}
