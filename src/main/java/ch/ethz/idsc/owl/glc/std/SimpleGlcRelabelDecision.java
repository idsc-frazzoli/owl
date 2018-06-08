package ch.ethz.idsc.owl.glc.std;

import ch.ethz.idsc.owl.data.tree.StateCostNode;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.sca.Sign;

public enum SimpleGlcRelabelDecision implements RelabelDecisionInterface {
  INSTANCE;
  // TODO probably should be relative to order of magnitude of merit
  private static final Scalar MERIT_EPS = DoubleScalar.of(1E-6);

  public boolean doRelabel(StateCostNode newNode, StateCostNode formerNode) {
    Scalar delta = ((GlcNode) formerNode).merit().subtract(((GlcNode) newNode).merit());
    boolean passed = Scalars.lessThan(MERIT_EPS, delta);
    return passed | ExactScalarQ.of(delta) && Sign.isPositive(delta);
  }
}
