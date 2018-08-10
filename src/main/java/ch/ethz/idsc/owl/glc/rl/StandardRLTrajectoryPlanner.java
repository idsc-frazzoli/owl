// code by ynager
package ch.ethz.idsc.owl.glc.rl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.glc.core.ControlsIntegrator;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Sign;

/** transcription of the c++ implementation by bapaden
 * 
 * subsequent modifications include:
 * <ul>
 * <li>parallel integration of trajectories
 * <li>parallel processing of queues
 * <li>nodes that get replaced in a domain, are also removed from the queue
 * </ul> */
public class StandardRLTrajectoryPlanner extends RLTrajectoryPlanner {
  private final StateIntegrator stateIntegrator;
  private final PlannerConstraint plannerConstraint;
  private final GoalInterface goalInterface;
  private transient final ControlsIntegrator controlsIntegrator;
  // ---
  private final Tensor slacks;
  private final int costSize;

  public StandardRLTrajectoryPlanner( //
      StateTimeRaster stateTimeRaster, //
      StateIntegrator stateIntegrator, //
      Collection<Flow> controls, //
      PlannerConstraint plannerConstraint, //
      GoalInterface goalInterface, 
      Tensor slacks) {
    super(stateTimeRaster, goalInterface, slacks);
    this.slacks = slacks;
    this.costSize = slacks.length();
    this.stateIntegrator = stateIntegrator;
    this.plannerConstraint = Objects.requireNonNull(plannerConstraint);
    this.goalInterface = goalInterface;
    controlsIntegrator = new ControlsIntegrator( //
        stateIntegrator, //
        () -> controls.stream().parallel(), //
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
        if (isWithinSlack(next, formerQueue.get())) {
          domainQueueMap.put(domainKey, next); // new node lies within slack, potentially better than previous ones
        }
      } else
        domainQueueMap.put(domainKey, next); // node is considered without comparison to any former node
    }
    // ---
    domainQueueMap.map.entrySet().stream() // FIXME make parallel
        .forEach(entry -> processCandidates(node, connectors, entry.getKey(), entry.getValue()));
  }

  private boolean isWithinSlack(GlcNode next, RLDomainQueue domainQueue) {
    Tensor merit = ((VectorScalar) next.merit()).vector();
    Tensor diff = domainQueue.getMinValues().add(slacks).subtract(merit);
    return !diff.stream().anyMatch(c -> Sign.isNegative(c.Get()));
  }

  private void processCandidates( //
      GlcNode node, Map<GlcNode, List<StateTime>> connectors, Tensor domainKey, RLDomainQueue domainQueue) {
    for (GlcNode next : domainQueue) { // iterate over the candidates in DomainQueue
      final List<StateTime> trajectory = connectors.get(next);
      if (plannerConstraint.isSatisfied(node, trajectory, next.flow())) {
        Optional<RLDomainQueue> former = getDomainQueue(domainKey);
        boolean isPresent = former.isPresent();
        // ---
        synchronized (this) {
          if (isPresent) { // are already nodes present from previous exploration ?
            RLDomainQueue formerQueue = former.get();
            Tensor minValues = formerQueue.getMinValues();
            Tensor merits = ((VectorScalar) next.merit()).vector();
            // find nodes outside of bounds
            for (int i = 0; i < costSize; i++) {
              // is cost lower than prev min?
              if (Scalars.lessThan(merits.Get(i), minValues.Get(i))) {
                final int j = i;
                List<GlcNode> toRemove = formerQueue.queue.stream() // find nodes to be removed
                    .filter(n -> Scalars.lessThan(merits.Get(j).add(slacks.Get(j)), //
                        ((VectorScalar) n.merit()).vector().Get(j)))
                    .collect(Collectors.toList());
                //
                if (!toRemove.isEmpty()) {
                  boolean removed = queue().removeAll(toRemove); // remove bad nodes from OPEN queue
                  formerQueue.removeAll(toRemove); // remove bad nodes from domain queue
                  toRemove.stream().distinct().forEach(n -> n.parent().removeEdgeTo(n)); // remove edges from parent
                  if (!removed) //
                    System.err.println("miss - nodes to be removed dont exist " + domainKey);
                }
              }
            }
          }
          // TODO only add if cost distance to existing nodes in domain is above threshold
          node.insertEdgeTo(next);
          addToDomainMap(domainKey, next); // insert node to domain queue
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
