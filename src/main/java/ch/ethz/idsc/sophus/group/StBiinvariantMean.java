// code by ob, jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.AffineQ;
import ch.ethz.idsc.sophus.math.BiinvariantMeanInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** @param sequence of (lambda_i, t_i) points in ST(1) and weights non-negative and normalized
 * @return associated biinvariant meanb which is the solution to the barycentric equation
 * Source: "Bi-invariant Means in Lie Groups. Application toLeft-invariant Polyaffine Transformations." p29
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
      return lambda.equals(lambdaMean) //
          ? RealScalar.ONE //
          : Log.FUNCTION.apply(lambda.divide(lambdaMean)).divide(lambda.divide(lambdaMean).subtract(RealScalar.ONE));
    }
  }

  @Override // from BiinvariantMeanInterface
  public Tensor mean(Tensor sequence, Tensor weights) {
    AffineQ.require(weights);
    Tensor lambdas = sequence.get(Tensor.ALL, 0);
    Scalar lambdaMean = Exp.FUNCTION.apply((Scalar) lambdas.map(Log.FUNCTION).dot(weights));
    Tensor prod = weights.pmul(lambdas.map(new Quotient(lambdaMean)));
    Tensor sum = prod.dot(sequence.get(Tensor.ALL, 1));
    return Tensors.of(lambdaMean, sum.divide(Total.ofVector(prod)));
  }
}
