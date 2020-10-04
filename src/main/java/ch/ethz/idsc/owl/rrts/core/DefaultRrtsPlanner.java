// code by jph
package ch.ethz.idsc.owl.rrts.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

import ch.ethz.idsc.owl.data.tree.NodeCostComparator;
import ch.ethz.idsc.owl.data.tree.StateCostNode;
import ch.ethz.idsc.sophus.math.sample.RandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;

public class DefaultRrtsPlanner implements RrtsPlanner {
  protected static final int K_NEAREST = 12;
  private static final RrtsNode DUMMY = new RrtsNodeImpl(null, null);
  // ---
  protected final Rrts rrts;
  private final RandomSampleInterface spaceSample;
  private final RandomSampleInterface goalSample;
  protected final Queue<RrtsNode> queue;

  /** @param rrts with root already inserted
   * @param spaceSample
   * @param goalSample generates samples in goal region */
  public DefaultRrtsPlanner(Rrts rrts, RandomSampleInterface spaceSample, RandomSampleInterface goalSample) {
    this(rrts, spaceSample, goalSample, NodeCostComparator.INSTANCE);
  }

  /** @param rrts with root already inserted
   * @param spaceSample
   * @param goalSample generates samples in goal region
   * @param nodeComparator */
  public DefaultRrtsPlanner(Rrts rrts, RandomSampleInterface spaceSample, RandomSampleInterface goalSample, Comparator<StateCostNode> nodeComparator) {
    this.rrts = rrts;
    this.spaceSample = spaceSample;
    this.goalSample = goalSample;
    queue = new PriorityQueue<>(nodeComparator);
  }

  @Override // from ExpandInterface
  public Optional<RrtsNode> pollNext() {
    return Optional.of(DUMMY); // never used, see expand(RrtsNode node)
  }

  @Override // from ExpandInterface
  public void expand(RrtsNode node) { // node is not used, instead new random sample
    rrts.insertAsNode(RandomSample.of(spaceSample), K_NEAREST);
    if (queue.isEmpty()) { // TODO RRTS logic not final
      rrts.insertAsNode(RandomSample.of(goalSample), K_NEAREST).ifPresent(queue::add);
    }
  }

  @Override // from ExpandInterface
  public Optional<RrtsNode> getBest() {
    return Optional.ofNullable(queue.peek());
  }

  @Override // from RrtsPlanner
  public List<RrtsNode> getQueue() {
    return new ArrayList<>(queue);
  }

  @Override // from RrtsPlanner
  public TransitionRegionQuery getObstacleQuery() {
    return ((DefaultRrts) rrts).getObstacleQuery();
  }
}
