// code by jph, ynager
package ch.ethz.idsc.owl.glc.adapter;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.RelabelDecision;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Sign;

/** for CostFunction that map to values of type {@link RealScalar} or {@link Quantity}.
 * 
 * in particular, not for cost functions that operate on {@link VectorScalar}
 * 
 * Remark: the implementation is designed to handle all cases:
 * exact precision, numerical inaccuracies, and quantities.
 * in the long term this universality may not be desirable. */
public enum SimpleRelabelDecision implements RelabelDecision {
  INSTANCE;
  // TODO probably should be relative to order of magnitude of merit
  /** minimum threshold of improvement by a candidate */
  private static final Scalar MERIT_EPS = DoubleScalar.of(1E-6);

  @Override // from RelabelDecision
  public boolean doRelabel(GlcNode newNode, GlcNode oldNode) {
    return doRelabel(newNode.merit(), oldNode.merit(), MERIT_EPS);
  }

  /** @param newMerit
   * @param oldMerit
   * @param slack of type {@link RealScalar}
   * @return */
  static boolean doRelabel(Scalar newMerit, Scalar oldMerit, Scalar slack) {
    Scalar delta = ToRealScalar.FUNCTION.apply(oldMerit.subtract(newMerit));
    return Scalars.lessThan(slack, delta) //
        || ExactScalarQ.of(delta) && Sign.isPositive(delta);
  }
}
