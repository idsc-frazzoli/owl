// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.Sign;

/** Reference:
 * Bi-invariant Means in Lie Groups.
 * Application to Left-invariant Polyaffine Transformations.
 * by Vincent Arsigny, Xavier Pennec, Nicholas Ayache
 * pages 27-31 */
public enum StExponential implements LieExponential {
  INSTANCE;
  // ---
  @Override // from LieExponential
  public Tensor exp(Tensor dlambda_dt) {
    Scalar dl = dlambda_dt.Get(0);
    Tensor dt = dlambda_dt.get(1);
    if (Scalars.isZero(dl))
      return Tensors.of(RealScalar.ONE, dt);
    Scalar exp_dl = Exp.FUNCTION.apply(dl);
    return Tensors.of( //
        exp_dl, //
        // TODO: OB This formula is a guess extrapolated from the ST1 case. I need the ST(n) case to write this down correctly
        dt.multiply(exp_dl.subtract(RealScalar.ONE)).divide(dl));
  }

  @Override // from LieExponential
  public Tensor log(Tensor lambda_t) {
    Scalar lambda = Sign.requirePositive(lambda_t.Get(0));
    Tensor t = lambda_t.get(1);
    if (lambda.equals(RealScalar.ONE))
      return Tensors.of(RealScalar.ZERO, t);
    Scalar log_l = Log.FUNCTION.apply(lambda);
    return Tensors.of( //
        log_l, //
        // TODO: OB This formula is a guess extrapolated from the ST1 case. I need the ST(n) case to write this down correctly
        t.multiply(log_l).divide(lambda.subtract(RealScalar.ONE)));
  }
}
