// code by ynager
package ch.ethz.idsc.owl.glc.rl;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** priority queue with ordering defined by {@link NodeMeritComparator} */
public class RLDomainQueue extends RLQueue {
  private static final Scalar INTEGER_MAX = RealScalar.of(Integer.MAX_VALUE);

  /** @param glcNode
   * @return relaxed lexicographic domain queue that contains given glcNode as single element */
  public static RLDomainQueue singleton(GlcNode glcNode, Tensor slacks) {
    RLDomainQueue domainQueue = new RLDomainQueue(slacks);
    domainQueue.add(glcNode);
    return domainQueue;
  }

  // ---
  private Tensor bounds = Tensors.vector(a -> INTEGER_MAX, vectorSize); // TODO find simpler way
  private Tensor minValues = bounds;

  @Override
  public boolean add(GlcNode e) {
    Tensor merit = ((VectorScalar) e.merit()).vector();
    for (int i = 0; i < vectorSize; i++) {
      Scalar m = merit.Get(i);
      if (Scalars.lessThan(m, minValues.Get(i))) {
        minValues.set(m, i); // update minValue
        bounds.set(m.add(slack.get(i)), i); // update bounds
      }
    }
    return queue.add(e);
  }

  public Tensor getBounds() {
    return bounds;
  }

  public Tensor getMinValues() {
    return minValues;
  }

  // ---
  private RLDomainQueue(Tensor slacks) {
    super(slacks);
  }
}
