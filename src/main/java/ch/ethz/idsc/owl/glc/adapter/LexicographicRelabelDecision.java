// code by ynager
package ch.ethz.idsc.owl.glc.adapter;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.RelabelDecision;
import ch.ethz.idsc.owl.math.DiscretizedLexicographic;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.tensor.Tensor;

public class LexicographicRelabelDecision implements RelabelDecision {
  private final DiscretizedLexicographic comparator;

  public LexicographicRelabelDecision(Tensor slack) {
    comparator = DiscretizedLexicographic.of(slack);
  }

  @Override // from RelabelDecisionInterface
  public boolean doRelabel(GlcNode newNode, GlcNode oldNode) {
    Tensor newMerit = ((VectorScalar) newNode.merit()).vector();
    Tensor formerMerit = ((VectorScalar) oldNode.merit()).vector();
    return comparator.compare(newMerit, formerMerit) == -1;
  }
}
