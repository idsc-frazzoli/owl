// code by ob
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.sca.Power;

/* package */ enum StaticHelperCausal {
  ;
  /** @param mask of weights of affine combination [a1, a2, ..., an]
   * @return weights for iterative geodesic averages from left to right
   * @throws Exception if mask is not a vector, or empty */
  public static Tensor splits(Tensor mask) {
    Tensor splits = Tensors.empty();
    Scalar lambda;
    for (int index = 0; index < mask.length(); ++index) {
      lambda = index == 0 //
          ? RealScalar.ONE
          : mask.Get(index - 1);
      Scalar sign = Power.of(RealScalar.ONE.negate(), mask.length() - index);
      for (int subindex = index; subindex < mask.length(); ++subindex)
        lambda = lambda.multiply(mask.Get(subindex).subtract(RealScalar.ONE)).multiply(sign);
      splits.append(lambda);
    }
    splits.append(Last.of(mask));
    return splits;
  }
}
