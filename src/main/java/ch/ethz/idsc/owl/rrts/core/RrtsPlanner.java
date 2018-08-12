// code by jph
package ch.ethz.idsc.owl.rrts.core;

import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

import ch.ethz.idsc.owl.data.tree.NodeCostComparator;
import ch.ethz.idsc.owl.glc.core.ExpandInterface;
import ch.ethz.idsc.owl.math.sample.RandomSampleInterface;

public class RrtsPlanner implements ExpandInterface<RrtsNode> {
  private static final RrtsNode DUMMY = new RrtsNodeImpl(null, null);
  // ---
  private final Rrts rrts;
  private final RandomSampleInterface spaceSample;
  private final RandomSampleInterface goalSample;
  private final Queue<RrtsNode> queue = new PriorityQueue<>(NodeCostComparator.INSTANCE);

  /** @param rrts with root already inserted
   * @param obstacleQuery
   * @param spaceSample
   * @param goalSample generates samples in goal region */
  public RrtsPlanner(Rrts rrts, RandomSampleInterface spaceSample, RandomSampleInterface goalSample) {
    this.rrts = rrts;
    this.spaceSample = spaceSample;
    this.goalSample = goalSample;
  }

  @Override
  public Optional<RrtsNode> pollNext() {
    return Optional.of(DUMMY);
  }

  @Override
  public void expand(RrtsNode node) {
    final int k_nearest = 12; // magic const
    rrts.insertAsNode(spaceSample.randomSample(), k_nearest);
    if (queue.isEmpty()) { // TODO RRTS logic not final
      Optional<RrtsNode> optional = rrts.insertAsNode(goalSample.randomSample(), k_nearest);
      if (optional.isPresent())
        queue.add(optional.get());
    }
  }

  @Override
  public Optional<RrtsNode> getBest() {
    return Optional.ofNullable(queue.peek()); // TODO use other criterion: distance from goal center ...?
  }

  public TransitionRegionQuery getObstacleQuery() {
    return ((DefaultRrts) rrts).getObstacleQuery();
  }
}
