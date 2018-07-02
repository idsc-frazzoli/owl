// code by ynager
package ch.ethz.idsc.owl.glc.std;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.tensor.Tensor;

public class LexicographicRelabelDecision implements RelabelDecision {
  private final RelaxedLexicographic relaxedLexicographic;

  public LexicographicRelabelDecision(Tensor slack) {
    relaxedLexicographic = new RelaxedLexicographic(slack);
  }

  @Override // from RelabelDecisionInterface
  public boolean doRelabel(GlcNode newNode, GlcNode oldNode) {
    Tensor newMerit = ((VectorScalar) newNode.merit()).vector();
    Tensor formerMerit = ((VectorScalar) oldNode.merit()).vector();
    return relaxedLexicographic.quasiCompare(newMerit, formerMerit) == -1;
  }
}
