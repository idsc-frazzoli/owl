// code by ynager
package ch.ethz.idsc.owl.glc.rl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

public abstract class RLTrajectoryPlanner implements TrajectoryPlanner, Serializable {
  protected final StateTimeRaster stateTimeRaster;
  private final HeuristicFunction heuristicFunction;
  // ---
  private final Tensor slacks;
  /** holds not expanded nodes of OPEN list */
  private final RLQueue openQueue;
  /** maps domain key to RLQueues of OPEN nodes */
  private final RLDomainQueueMap domainMap;
  /** holds set of nodes reaching goal region and lying within slack boundaries */
  protected final RLDomainQueue reachingSet;

  protected RLTrajectoryPlanner(StateTimeRaster stateTimeRaster, HeuristicFunction heuristicFunction, Tensor slacks) {
    this.slacks = slacks;
    this.stateTimeRaster = stateTimeRaster;
    this.heuristicFunction = heuristicFunction;
    this.openQueue = new RLQueue(slacks); // holds not expanded nodes of OPEN list
    this.domainMap = new RLDomainQueueMap(slacks); // maps domain key to RLQueues of OPEN nodes.
    this.reachingSet = RLDomainQueue.empty(slacks);
  }

  /** @param domain_key
   * @param node non-null
   * @return true if node is added to open queue and domain queue */
  protected final void addToOpen(Tensor domain_key, GlcNode node) {
    if (!node.isLeaf()) {
      System.err.println("The Inserted Node has children");
      throw new RuntimeException();
    }
    openQueue.add(node);
  }

  protected final void addToDomainMap(Tensor domain_key, GlcNode node) {
    domainMap.put(domain_key, node);
  }

  /** @param domain_key
   * @return RLDomainQueue in domain or Optional.empty() if domain has not been assigned a node yet */
  protected final Optional<RLDomainQueue> getDomainQueue(Tensor domain_key) {
    return Optional.ofNullable(domainMap.get(domain_key));
  }

  /** method is invoked to notify planner that the
   * intersection of the goal interface and the connector is non-empty
   * 
   * {@link AbstractAnyTrajectoryPlanner} overrides this method
   * 
   * @param node
   * @param connector */
  protected final void offerDestination(GlcNode node, List<StateTime> connector) {
    Optional<Tensor> optional = reachingSet.getMinValues();
    if (optional.isPresent()) {
      // TODO YN code redundant to StdRL.TPlanner#processCandidates -> can simplify?
      Tensor minValues = optional.get();
      Tensor merit = VectorScalars.vector(node.merit());
      for (int i = 0; i < slacks.length(); i++) {
        final int j = i;
        if (Scalars.lessThan(merit.Get(i), minValues.Get(i))) {
          List<GlcNode> toRemove = reachingSet.stream() // find nodes to be removed
              .filter(n -> Scalars.lessThan( //
                  merit.Get(j).add(slacks.Get(j)), // lhs
                  VectorScalars.at(n.merit(), j) // rhs
              )).collect(Collectors.toList());
          if (!toRemove.isEmpty()) {
            queue().removeAll(toRemove);
            reachingSet.removeAll(toRemove);
            // toRemove.stream().forEach(n -> n.parent().removeEdgeTo(n)); // FIXME YN
          }
        }
      }
    }
    reachingSet.add(node);
  }

  protected final RLQueue queue() {
    return openQueue;
  }

  /***************************************************/
  @Override // from ExpandInterface
  public final Optional<GlcNode> pollNext() {
    // Queue#poll() returns the head of queue, or null if queue is empty
    return Optional.ofNullable(openQueue.poll());
  }

  @Override // from ExpandInterface
  public final Optional<GlcNode> getBest() {
    System.out.println(reachingSet.getMinValues());
    return Optional.ofNullable(reachingSet.isEmpty() ? null : reachingSet.peek());
  }

  /***************************************************/
  @Override // from TrajectoryPlanner
  public final void insertRoot(StateTime stateTime) {
    GlobalAssert.that(openQueue.isEmpty() && domainMap.isEmpty()); // root insertion requires empty planner
    addToOpen(stateTimeRaster.convertToKey(stateTime), GlcNodes.createRoot(stateTime, heuristicFunction));
  }

  @Override // from TrajectoryPlanner
  public final Optional<GlcNode> getBestOrElsePeek() {
    // Queue#peek() returns the head of queue, or null if queue is empty
    return Optional.ofNullable(getBest().orElse(openQueue.peek()));
  }

  @Override // from TrajectoryPlanner
  public final HeuristicFunction getHeuristicFunction() {
    return heuristicFunction;
  }

  @Override // from TrajectoryPlanner
  public final Map<Tensor, GlcNode> getDomainMap() {
    return new HashMap<>(); // FIXME YN
  }

  @Override // from TrajectoryPlanner
  public final Collection<GlcNode> getQueue() {
    return Collections.unmodifiableCollection(queue().collection());
  }
}
