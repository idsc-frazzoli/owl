// code by jph, ynager
package ch.ethz.idsc.owl.glc.std;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.sca.Sign;

public enum SimpleGlcRelabelDecision implements RelabelDecisionInterface<GlcNode> {
  INSTANCE;
  /** minimum threshold of improvement by a candidate */
  // TODO probably should be relative to order of magnitude of merit
  private static final Scalar MERIT_EPS = DoubleScalar.of(1E-6);

  @Override // from RelabelDecisionInterface
  public boolean doRelabel(GlcNode newNode, GlcNode formerNode) {
    return doRelabel(newNode.merit(), formerNode.merit(), MERIT_EPS);
  }

  /** @param newMerit
   * @param formerMerit
   * @param slack
   * @return */
  static boolean doRelabel(Scalar newMerit, Scalar formerMerit, Scalar slack) {
    Scalar delta = formerMerit.subtract(newMerit);
    return Scalars.lessThan(slack, delta) || ExactScalarQ.of(delta) && Sign.isPositive(delta);
  }
}
