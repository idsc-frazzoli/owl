// code by ob
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;

/* package */ enum StaticHelperCausal {
  ;
  /** @param mask of weights of affine combination [a1, a2, ..., an]
   * @return weights for iterative geodesic averages from left to right
   * @throws Exception if mask is not a vector, or empty */
  public static Tensor splits(Tensor mask) {
    Tensor splits = Tensors.empty();
    Scalar lambda;
    for (int index = 0; index <= mask.length() - 3; ++index) {
      Scalar temp = RealScalar.ZERO;
      for (int subindex = 0; subindex <= index + 1; ++subindex) {
        temp = temp.add(mask.Get(subindex));
      }
      lambda = mask.Get(index + 1).divide(temp);
      splits.append(lambda);
    }
    splits.append(Last.of(mask));
    return splits;
  }
}
