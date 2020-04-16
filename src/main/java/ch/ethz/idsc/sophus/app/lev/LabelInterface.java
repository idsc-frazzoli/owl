// code by jph
package ch.ethz.idsc.sophus.app.lev;

import ch.ethz.idsc.tensor.Tensor;

public interface LabelInterface {
  /** @param weights
   * @return */
  int label(Tensor weights);
}
