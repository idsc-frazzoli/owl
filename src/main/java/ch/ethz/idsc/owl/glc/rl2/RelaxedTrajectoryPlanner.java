// code by astoll, ynager
package ch.ethz.idsc.owl.glc.rl2;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcNodes;
import ch.ethz.idsc.owl.glc.core.HeuristicFunction;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.math.state.StateTime;
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

  protected final Collection<GlcNode> addToDomainMap(Tensor domain_key, GlcNode node) {
    return domainMap.addToDomainMap(domain_key, node);
  }

  /** method is invoked to notify planner that the
   * intersection of the goal interface and the connector is non-empty
   * 
   * @param node
   * @param connector */
  protected final void offerDestination(GlcNode node, List<StateTime> connector) {
    goalDomainQueue.add(node);
    // TODO ANDRE check if valid
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

  protected final void removeFromGlobal(Collection<GlcNode> toRemove) {
    globalQueue.removeAll(toRemove);
  }

  // check if obsolete
  public final Map<Tensor, RelaxedDomainQueue> getRelaxedDomainQueueMap() {
    return domainMap.getMap();
  }

  /** Returns most promising unexpanded node. */
  @Override // from ExpandInterface
  public final Optional<GlcNode> pollNext() {
    // returns the head of queue, or null if queue is empty
    return Optional.ofNullable(globalQueue.pollBest());
  }

  @Override // from ExpandInterface
  public final Optional<GlcNode> getBest() {
    return Optional.ofNullable(goalDomainQueue.collection().isEmpty() ? null : goalDomainQueue.peekBest());
  }

  /***************************************************/
  @Override // from TrajectoryPlanner
  public final void insertRoot(StateTime stateTime) {
    GlobalAssert.that(globalQueue.collection().isEmpty() && domainMap.isEmpty()); // root insertion requires empty planner
    addToGlobalQueue(GlcNodes.createRoot(stateTime, heuristicFunction));
  }

  @Override // from TrajectoryPlanner
  public final Optional<GlcNode> getBestOrElsePeek() {
    return Optional.ofNullable(getBest().orElse(globalQueue.peekBest()));
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
