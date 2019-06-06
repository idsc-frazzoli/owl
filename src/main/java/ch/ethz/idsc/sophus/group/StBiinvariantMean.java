// code by ob, jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.AffineQ;
import ch.ethz.idsc.sophus.math.BiinvariantMeanInterface;
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
 * Reference:
 * "Bi-invariant Means in Lie Groups. Application to Left-invariant Polyaffine Transformations." p.29
 * Vincent Arsigny - Xavier Pennec - Nicholas Ayache */
public enum StBiinvariantMean implements BiinvariantMeanInterface {
  INSTANCE;
  // ---
  private static class Quotient implements ScalarUnaryOperator {
    private final Scalar lambdaMean;

    public Quotient(Scalar lambdaMean) {
      this.lambdaMean = lambdaMean;
    }

    @Override
    public Scalar apply(Scalar lambda) {
      Scalar ratio = lambda.divide(lambdaMean);
      Scalar den = ratio.subtract(RealScalar.ONE);
      return Scalars.isZero(den) //
          ? RealScalar.ONE // Limit[Log[p]/(p - 1), p -> 1] == 1
          : Log.FUNCTION.apply(ratio).divide(den);
    }
  }

  @Override // from BiinvariantMeanInterface
  public Tensor mean(Tensor sequence, Tensor weights) {
    AffineQ.require(weights);
    Tensor lambdas = sequence.get(Tensor.ALL, 0);
    Scalar lambdaMean = Exp.FUNCTION.apply(lambdas.map(Log.FUNCTION).dot(weights).Get());
    Tensor prod = weights.pmul(lambdas.map(new Quotient(lambdaMean)));
    Tensor sum = prod.dot(sequence.get(Tensor.ALL, 1));
    return Tensors.of(lambdaMean, sum.divide(Total.ofVector(prod)));
  }
}
