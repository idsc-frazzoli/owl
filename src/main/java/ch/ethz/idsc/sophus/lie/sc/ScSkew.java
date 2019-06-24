// code by ob, jph
package ch.ethz.idsc.sophus.lie.sc;

import ch.ethz.idsc.sophus.lie.st.StBiinvariantMean;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Sign;

/** "Exponential Barycenters of the Canonical Cartan Connection and Invariant Means on Lie Groups"
 * by Xavier Pennec, Vincent Arsigny, p.27, Section 4.1
 * 
 * @see StBiinvariantMean */
public class ScSkew implements ScalarUnaryOperator {
  private final Scalar mean;

  /** @param mean non-zero */
  public ScSkew(Scalar mean) {
    this.mean = Sign.requirePositive(mean);
  }

  @Override
  public Scalar apply(Scalar lambda) {
    Scalar ratio = lambda.divide(mean);
    Scalar den = ratio.subtract(RealScalar.ONE);
    return Scalars.isZero(den) //
        ? RealScalar.ONE // Limit[Log[p]/(p - 1), p -> 1] == 1
        : Log.FUNCTION.apply(ratio).divide(den);
  }
}