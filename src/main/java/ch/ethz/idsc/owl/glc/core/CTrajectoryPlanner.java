// code by bapaden, jph, jl, ynager
package ch.ethz.idsc.owl.glc.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeMap;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;

/** base class for classic generalized label correction implementation */
public abstract class CTrajectoryPlanner implements TrajectoryPlanner, Serializable {
  protected final StateTimeRaster stateTimeRaster;
  private final HeuristicFunction heuristicFunction;
  // ---
  // TODO design so that queue is private final
  // TODO comparator for queue, best, relabel decision
  public Queue<GlcNode> queue = new PriorityQueue<>(NodeMeritComparator.INSTANCE);
  private final Map<Tensor, GlcNode> domainMap = new HashMap<>();
  /** best is a reference to a Node in the goal region,
   * or null if such a node has not been identified
   * use function setBestNull() to reset best to null */
  protected final NavigableMap<GlcNode, List<StateTime>> best = new TreeMap<>(NodeMeritComparator.INSTANCE);
  private int replaceCount = 0;

  protected CTrajectoryPlanner(StateTimeRaster stateTimeRaster, HeuristicFunction heuristicFunction) {
    this.stateTimeRaster = stateTimeRaster;
    this.heuristicFunction = heuristicFunction;
  }

  /** @param domain_key
   * @param node non-null
   * @return true if node replaces a existing entry in the domain map,
   * false if the domain map did not have a pre-existing mapping from given domain_key */
  protected final boolean insert(Tensor domain_key, GlcNode node) {
    if (!node.isLeaf()) {
      System.err.println("The Inserted Node has children");
      throw new RuntimeException();
    }
    queue.add(node);
    final boolean replaced = domainMap.containsKey(domain_key);
    domainMap.put(domain_key, node);
    if (replaced)
      ++replaceCount;
    return replaced;
  }

  /** @param domain_key
   * @return node in domain or Optional.empty() if domain has not been assigned a node yet */
  protected final Optional<GlcNode> getNode(Tensor domain_key) {
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
    best.put(node, connector);
    if (1 < best.size())
      best.remove(best.lastKey());
  }

  /** @return number of replacements in the domain map caused by
   * {@link TrajectoryPlanner#insert(Tensor, GlcNode)} */
  public final int replaceCount() {
    return replaceCount;
  }

  protected final Collection<GlcNode> queue() {
    return queue;
  }

  /***************************************************/
  @Override // from ExpandInterface
  public final Optional<GlcNode> getBest() {
    return Optional.ofNullable(best.isEmpty() ? null : best.firstKey());
  }

  @Override // from ExpandInterface
  public final Optional<GlcNode> pollNext() {
    // Queue#poll() returns the head of queue, or null if queue is empty
    return Optional.ofNullable(queue.poll());
  }

  /***************************************************/
  @Override // from TrajectoryPlanner
  public final void insertRoot(StateTime stateTime) {
    GlobalAssert.that(queue.isEmpty() && domainMap.isEmpty()); // root insertion requires empty planner
    boolean replaced = insert(stateTimeRaster.convertToKey(stateTime), GlcNodes.createRoot(stateTime, heuristicFunction));
    GlobalAssert.that(!replaced); // root insertion should not replace any other node
  }

  @Override // from TrajectoryPlanner
  public final Optional<GlcNode> getBestOrElsePeek() {
    // Queue#peek() returns the head of queue, or null if queue is empty
    return Optional.ofNullable(getBest().orElse(queue.peek()));
  }

  @Override // from TrajectoryPlanner
  public final HeuristicFunction getHeuristicFunction() {
    return heuristicFunction;
  }

  @Override // from TrajectoryPlanner
  public final Map<Tensor, GlcNode> getDomainMap() {
    return Collections.unmodifiableMap(domainMap);
  }

  @Override // from TrajectoryPlanner
  public final Collection<GlcNode> getQueue() {
    return Collections.unmodifiableCollection(queue);
  }
}
