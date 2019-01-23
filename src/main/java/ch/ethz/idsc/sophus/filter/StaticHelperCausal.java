// code by ob
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Power;

/* package */ enum StaticHelperCausal {
  ;
  private static final Scalar TWO = RealScalar.of(2);

  /** @param mask of weights of affine comination [a1, a2, ... , an]
   * @return weights for iterative geodesic averages from left to right **/
  public static Tensor splits(Tensor mask) {
    Tensor splits = Tensors.empty();
    Scalar lambda;
    for (int index = 0; index < mask.length(); index++) {
      if (index == 0) {
        lambda = RealScalar.ONE;
      } else {
        lambda = mask.Get(index - 1);
      }
      Scalar sign = Power.of(RealScalar.ONE.negate(), mask.length() - index);
      for (int subindex = index; subindex < mask.length(); subindex++) {
        lambda = lambda.multiply(mask.Get(subindex).subtract(RealScalar.ONE)).multiply(sign);
      }
      splits.append(lambda);
    }
    splits.append(mask.Get(mask.length() - 1));
    return splits;
  }
}
