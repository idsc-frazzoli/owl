package ch.ethz.idsc.owl.glc.rl;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

public class RLQueue extends AbstractQueue<GlcNode> {
  private final List<GlcNode> queue = new ArrayList<>(500);
  private final Tensor slack;
  private final int costSize;

  public RLQueue(Tensor slack) {
    this.slack = slack;
    this.costSize = slack.length();
  }

  @Override
  public boolean offer(GlcNode e) {
    return queue.add(e);
  }

  @Override
  public GlcNode poll() {
    GlcNode best = getFromBest();
    queue.remove(best);
    return best;
  }

  @Override
  public GlcNode peek() {
    return getFromBest();
  }

  @Override
  public Iterator<GlcNode> iterator() {
    return queue.iterator();
  }

  @Override
  public int size() {
    return queue.size();
  }

  /** Get first element from best set */
  private GlcNode getFromBest() {
    List<GlcNode> queueCopy = new ArrayList<>(queue);
    getBestSet(queueCopy, 0);
    return queueCopy.get(0);
  }

  /** recursively find best set */
  private List<GlcNode> getBestSet(List<GlcNode> list, int d) {
    GlcNode minCostNode = Collections.min(list, new Comparator<GlcNode>() {
      @Override
      public int compare(GlcNode first, GlcNode second) {
        return Scalars.compare(((VectorScalar) first.merit()).vector().Get(d), ((VectorScalar) second.merit()).vector().Get(d));
      }
    });
    Scalar minMerit = ((VectorScalar) minCostNode.merit()).vector().Get(d);
    list.removeIf(n -> Scalars.lessThan(minMerit.add(slack.Get(d)), ((VectorScalar) n.merit()).vector().Get(d)));
    if (d == costSize - 1)
      return list;
    return getBestSet(list, d + 1);
  }
}
