package ch.ethz.idsc.owl.glc.std;

import ch.ethz.idsc.owl.data.tree.StateCostNode;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.sca.Sign;

public class SimpleGlcRelabelDecision implements RelabelDecisionInterface {
  private final Scalar merit_eps;

  public SimpleGlcRelabelDecision(Scalar merit_eps) {
    this.merit_eps = merit_eps;
  }

  public boolean doRelabel(StateCostNode newNode, StateCostNode formerNode) {
    Scalar delta = ((GlcNode) formerNode).merit().subtract(((GlcNode) newNode).merit());
    boolean passed = Scalars.lessThan(merit_eps, delta);
    return passed | ExactScalarQ.of(delta) && Sign.isPositive(delta);
  }
}
