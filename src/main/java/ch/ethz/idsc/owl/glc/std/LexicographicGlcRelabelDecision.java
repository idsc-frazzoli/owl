package ch.ethz.idsc.owl.glc.std;

import ch.ethz.idsc.owl.bot.util.RelaxedLexicographic;
import ch.ethz.idsc.owl.data.tree.StateCostNode;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.tensor.Tensor;

public class LexicographicGlcRelabelDecision implements RelabelDecisionInterface {
  RelaxedLexicographic lexicographic;

  public LexicographicGlcRelabelDecision(Tensor slack) {
    lexicographic = RelaxedLexicographic.of(slack);
  }

  @Override
  public boolean doRelabel(StateCostNode newNode, StateCostNode formerNode) {
    Tensor newMerit = ((VectorScalar) ((GlcNode) newNode).merit()).vector();
    Tensor formerMerit = ((VectorScalar) ((GlcNode) formerNode).merit()).vector();
    int comp = lexicographic.compare(newMerit, formerMerit);
    return (comp == -1) ? true : false;
  }
}
