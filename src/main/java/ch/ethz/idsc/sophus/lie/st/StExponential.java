// code by ob
package ch.ethz.idsc.sophus.lie.st;

import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.Sign;

/** Reference 1:
 * "Exponential Barycenters of the Canonical Cartan Connection and
 * Invariant Means on Lie Groups" by Xavier Pennec, Vincent Arsigny
 * pages 25-28
 * 
 * Reference 2:
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
        dt.multiply(exp_dl.subtract(RealScalar.ONE).divide(dl)));
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
        /* there is a typo in Reference 1 (!) */
        t.multiply(log_l.divide(lambda.subtract(RealScalar.ONE))));
  }
}
