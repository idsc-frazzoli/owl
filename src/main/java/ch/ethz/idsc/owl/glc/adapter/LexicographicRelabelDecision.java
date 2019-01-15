// code by ynager
package ch.ethz.idsc.owl.glc.adapter;

import java.util.Comparator;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.RelabelDecision;
import ch.ethz.idsc.owl.math.VectorScalars;
import ch.ethz.idsc.tensor.Tensor;

/** class is used for motion planning on the gokart */
public class LexicographicRelabelDecision implements RelabelDecision {
  private final Comparator<Tensor> comparator;

  public LexicographicRelabelDecision(Comparator<Tensor> comparator) {
    this.comparator = comparator;
  }

  @Override // from RelabelDecision
  public boolean doRelabel(GlcNode newNode, GlcNode oldNode) {
    return comparator.compare( //
        VectorScalars.vector(newNode.merit()), //
        VectorScalars.vector(oldNode.merit())) < 0;
  }
}
