// code by ob
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.sca.Chop;

/* package */ enum StaticHelperCausal {
  ;
  /** @param mask of weights of affine combination [a1, a2, ..., an]
   * @return weights for iterative geodesic averages from left to right
   * @throws Exception if mask is not a vector, or empty */
  public static Tensor splits(Tensor mask) {
    Tensor splits = Tensors.empty();
    Scalar factor = mask.Get(0);
    Scalar sum = mask.Get(0);
    for (int index = 0; index < mask.length() - 2; ++index) {
      factor = factor.add(mask.Get(index + 1));
      Scalar lambda = mask.Get(index + 1).divide(factor);
      splits.append(lambda);
      sum = sum.add(mask.Get(index + 1));
    }
    sum = sum.add(mask.Get(mask.length() - 1));
    splits.append(Last.of(mask));
    if (!Chop._12.close(sum, RealScalar.ONE))
      throw TensorRuntimeException.of(sum);
    return splits;
  }
}
