// code by bapaden, jph, and jl
package ch.ethz.idsc.owl.glc.std;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
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

/** translation of the c++ implementation by bapaden
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
    integratorWatch.start();
    Map<GlcNode, List<StateTime>> connectors = controlsIntegrator.inParallel(node);
    // ---
    DomainQueueMap domainQueueMap = new DomainQueueMap(); // holds candidates for insertion
    for (GlcNode next : connectors.keySet()) { // <- order of keys is non-deterministic
      final Tensor domainKey = convertToKey(next.stateTime());
      final GlcNode former = getNode(domainKey);
      if (Objects.nonNull(former)) {
        // is already some node present from previous exploration ?
        if (Scalars.lessThan(next.merit(), former.merit())) // new node is potentially better than previous one
          domainQueueMap.insert(domainKey, next);
      } else
        domainQueueMap.insert(domainKey, next); // node is considered without comparison to any former node
    }
    integratorWatch.stop();
    processCWatch.start();
    processCandidates(node, connectors, domainQueueMap);
    processCWatch.stop();
  }

  private void processCandidates( //
      GlcNode node, Map<GlcNode, List<StateTime>> connectors, DomainQueueMap domainQueueMap) {
    for (Entry<Tensor, DomainQueue> entry : domainQueueMap.map.entrySet()) {
      final Tensor domainKey = entry.getKey();
      final DomainQueue domainQueue = entry.getValue();
      while (!domainQueue.isEmpty()) {
        final GlcNode next = domainQueue.element();
        final GlcNode formerLabel = getNode(domainKey);
        if (Objects.nonNull(formerLabel)) {
          if (Scalars.lessThan(next.merit(), formerLabel.merit())) {
            if (!getObstacleQuery().firstMember(connectors.get(next)).isPresent()) { // no collision
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
              if (isInsideGoal(connectors.get(next)))
                offerDestination(next, connectors.get(next));
              // Same principle as in B. Paden's implementation, leaving while loop after first relabel
              break; // leaves the while loop, but not the for loop
            }
          }
        } else { // No formerLabel, so definitely adding a Node
          if (!getObstacleQuery().firstMember(connectors.get(next)).isPresent()) {
            // removing the nextCandidate from bucket of this domain
            // adding next to tree and DomainMap
            node.insertEdgeTo(next);
            insert(domainKey, next);
            domainQueue.remove();
            // GOAL check
            if (isInsideGoal(connectors.get(next)))
              offerDestination(next, connectors.get(next));
            break;
          }
        }
        domainQueue.remove();
      }
    }
  }

  @Override
  protected Optional<GlcNode> getFurthestGoalNode() {
    // TODO JONAS operation is not defined for standard planner, but some demos (deltaxt...) use it !!!
    // throw new UnsupportedOperationException();
    return Optional.empty();
  }

  @Override
  public String infoString() {
    StringBuilder stringBuilder = new StringBuilder(super.infoString() + ", ");
    stringBuilder.append("default...");
    return stringBuilder.toString();
  }
}
