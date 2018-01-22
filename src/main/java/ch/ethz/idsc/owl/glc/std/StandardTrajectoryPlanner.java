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
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Sign;

/** transcription of the c++ implementation by bapaden
 * 
 * subsequent modifications include:
 * <ul>
 * <li>parallel integration of trajectories
 * <li>nodes that get replaced in a domain, are also removed from the queue
 * </ul> */
public class StandardTrajectoryPlanner extends AbstractTrajectoryPlanner {
  /** minimum threshold of improvement by a candidate */
  // TODO probably should be relative to order of magnitude of merit
  private static final Scalar MERIT_EPS = DoubleScalar.of(1E-6);
  // ---
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
      Optional<GlcNode> former = getNode(domainKey);
      if (former.isPresent()) { // is already some node present from previous exploration ?
        GlcNode formerLabel = former.get();
        Scalar delta = formerLabel.merit().subtract(next.merit());
        boolean passed = Scalars.lessThan(MERIT_EPS, delta);
        passed |= ExactScalarQ.of(delta) && Sign.isPositive(delta);
        if (passed) // new node is potentially better than previous one
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
      for (GlcNode next : entry.getValue()) { // iterate over the candidates in DomainQueue
        final List<StateTime> trajectory = connectors.get(next);
        Optional<GlcNode> former = getNode(domainKey);
        if (!getObstacleQuery().firstMember(trajectory).isPresent()) {
          boolean isPresent = former.isPresent();
          if (isPresent) { // is already some node present from previous exploration ?
            GlcNode formerLabel = former.get();
            boolean removed = queue().remove(formerLabel);
            if (!removed) // resolved by introduction of MERIT_EPS
              System.err.println("miss: " + domainKey + "\n " + formerLabel.stateTime().toInfoString());
            formerLabel.parent().removeEdgeTo(formerLabel);
          }
          node.insertEdgeTo(next);
          boolean replaced = insert(domainKey, next);
          GlobalAssert.that(replaced || !isPresent);
          if (isInsideGoal(trajectory)) // GOAL check
            offerDestination(next, trajectory);
          // Same principle as in B. Paden's implementation, leaving while loop after first relabel
          break; // leaves the inner for loop, but not the outer for loop
        }
      }
    }
  }
}
