// code by ob
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.AffineQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;

/* package */ enum StaticHelperCausal {
  ;
  /** @param mask of weights of affine combination [a1, a2, ..., an]
   * @return weights for iterative geodesic averages from left to right
   * @throws Exception if mask is not a vector, or empty, or entries do not add up to 1 */
  public static Tensor splits(Tensor mask) {
    AffineQ.require(mask);
    Tensor splits = Tensors.empty();
    Scalar factor = mask.Get(0);
    for (int index = 1; index < mask.length() - 1; ++index) {
      factor = factor.add(mask.get(index));
      Scalar lambda = mask.Get(index).divide(factor);
      splits.append(lambda);
    }
    splits.append(Last.of(mask));
    return splits;
  }
}
