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
public enum St1Exponential implements LieExponential {
  INSTANCE;
  // ---
  @Override // from LieExponential
  public Tensor exp(Tensor dlambda_dt) {
    Scalar dl = dlambda_dt.Get(0);
    Scalar dt = dlambda_dt.Get(1);
    if (Scalars.isZero(dl))
      return Tensors.of(RealScalar.ONE, dt);
    Scalar exp_dl = Exp.FUNCTION.apply(dl);
    return Tensors.of( //
        exp_dl, //
        exp_dl.subtract(RealScalar.ONE).multiply(dt).divide(dl));
  }

  @Override // from LieExponential
  public Tensor log(Tensor lambda_t) {
    Scalar lambda = Sign.requirePositive(lambda_t.Get(0));
    Scalar t = lambda_t.Get(1);
    if (lambda.equals(RealScalar.ONE))
      return Tensors.of(RealScalar.ZERO, t);
    Scalar log_l = Log.FUNCTION.apply(lambda);
    return Tensors.of( //
        log_l, //
        t.multiply(log_l).divide(lambda.subtract(RealScalar.ONE)));
  }
  // TODO JPH/OB ?
  // Kontrollieren wieseo "abs" benötigt wird. sonst gibt es vorzeichenfehler. EVTL Fehler in formel?
  // Log.FUNCTION.apply(lambda).multiply(t).divide(RealScalar.ONE.subtract(lambda)));
}
