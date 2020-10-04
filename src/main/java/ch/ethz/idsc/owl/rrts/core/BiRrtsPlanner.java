// code by jph, gjoel
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

public class BiRrtsPlanner implements RrtsPlanner {
  private static final int K_NEAREST = 12;
  private static final RrtsNode DUMMY = new RrtsNodeImpl(null, null);
  // ---
  private final BidirectionalRrts rrts;
  private final RandomSampleInterface randomSampleInterface;
  private final Queue<RrtsNode> queue;

  /** @param rrts with root already inserted
   * @param spaceSample */
  public BiRrtsPlanner(BidirectionalRrts rrts, RandomSampleInterface spaceSample) {
    this(rrts, spaceSample, NodeCostComparator.INSTANCE);
  }

  /** @param rrts with root already inserted
   * @param randomSampleInterface
   * @param nodeComparator */
  public BiRrtsPlanner(BidirectionalRrts rrts, RandomSampleInterface randomSampleInterface, Comparator<StateCostNode> nodeComparator) {
    this.rrts = rrts;
    this.randomSampleInterface = randomSampleInterface;
    queue = new PriorityQueue<>(nodeComparator);
  }

  @Override // from ExpandInterface
  public Optional<RrtsNode> pollNext() {
    return Optional.of(DUMMY); // never used, see expand(RrtsNode node)
  }

  @Override // from ExpandInterface
  public void expand(RrtsNode node) { // node is not used, instead new random sample
    rrts.insertAsNode(RandomSample.of(randomSampleInterface), K_NEAREST);
    rrts.getGoal().ifPresent(queue::add);
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
    return rrts.getObstacleQuery();
  }
}
