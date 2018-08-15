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
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Sign;

public class StandardRLTrajectoryPlanner extends RLTrajectoryPlanner {
  private final StateIntegrator stateIntegrator;
  private final PlannerConstraint plannerConstraint;
  private final GoalInterface goalInterface;
  private transient final ControlsIntegrator controlsIntegrator;
  // ---
  private final Tensor slacks;
  private final int costSize;
  //
  private static final Scalar MERIT_EPS = RationalScalar.of(1, 100);

  public StandardRLTrajectoryPlanner( //
      StateTimeRaster stateTimeRaster, //
      StateIntegrator stateIntegrator, //
      Collection<Flow> controls, //
      PlannerConstraint plannerConstraint, //
      GoalInterface goalInterface, Tensor slacks) {
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
        if (isWithinSlack(next, formerQueue.get()) && !isEqual(next, formerQueue.get())) {
          domainQueueMap.put(domainKey, next); // new node lies within slack, potentially better than previous ones
        }
      } else
        domainQueueMap.put(domainKey, next); // node is considered without comparison to any former node
    }
    // ---
    domainQueueMap.mapEntrySetStream() //
        .parallel() //
        .forEach(entry -> processCandidates(node, connectors, entry.getKey(), entry.getValue()));
  }

  private boolean isWithinSlack(GlcNode next, RLDomainQueue domainQueue) {
    Tensor merit = ((VectorScalar) next.merit()).vector();
    Tensor diff = domainQueue.getMinValues().get().add(slacks).subtract(merit);
    return !diff.stream().map(Tensor::Get).anyMatch(Sign::isNegative);
  }

  private static boolean isEqual(GlcNode next, RLDomainQueue domainQueue) {
    // TODO check if close to existing nodes / assert if this is helpful
    Tensor nextMerit = ((VectorScalar) next.merit()).vector();
    return domainQueue.stream().anyMatch(a -> ((VectorScalar) a.merit()).vector().subtract(nextMerit) //
        .stream().map(Tensor::Get).allMatch(v -> Scalars.lessThan(v, MERIT_EPS)));
  }

  private void processCandidates( //
      GlcNode node, Map<GlcNode, List<StateTime>> connectors, Tensor domainKey, RLDomainQueue domainQueue) {
    // iterate over the candidates in DomainQueue
    // TODO YN is there any requirement on the ordering of the nodes in the iterator?
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
            Tensor merits = ((VectorScalar) next.merit()).vector();
            for (int i = 0; i < costSize; i++) { // find nodes outside of bounds
              if (Scalars.lessThan(merits.Get(i), minValues.Get(i))) { // cost lower than prev min?
                final int j = i;
                List<GlcNode> toRemove = formerQueue.stream() // find nodes to be removed
                    .filter(n -> Scalars.lessThan(merits.Get(j).add(slacks.Get(j)), //
                        ((VectorScalar) n.merit()).vector().Get(j)))
                    .collect(Collectors.toList());
                //
                if (!toRemove.isEmpty()) {
                  boolean removed = queue().removeAll(toRemove); // remove bad nodes from OPEN queue
                  formerQueue.removeAll(toRemove); // remove bad nodes from domain queue
                  toRemove.stream().forEach(n -> n.parent().removeEdgeTo(n)); // remove edges from parent
                  if (!removed) //
                    System.err.println("miss - nodes to be removed dont exist " + domainKey);
                }
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
