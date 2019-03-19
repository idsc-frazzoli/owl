// code by ob
package ch.ethz.idsc.sophus.app.ob;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum DeuniformData {
  ;
  /** @param Uniform sequence p
   * @param ratio of data to be left out
   * @return Randomized nonuniform data */
  public static Tensor of(Tensor data, Scalar q) {
    Tensor result = Tensors.empty();
    for (int index = 0; index < data.length(); index++) {
      if (Scalars.lessEquals(q, RealScalar.of(Math.random()))) {
        result.append(data.get(index));
      }
    }
    return result;
  }
}
