// code by ynager
package ch.ethz.idsc.owl.glc.rl;

import java.util.Collections;
import java.util.Comparator;

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
  @Override
  public boolean add(GlcNode e) {
    return list.add(e);
  }

  public Tensor getMinValues() {
    Tensor minValues = Tensors.vector(a -> INTEGER_MAX, vectorSize);
    if (list.isEmpty())
      return minValues;
    for (int i = 0; i < vectorSize; i++) {
      final int j = i;
      GlcNode minCostNode = Collections.min(list, new Comparator<GlcNode>() {
        @Override
        public int compare(GlcNode first, GlcNode second) {
          return Scalars.compare( //
              ((VectorScalar) first.merit()).vector().Get(j), //
              ((VectorScalar) second.merit()).vector().Get(j));
        }
      });
      minValues.set(((VectorScalar) minCostNode.merit()).vector().Get(i), i);
    }
    return minValues;
  }

  // ---
  public RLDomainQueue(Tensor slacks) {
    super(slacks);
  }
}
