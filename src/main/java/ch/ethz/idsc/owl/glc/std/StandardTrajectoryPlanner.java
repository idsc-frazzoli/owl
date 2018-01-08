// code by bapaden, jph, and jl
package ch.ethz.idsc.owl.glc.std;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.glc.core.AbstractTrajectoryPlanner;
import ch.ethz.idsc.owl.glc.core.ControlsIntegrator;
import ch.ethz.idsc.owl.glc.core.DomainQueue;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

/** transcription of the c++ implementation by bapaden
 * 
 * subsequent modifications include:
 * <ul>
 * <li>parallel integration of trajectories
 * <li>nodes that get replaced in a domain, are also removed from the queue
 * </ul> */
public class StandardTrajectoryPlanner extends AbstractTrajectoryPlanner {
  private final ControlsIntegrator controlsIntegrator;

  public StandardTrajectoryPlanner( //
      Tensor eta, //
      StateIntegrator stateIntegrator, //
      Collection<Flow> controls, //
      TrajectoryRegionQuery obstacleQuery, //
      GoalInterface goalInterface) {
    super(eta, stateIntegrator, obstacleQuery, goalInterface);
    controlsIntegrator = new ControlsIntegrator(stateIntegrator, controls, goalInterface);
  }

  @Override // from ExpandInterface
  public void expand(final GlcNode node) {
    Map<GlcNode, List<StateTime>> connectors = controlsIntegrator.inParallel(node);
    // ---
    DomainQueueMap domainQueueMap = new DomainQueueMap(); // holds candidates for insertion
    for (GlcNode next : connectors.keySet()) { // <- order of keys is non-deterministic
      final Tensor domainKey = convertToKey(next.stateTime());
      final Optional<GlcNode> former = getNode(domainKey);
      if (former.isPresent()) {
        // is already some node present from previous exploration ?
        if (Scalars.lessThan(next.merit(), former.get().merit())) // new node is potentially better than previous one
          domainQueueMap.insert(domainKey, next);
      } else
        domainQueueMap.insert(domainKey, next); // node is considered without comparison to any former node
    }
    processCandidates(node, connectors, domainQueueMap);
  }

  private void processCandidates( //
      GlcNode node, Map<GlcNode, List<StateTime>> connectors, DomainQueueMap domainQueueMap) {
    for (Entry<Tensor, DomainQueue> entry : domainQueueMap) {
      final Tensor domainKey = entry.getKey();
      final DomainQueue domainQueue = entry.getValue();
      while (!domainQueue.isEmpty()) {
        final GlcNode next = domainQueue.element(); // retrieves, but does not remove, the head of this queue
        final List<StateTime> trajectory = connectors.get(next);
        final Optional<GlcNode> former = getNode(domainKey);
        if (former.isPresent()) {
          GlcNode formerLabel = former.get();
          if (Scalars.lessThan(next.merit(), formerLabel.merit())) {
            if (!getObstacleQuery().firstMember(trajectory).isPresent()) { // no collision
              /** removal from queue is unsure; needs to be checked with theory. */
              boolean removed = queue().remove(formerLabel);
              if (!removed) {
                System.err.println("miss: " + domainKey + " " + formerLabel.stateTime().toInfoString());
                // GlobalAssert.that(removed);
              }
              formerLabel.parent().removeEdgeTo(formerLabel);
              node.insertEdgeTo(next);
              boolean replaced = insert(domainKey, next);
              GlobalAssert.that(replaced);
              domainQueue.remove();
              if (isInsideGoal(trajectory))
                offerDestination(next, trajectory);
              // Same principle as in B. Paden's implementation, leaving while loop after first relabel
              break; // leaves the while loop, but not the for loop
            }
          }
        } else { // no formerLabel, so definitely adding a Node
          if (!getObstacleQuery().firstMember(trajectory).isPresent()) {
            // removing the nextCandidate from bucket of this domain
            // adding next to tree and DomainMap
            node.insertEdgeTo(next);
            insert(domainKey, next);
            domainQueue.remove();
            // GOAL check
            if (isInsideGoal(trajectory))
              offerDestination(next, trajectory);
            break;
          }
        }
        domainQueue.remove();
      }
    }
  }
}
