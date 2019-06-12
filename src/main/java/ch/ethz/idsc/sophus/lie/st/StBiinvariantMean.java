// code by ob, jph
package ch.ethz.idsc.sophus.lie.st;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.math.win.AffineQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** @param sequence of (lambda_i, t_i) points in ST(n) and weights non-negative and normalized
 * @return associated biinvariant mean which is the solution to the barycentric equation
 * 
 * Reference 1:
 * "Exponential Barycenters of the Canonical Cartan Connection and Invariant Means on Lie Groups"
 * by Xavier Pennec, Vincent Arsigny, p.26, Section 4.1:
 * "ST (n) is one of the most simple non-compact and non-commutative Lie groups. As expected for
 * such Lie groups, it has no bi-invariant metric."
 * 
 * Reference 2:
 * "Bi-invariant Means in Lie Groups. Application to Left-invariant Polyaffine Transformations."
 * by Vincent Arsigny, Xavier Pennec, Nicholas Ayache, p.29 */
public enum StBiinvariantMean implements BiinvariantMean {
  INSTANCE;
  // ---
  private static class Alpha implements ScalarUnaryOperator {
    private final Scalar lambdaBar;

    public Alpha(Scalar lambdaBar) {
      this.lambdaBar = lambdaBar;
    }

    @Override
    public Scalar apply(Scalar lambda) {
      Scalar ratio = lambda.divide(lambdaBar);
      Scalar den = ratio.subtract(RealScalar.ONE);
      return Scalars.isZero(den) //
          ? RealScalar.ONE // Limit[Log[p]/(p - 1), p -> 1] == 1
          : Log.FUNCTION.apply(ratio).divide(den);
    }
  }

  @Override // from BiinvariantMean
  public Tensor mean(Tensor sequence, Tensor weights) {
    AffineQ.require(weights);
    Tensor lambdas = sequence.get(Tensor.ALL, 0);
    // Reference 1, p.27
    Scalar lambdaBar = Exp.FUNCTION.apply(lambdas.map(Log.FUNCTION).dot(weights).Get());
    Tensor prod = weights.pmul(lambdas.map(new Alpha(lambdaBar)));
    Tensor sum = prod.dot(sequence.get(Tensor.ALL, 1));
    return Tensors.of( //
        lambdaBar, // "weighted geometric mean of scalings"
        sum.divide(Total.ofVector(prod))); // "scalings reweighed arithmetic mean of translations"
  }
}
