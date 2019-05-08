// code by astoll, ynager
package ch.ethz.idsc.owl.glc.rl2;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.glc.core.ControlsIntegrator;
import ch.ethz.idsc.owl.glc.core.DomainQueue;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.rl2.RelaxedDomainQueue;
import ch.ethz.idsc.owl.math.VectorScalars;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

public class StandardRelaxedLexicographicPlanner extends RelaxedTrajectoryPlanner {
  private final StateIntegrator stateIntegrator;
  private final PlannerConstraint plannerConstraint;
  private final GoalInterface goalInterface;
  private transient final ControlsIntegrator controlsIntegrator;
  // ---
  private final Tensor slacks;

  public StandardRelaxedLexicographicPlanner(//
      StateTimeRaster stateTimeRaster, //
      StateIntegrator stateIntegrator, //
      Collection<Flow> controls, //
      PlannerConstraint plannerConstraints, //
      GoalInterface goalInterface, //
      Tensor slacks) {
    super(stateTimeRaster, goalInterface, slacks);
    this.slacks = slacks;
    this.stateIntegrator = stateIntegrator;
    this.plannerConstraint = Objects.requireNonNull(plannerConstraints);
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
    RelaxedDomainQueueMap domainQueueMap = new RelaxedDomainQueueMap(slacks); // holds candidates for insertion
    for (GlcNode next : connectors.keySet()) { // <- order of keys is non-deterministic
      final Tensor domainKey = stateTimeRaster.convertToKey(next.stateTime());
      domainQueueMap.addToDomainMap(domainKey, next);
    }
    // TODO ANDRE check if close to other merits see StaticHelper
    // TODO ANDRE check if former is present
    
    // TODO add next to openQueue if not accept by domainQueue
    // TODO remove all discarded nodes from globalQueue
    // TODO if any removed, remove edges from parent
    // ---
//    domainQueueMap.mapEntrySetStream() //
//        .parallel() // TODO ANDRE check if all operations are thread safe!
//        .forEach(entry -> processCandidates(node, connectors, entry.getKey(), entry.getValue()));
  }
  
//  private void processCandidates( //
//      GlcNode node, Map<GlcNode, List<StateTime>> connectors, Tensor domainKey, RelaxedDomainQueue domainQueue) {
//    // iterate over the candidates in DomainQueue
//    // TODO YN is there any requirement on the ordering of the nodes in the iterator?
//    for (GlcNode next : domainQueue) {
//      final List<StateTime> trajectory = connectors.get(next);
//      if (plannerConstraint.isSatisfied(node, trajectory, next.flow())) {
//        Optional<RelaxedDomainQueue> former = getDomainQueue(domainKey);
//        boolean isPresent = former.isPresent();
//        // ---
//        synchronized (this) {
//          if (isPresent) { // are already nodes present from previous exploration ?
//            RelaxedDomainQueue formerQueue = former.get();
//            Tensor minValues = formerQueue.getMinValues().get();
//            Tensor merit = VectorScalars.vector(next.merit());
//            for (int i = 0; i < slacks.length(); ++i) // find nodes outside of bounds
//              if (Scalars.lessThan(merit.Get(i), minValues.Get(i))) { // cost lower than prev min?
//                Scalar margin = merit.Get(i).add(slacks.Get(i));
//                final int j = i;
//                List<GlcNode> toRemove = formerQueue.stream() // find nodes to be removed
//                    .filter(n -> Scalars.lessThan( //
//                        margin, // lhs
//                        VectorScalars.at(n.merit(), j) // rhs
//                    )).collect(Collectors.toList());
//                //
//                if (!toRemove.isEmpty()) {
//                  boolean removed = queue().removeAll(toRemove); // remove bad nodes from OPEN queue
//                  formerQueue.removeAll(toRemove); // remove bad nodes from domain queue
//                  toRemove.stream().forEach(Nodes::disjoinChild); // remove edges from parent
//                  if (!removed) //
//                    System.err.println("miss - nodes to be removed dont exist " + domainKey);
//                }
//              }
//          }
//          addToDomainMap(domainKey, next); // insert node to domain queue
//          node.insertEdgeTo(next);
//          addToOpen(domainKey, next); // insert node into OPEN
//          if (goalInterface.firstMember(trajectory).isPresent()) // GOAL check
//            offerDestination(next, trajectory);
//        }
//        break; // as in B. Paden's implementation: leaving loop after first relabel
//      }
//    }
//  }

  @Override // from TrajectoryPlanner
  public final StateIntegrator getStateIntegrator() {
    return stateIntegrator;
  }
}
