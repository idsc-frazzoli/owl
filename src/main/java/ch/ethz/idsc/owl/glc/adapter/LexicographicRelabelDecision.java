// code by ynager
package ch.ethz.idsc.owl.glc.adapter;

import java.util.Comparator;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.RelabelDecision;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.tensor.Tensor;

/** class is used for motion planning on the gokart */
public class LexicographicRelabelDecision implements RelabelDecision {
  private final Comparator<Tensor> comparator;

  public LexicographicRelabelDecision(Comparator<Tensor> comparator) {
    this.comparator = comparator;
  }

  @Override // from RelabelDecisionInterface
  public boolean doRelabel(GlcNode newNode, GlcNode oldNode) {
    Tensor newMerit = ((VectorScalar) newNode.merit()).vector();
    Tensor formerMerit = ((VectorScalar) oldNode.merit()).vector();
    return comparator.compare(newMerit, formerMerit) == -1;
  }
}
