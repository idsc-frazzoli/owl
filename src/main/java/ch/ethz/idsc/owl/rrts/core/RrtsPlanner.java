// code by jph
package ch.ethz.idsc.owl.rrts.core;

import java.util.Comparator;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import ch.ethz.idsc.owl.data.tree.NodeCostComparator;
import ch.ethz.idsc.owl.data.tree.StateCostNode;
import ch.ethz.idsc.owl.glc.core.ExpandInterface;
import ch.ethz.idsc.owl.math.sample.RandomSampleInterface;

public class RrtsPlanner implements ExpandInterface<RrtsNode> {
  private static final int K_NEAREST = 12;
  private static final RrtsNode DUMMY = new RrtsNodeImpl(null, null);
  private static final Random RANDOM = new Random();
  // ---
  private final Rrts rrts;
  private final RandomSampleInterface spaceSample;
  private final RandomSampleInterface goalSample;
  private final Queue<RrtsNode> queue;

  /** @param rrts with root already inserted
   * @param spaceSample
   * @param goalSample generates samples in goal region */
  public RrtsPlanner(Rrts rrts, RandomSampleInterface spaceSample, RandomSampleInterface goalSample) {
    this(rrts, spaceSample, goalSample, NodeCostComparator.INSTANCE); // TODO use other criterion: distance from goal center ...?
  }

  /** @param rrts with root already inserted
   * @param spaceSample
   * @param goalSample generates samples in goal region
   * @param nodeComparator */
  public RrtsPlanner(Rrts rrts, RandomSampleInterface spaceSample, RandomSampleInterface goalSample, Comparator<StateCostNode> nodeComparator) {
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
    rrts.insertAsNode(spaceSample.randomSample(RANDOM), K_NEAREST);
    if (queue.isEmpty()) { // TODO RRTS logic not final
      rrts.insertAsNode(goalSample.randomSample(RANDOM), K_NEAREST).ifPresent(queue::add);
    }
  }

  @Override // from ExpandInterface
  public Optional<RrtsNode> getBest() {
    return Optional.ofNullable(queue.peek());
  }

  public TransitionRegionQuery getObstacleQuery() {
    return ((DefaultRrts) rrts).getObstacleQuery();
  }
}
