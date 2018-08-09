// code by ynager
package ch.ethz.idsc.owl.glc.rl;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.glc.core.ExpandInterface;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.HeuristicFunction;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;

public abstract class RLTrajectoryPlanner implements ExpandInterface<GlcNode>, Serializable {
  protected final StateTimeRaster stateTimeRaster;
  private final HeuristicFunction heuristicFunction;
  // ---
  private final Tensor slacks;
  /** holds not expanded nodes of OPEN list */
  private final RLQueue openQueue;
  /** maps domain key to RLQueues of OPEN nodes */
  private final RLDomainQueueMap domainMap;
  /** holds set of nodes reaching goal region and lying within slack boundaries */
  protected final RLQueue reachingSet;

  protected RLTrajectoryPlanner(StateTimeRaster stateTimeRaster, HeuristicFunction heuristicFunction, Tensor slacks) {
    this.stateTimeRaster = stateTimeRaster;
    this.heuristicFunction = heuristicFunction;
    this.slacks = slacks;
    this.openQueue = new RLQueue(slacks); // holds not expanded nodes of OPEN list
    this.domainMap = new RLDomainQueueMap(slacks); // maps domain key to RLQueues of OPEN nodes.
    this.reachingSet = new RLQueue(slacks);
  }

  /** @param stateTime */
  public final void insertRoot(StateTime stateTime) {
    GlobalAssert.that(openQueue.isEmpty() && domainMap.isEmpty()); // root insertion requires empty planner
    insert(stateTimeRaster.convertToKey(stateTime), StaticHelper.createRoot(stateTime, heuristicFunction));
  }

  /** @param domain_key
   * @param node non-null
   * @return true if node is added to open queue and domain queue */
  protected final void insert(Tensor domain_key, GlcNode node) {
    if (!node.isLeaf()) {
      System.err.println("The Inserted Node has children");
      throw new RuntimeException();
    }
    openQueue.add(node);
    domainMap.put(domain_key, node);
  }

  /** @param domain_key
   * @return RLDomainQueue in domain or Optional.empty() if domain has not been assigned a node yet */
  protected final Optional<RLDomainQueue> getNode(Tensor domain_key) {
    return Optional.ofNullable(domainMap.get(domain_key));
  }

  @Override // from ExpandInterface
  public final Optional<GlcNode> pollNext() {
    // Queue#poll() returns the head of queue, or null if queue is empty
    return Optional.ofNullable(openQueue.poll());
  }

  /** method is invoked to notify planner that the
   * intersection of the goal interface and the connector is non-empty
   * 
   * {@link AbstractAnyTrajectoryPlanner} overrides this method
   * 
   * @param node
   * @param connector */
  protected final void offerDestination(GlcNode node, List<StateTime> connector) {
    reachingSet.add(node);
    // FIXME: remove inferiour nodes & nodes outside slack bounds
  }

  @Override // from ExpandInterface
  public final Optional<GlcNode> getBest() {
    return Optional.ofNullable(reachingSet.isEmpty() ? null : reachingSet.poll());
  }

  /** @return best node known to be in goal, or top node in queue, or null,
   * in this order depending on existence */
  public final Optional<GlcNode> getBestOrElsePeek() {
    // Queue#peek() returns the head of queue, or null if queue is empty
    return Optional.ofNullable(getBest().orElse(openQueue.peek()));
  }

  // FIXME
  // /** @return number of replacements in the domain map caused by {@link RLTrajectoryPlanner#insert(Tensor, GlcNode)} */
  // public final int replaceCount() {
  // return replaceCount;
  // }
  /** @return state integrator for the state space to generate trajectories from given controls */
  public abstract StateIntegrator getStateIntegrator();

  /** @return goal query for the purpose of inspection */
  public final HeuristicFunction getHeuristicFunction() {
    return heuristicFunction;
  }

  protected final RLQueue queue() {
    return openQueue;
  }
  // /** @return unmodifiable view on queue for display and tests */
  // public final Collection<GlcNode> getQueue() {
  // return Collections.unmodifiableCollection(openQueue);
  // }
  // FIXME
  // /** @return unmodifiable view on domain map for display and tests */
  // public final RLDomainQueueMap getDomainMap() {
  // return Collections.unmodifiableMap(domainMap);
  // }
}
