// code by jph
package ch.ethz.idsc.sophus.app.lev;

import ch.ethz.idsc.tensor.Tensor;

public interface Classification {
  /** @param weights
   * @return */
  ClassificationResult result(Tensor weights);
}
