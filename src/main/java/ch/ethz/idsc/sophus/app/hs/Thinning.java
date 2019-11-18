// code by jph
package ch.ethz.idsc.sophus.app.hs;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum Thinning {
  ;
  public static Tensor of(Tensor tensor, int delta) {
    Tensor result = Tensors.empty();
    for (int index = 0; index < tensor.length(); index += delta)
      result.append(tensor.get(index));
    return result;
  }
}
