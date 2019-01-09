// code by bapaden, jph, jl, and ynager
package ch.ethz.idsc.owl.glc.std;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.owl.glc.adapter.SimpleRelabelDecision;
import ch.ethz.idsc.owl.glc.core.CTrajectoryPlanner;
import ch.ethz.idsc.owl.glc.core.ControlsIntegrator;
import ch.ethz.idsc.owl.glc.core.DomainQueue;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.RelabelDecision;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;

/** transcription of the c++ implementation by bapaden
 * 
 * subsequent modifications include:
 * <ul>
 * <li>parallel integration of trajectories
 * <li>parallel processing of queues
 * <li>nodes that get replaced in a domain, are also removed from the queue
 * </ul> */
public class StandardTrajectoryPlanner extends CTrajectoryPlanner {
  private final StateIntegrator stateIntegrator;
  private final PlannerConstraint plannerConstraint;
  private final GoalInterface goalInterface;
  private transient final ControlsIntegrator controlsIntegrator;
  /** decides if new node is better than existing node
   * TODO JPH API design not final */
  public RelabelDecision relabelDecision = SimpleRelabelDecision.INSTANCE;

  public StandardTrajectoryPlanner( //
      StateTimeRaster stateTimeRaster, //
      StateIntegrator stateIntegrator, //
      Collection<Flow> controls, //
      PlannerConstraint plannerConstraint, //
      GoalInterface goalInterface) {
    super(stateTimeRaster, goalInterface);
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
    DomainQueueMap domainQueueMap = new DomainQueueMap(); // holds candidates for insertion
    for (GlcNode next : connectors.keySet()) { // <- order of keys is non-deterministic
      final Tensor domainKey = stateTimeRaster.convertToKey(next.stateTime());
      Optional<GlcNode> former = getNode(domainKey);
      if (former.isPresent()) { // is already some node present from previous exploration ?
        if (relabelDecision.doRelabel(next, former.get()))
          domainQueueMap.insert(domainKey, next); // new node is potentially better than previous one
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
      if (plannerConstraint.isSatisfied(node, trajectory, next.flow())) {
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
