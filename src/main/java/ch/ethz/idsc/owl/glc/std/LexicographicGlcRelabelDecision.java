package ch.ethz.idsc.owl.glc.std;

import ch.ethz.idsc.owl.bot.util.RelaxedLexicographic;
import ch.ethz.idsc.owl.data.tree.StateCostNode;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.tensor.Tensor;

public class LexicographicGlcRelabelDecision implements RelabelDecisionInterface {
  RelaxedLexicographic lexicographic;

  public LexicographicGlcRelabelDecision(Tensor slack) {
    lexicographic = RelaxedLexicographic.of(slack);
  }

  @Override
  public boolean doRelabel(StateCostNode newNode, StateCostNode formerNode) {
    int comp = lexicographic.compare(((GlcNode) newNode).merit(), ((GlcNode) formerNode).merit());
    return (comp == 1) ? true : false;
  }
}
