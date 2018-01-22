// code by bapaden, jph, and jl
package ch.ethz.idsc.owl.glc.std;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
 * <li>parallel processing of queues
 * <li>nodes that get replaced in a domain, are also removed from the queue
 * </ul> */
public class StandardTrajectoryPlanner extends AbstractTrajectoryPlanner {
  /** minimum threshold of improvement by a candidate */
  // TODO probably should be relative to order of magnitude of merit
  private static final Scalar MERIT_EPS = DoubleScalar.of(1E-6);
  // ---
  private transient final ControlsIntegrator controlsIntegrator;

  public StandardTrajectoryPlanner( //
      Tensor eta, //
      StateIntegrator stateIntegrator, //
      Collection<Flow> controls, //
      TrajectoryRegionQuery obstacleQuery, //
      GoalInterface goalInterface) {
    super(eta, stateIntegrator, obstacleQuery, goalInterface);
    controlsIntegrator = new ControlsIntegrator( //
        stateIntegrator, //
        () -> controls.stream().parallel(), //
        goalInterface);
  }

  @Override // from ExpandInterface
  public void expand(final GlcNode node) {
    Map<GlcNode, List<StateTime>> connectors = controlsIntegrator.from(node);
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
    // ---
    domainQueueMap.map.entrySet().stream().parallel() //
        .forEach(entry -> processCandidates(node, connectors, entry.getKey(), entry.getValue()));
  }

  private void processCandidates( //
      GlcNode node, Map<GlcNode, List<StateTime>> connectors, Tensor domainKey, DomainQueue domainQueue) {
    for (GlcNode next : domainQueue) { // iterate over the candidates in DomainQueue
      final List<StateTime> trajectory = connectors.get(next);
      if (!getObstacleQuery().firstMember(trajectory).isPresent()) {
        Optional<GlcNode> former = getNode(domainKey);
        boolean isPresent = former.isPresent();
        synchronized (this) {
          if (isPresent) { // is already some node present from previous exploration ?
            GlcNode formerLabel = former.get();
            boolean removed = queue().remove(formerLabel);
            if (!removed) // resolved by introduction of MERIT_EPS
              System.err.println("miss: " + domainKey + "\n " + formerLabel.stateTime().toInfoString());
            formerLabel.parent().removeEdgeTo(formerLabel);
          }
          node.insertEdgeTo(next);
          insert(domainKey, next); // replaces former node if present
          if (isInsideGoal(trajectory)) // GOAL check
            offerDestination(next, trajectory);
        }
        break; // as in B. Paden's implementation: leaving loop after first relabel
      }
    }
  }
}
