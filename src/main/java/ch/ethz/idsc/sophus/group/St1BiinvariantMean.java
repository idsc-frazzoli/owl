// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.math.AffinityQ;
import ch.ethz.idsc.sophus.math.BiinvariantMeanInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Log;

/** @param sequence of (lambda_i, t_i) points in ST(1) and weights non-negative and normalized
 * @return associated biinvariant meanb which is the solution to the barycentric equation
 * Source: "Bi-invariant Means in Lie Groups. Application toLeft-invariant Polyaffine Transformations." p29
 * Vincent Arsigny - Xavier Pennec - Nicholas Ayache */
public enum St1BiinvariantMean implements BiinvariantMeanInterface {
  INSTANCE;
  // ---
  @Override
  public Tensor mean(Tensor sequence, Tensor weights) {
    AffinityQ.requirePositive(weights);
    // ---
    Scalar lambdaMean = Exp.FUNCTION.apply((Scalar) Tensor.of(sequence.stream().map(lambda_t -> Log.FUNCTION.apply(lambda_t.Get(0)))).dot(weights));
    Tensor alpha = Tensors.empty();
    // if is necessary for case lambda = lambdaMean (unspecific in paper)
    for (int index = 0; index < sequence.length(); ++index) {
      Scalar lambda = sequence.get(index).Get(0);
      Scalar a = lambda.equals(lambdaMean) //
          ? RealScalar.ONE//
          : Log.FUNCTION.apply(lambda.divide(lambdaMean)).divide(lambda.divide(lambdaMean).subtract(RealScalar.ONE));
      alpha.append(a);
    }
    Scalar Z = ((Scalar) alpha.dot(weights)).reciprocal();
    // ---
    Scalar sum = RealScalar.ZERO;
    for (int index = 0; index < sequence.length(); ++index) {
      Scalar t = sequence.get(index).Get(1);
      sum = sum.add(t.multiply(weights.Get(index).multiply(alpha.Get(index))));
    }
    Scalar tMean = Z.multiply(sum);
    return Tensors.of(lambdaMean, tMean);
  }
}
