// code by jph
package ch.ethz.idsc.sophus.crv;

import ch.ethz.idsc.sophus.math.TensorNorm;
import ch.ethz.idsc.tensor.Tensor;

public interface LineDistance {
  TensorNorm tensorNorm(Tensor p, Tensor q);
}
