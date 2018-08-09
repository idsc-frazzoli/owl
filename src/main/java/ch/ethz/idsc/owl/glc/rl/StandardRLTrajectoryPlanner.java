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
  private final static Tensor SLACKS = Tensors.vector(0, 0); // FIXME
  private final int costSize = SLACKS.length(); // FIXME

  public StandardRLTrajectoryPlanner( //
      StateTimeRaster stateTimeRaster, //
      StateIntegrator stateIntegrator, //
      Collection<Flow> controls, //
      PlannerConstraint plannerConstraint, //
      GoalInterface goalInterface) {
    super(stateTimeRaster, goalInterface, SLACKS);
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
    RLDomainQueueMap domainQueueMap = new RLDomainQueueMap(SLACKS); // holds candidates for insertion
    for (GlcNode next : connectors.keySet()) { // <- order of keys is non-deterministic
      final Tensor domainKey = stateTimeRaster.convertToKey(next.stateTime());
      Optional<RLDomainQueue> former = getNode(domainKey);
      if (former.isPresent()) { // is already some domain queue present from previous exploration ?
        if (isWithinSlack(next, former.get()))
          domainQueueMap.put(domainKey, next); // new node lies within slack, potentially better than previous ones
      } else
        domainQueueMap.put(domainKey, next); // node is considered without comparison to any former node
    }
    // ---
    domainQueueMap.map.entrySet().stream().parallel() //
        .forEach(entry -> processCandidates(node, connectors, entry.getKey(), entry.getValue()));
  }

  private boolean isWithinSlack(GlcNode next, RLDomainQueue domainQueue) {
    Tensor merit = ((VectorScalar) next.merit()).vector();
    for (int i = 0; i < costSize; i++) {
      if (Scalars.lessThan(domainQueue.getBounds().Get(i), merit.Get(i)))
        return false; // cost out of slack bounds
    }
    return true;
  }

  private void processCandidates( //
      GlcNode node, Map<GlcNode, List<StateTime>> connectors, Tensor domainKey, RLDomainQueue domainQueue) {
    for (GlcNode next : domainQueue) { // iterate over the candidates in DomainQueue
      final List<StateTime> trajectory = connectors.get(next);
      if (plannerConstraint.isSatisfied(node, trajectory, next.flow())) {
        Optional<RLDomainQueue> former = getNode(domainKey);
        boolean isPresent = former.isPresent();
        // ---
        synchronized (this) {
          if (isPresent) { // are already nodes present from previous exploration ?
            RLDomainQueue formerQueue = former.get();
            Tensor bounds = formerQueue.getBounds();
            // find nodes to be removed from OPEN queue
            formerQueue.add(next); // add node to domainQueue
            for (int i = 0; i < costSize; i++) {
              // is cost lower than prev min?
              if (Scalars.lessThan(((VectorScalar) next.merit()).vector().Get(i), formerQueue.getMinValues().Get(i))) {
                final int j = i;
                List<GlcNode> toRemove = formerQueue.queue.stream() // find nodes to be removed
                    .filter(n -> Scalars.lessThan(bounds.Get(j), ((VectorScalar) n.merit()).vector().Get(j))).collect(Collectors.toList());
                if (!toRemove.isEmpty()) {
                  boolean removed = queue().removeAll(toRemove); // remove bad nodes from OPEN queue
                  formerQueue.removeAll(toRemove); // remove bad nodes from domainqueue
                  toRemove.stream().forEach(n -> n.parent().removeEdgeTo(n)); // remove edges from parent TODO check if correct
                  if (!removed) //
                    System.err.println("miss: " + domainKey);
                }
              }
            }
          }
          node.insertEdgeTo(next);
          insert(domainKey, next); // insert node into OPEN queue and domainQueueMap
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
