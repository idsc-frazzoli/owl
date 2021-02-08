// code by bapaden, jph, jl, ynager
package ch.ethz.idsc.owl.glc.std;

import java.util.Collection;
import java.util.Comparator;
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
import ch.ethz.idsc.owl.glc.core.InvariantFlows;
import ch.ethz.idsc.owl.glc.core.NodeMeritComparator;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.RelabelDecision;
import ch.ethz.idsc.owl.glc.core.StateTimeFlows;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
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
  /** @param stateTimeRaster
   * @param stateIntegrator
   * @param stateTimeFlows
   * @param plannerConstraint
   * @param goalInterface
   * @return */
  public static CTrajectoryPlanner create( //
      StateTimeRaster stateTimeRaster, StateIntegrator stateIntegrator, StateTimeFlows stateTimeFlows, //
      PlannerConstraint plannerConstraint, GoalInterface goalInterface) {
    return new StandardTrajectoryPlanner( //
        stateTimeRaster, stateIntegrator, stateTimeFlows, plannerConstraint, goalInterface, //
        SimpleRelabelDecision.DEFAULT, NodeMeritComparator.INSTANCE);
  }

  /***************************************************/
  private final StateIntegrator stateIntegrator;
  private final PlannerConstraint plannerConstraint;
  private final GoalInterface goalInterface;
  private final RelabelDecision relabelDecision;
  private final ControlsIntegrator controlsIntegrator;

  public StandardTrajectoryPlanner( //
      StateTimeRaster stateTimeRaster, StateIntegrator stateIntegrator, Collection<Tensor> controls, //
      PlannerConstraint plannerConstraint, GoalInterface goalInterface) {
    this(stateTimeRaster, stateIntegrator, new InvariantFlows(controls), //
        plannerConstraint, goalInterface, //
        SimpleRelabelDecision.DEFAULT, NodeMeritComparator.INSTANCE);
  }

  public StandardTrajectoryPlanner( //
      StateTimeRaster stateTimeRaster, StateIntegrator stateIntegrator, Collection<Tensor> controls, //
      PlannerConstraint plannerConstraint, GoalInterface goalInterface, RelabelDecision relabelDecision) {
    this(stateTimeRaster, stateIntegrator, new InvariantFlows(controls), //
        plannerConstraint, goalInterface, //
        relabelDecision, NodeMeritComparator.INSTANCE);
  }

  public StandardTrajectoryPlanner( //
      StateTimeRaster stateTimeRaster, StateIntegrator stateIntegrator, StateTimeFlows stateTimeFlows, //
      PlannerConstraint plannerConstraint, GoalInterface goalInterface, //
      RelabelDecision relabelDecision, Comparator<GlcNode> comparator) {
    super(stateTimeRaster, goalInterface, comparator);
    this.stateIntegrator = stateIntegrator;
    this.plannerConstraint = Objects.requireNonNull(plannerConstraint);
    this.goalInterface = goalInterface;
    this.relabelDecision = relabelDecision;
    controlsIntegrator = new ControlsIntegrator( //
        stateIntegrator, //
        stateTimeFlows, //
        goalInterface);
  }

  @Override // from ExpandInterface
  public void expand(final GlcNode node) {
    Map<GlcNode, List<StateTime>> connectors = controlsIntegrator.from(node);
    // ---
    DomainQueueMap domainQueueMap = new DomainQueueMap(); // holds candidates for insertion
    for (GlcNode next : connectors.keySet()) { // <- order of keys is non-deterministic
      final Tensor domainKey = stateTimeRaster().convertToKey(next.stateTime());
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
    // GlcNode seed = node;
    // while (seed.children().isEmpty()) {
    // GlcNode par = node.parent();
    // if (Objects.isNull(par))
    // break;
    // GlobalAssert.that(seed.isLeaf());
    // par.removeEdgeTo(seed);
    // GlcNode remove = remove(stateTimeRaster().convertToKey(seed.stateTime()));
    // if (remove != seed)
    // throw new RuntimeException();
    // seed = par;
    // }
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
