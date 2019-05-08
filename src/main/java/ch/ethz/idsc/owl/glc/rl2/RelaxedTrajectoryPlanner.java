// code by astoll, ynager
package ch.ethz.idsc.owl.glc.rl2;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcNodes;
import ch.ethz.idsc.owl.glc.core.HeuristicFunction;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.math.VectorScalars;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

public abstract class RelaxedTrajectoryPlanner implements TrajectoryPlanner, Serializable {
  protected final StateTimeRaster stateTimeRaster;
  private final HeuristicFunction heuristicFunction;
  private final Tensor slacks;
  private final RelaxedGlobalQueue globalQueue;
  private final RelaxedDomainQueueMap domainMap;
  private final RelaxedDomainQueue goalDomainQueue;

  // ---
  protected RelaxedTrajectoryPlanner(StateTimeRaster stateTimeRaster, HeuristicFunction heuristicFunction, Tensor slacks) {
    this.slacks = slacks;
    this.stateTimeRaster = stateTimeRaster;
    this.heuristicFunction = heuristicFunction;
    this.globalQueue = new RelaxedGlobalQueue(slacks);
    this.domainMap = new RelaxedDomainQueueMap(slacks);
    this.goalDomainQueue = RelaxedDomainQueue.empty(slacks);
  }

  /** Adds node to global queue if it does not have children
   * @param node non-null */
  protected final void addToGlobalQueue(GlcNode node) {
    if (!node.isLeaf()) {
      System.err.println("The Inserted Node has children");
      throw new RuntimeException();
    }
    globalQueue.add(node);
  }

  protected final void addToDomainMap(Tensor domain_key, GlcNode node) {
    domainMap.addToDomainMap(domain_key, node);
  }

  /** method is invoked to notify planner that the
   * intersection of the goal interface and the connector is non-empty
   * 
   * @param node
   * @param connector */
  protected final void offerDestination(GlcNode node, List<StateTime> connector) {
    Optional<Tensor> optional = goalDomainQueue.getMinValues();
    if (optional.isPresent()) {
      // TODO YN code redundant to StdRL.TPlanner#processCandidates -> can simplify?
      Tensor minValues = optional.get();
      Tensor merit = VectorScalars.vector(node.merit());
      for (int i = 0; i < slacks.length(); ++i) {
        if (Scalars.lessThan(merit.Get(i), minValues.Get(i))) {
          Scalar margin = merit.Get(i).add(slacks.Get(i));
          final int j = i;
          List<GlcNode> toRemove = goalDomainQueue.stream() // find nodes to be removed
              .filter(n -> Scalars.lessThan( //
                  margin, // lhs
                  VectorScalars.at(n.merit(), j) // rhs
              )).collect(Collectors.toList());
          if (!toRemove.isEmpty()) {
            getUnexpandedNodes().removeAll(toRemove);
            goalDomainQueue.removeAll(toRemove);
            // toRemove.stream().forEach(n -> n.parent().removeEdgeTo(n)); // FIXME YN
          }
        }
      }
    }
    goalDomainQueue.add(node);
  }
  
  /** @param domain_key
   * @return RLDomainQueue in domain or Optional.empty() if domain has not been assigned a node yet */
  protected final Optional<RelaxedDomainQueue> getDomainQueue(Tensor domain_key) {
    return Optional.ofNullable(domainMap.getQueue(domain_key));
  }

  // TODO ANDRE check if obsolete
  protected final RelaxedGlobalQueue getUnexpandedNodes() {
    return globalQueue;
  }

  // check if obsolete
  public final Map<Tensor, RelaxedDomainQueue> getRelaxedDomainQueueMap() {
    return domainMap.getMap();
  }
  /** Returns most promising unexpanded node. */
  @Override // from ExpandInterface
  public final Optional<GlcNode> pollNext() {
    // returns the head of queue, or null if queue is empty
    return Optional.ofNullable(globalQueue.poll());
  }

  @Override // from ExpandInterface
  public final Optional<GlcNode> getBest() {
    return Optional.ofNullable(goalDomainQueue.isEmpty() ? null : goalDomainQueue.peek());
  }

  /***************************************************/
  @Override // from TrajectoryPlanner
  public final void insertRoot(StateTime stateTime) {
    GlobalAssert.that(globalQueue.isEmpty() && domainMap.isEmpty()); // root insertion requires empty planner
    addToGlobalQueue(GlcNodes.createRoot(stateTime, heuristicFunction));
  }

  @Override // from TrajectoryPlanner
  public final Optional<GlcNode> getBestOrElsePeek() {
    return Optional.ofNullable(getBest().orElse(globalQueue.peek()));
  }

  @Override // from TrajectoryPlanner
  public final HeuristicFunction getHeuristicFunction() {
    return heuristicFunction;
  }

  @Override // from TrajectoryPlanner
  public final Map<Tensor, GlcNode> getDomainMap() {
    // LONGTERM investigate unified design
    throw new UnsupportedOperationException();
  }

  @Override // from TrajectoryPlanner
  public final Collection<GlcNode> getQueue() {
    return Collections.unmodifiableCollection(getUnexpandedNodes().collection());
  }
}
