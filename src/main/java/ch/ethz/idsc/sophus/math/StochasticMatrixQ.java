// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.Tensor;

/** row stochastic matrices */
public enum StochasticMatrixQ {
  ;
  /** @param tensor
   * @return given tensor
   * @throws Exception if tensor is not a row-stochastic matrix */
  public static Tensor requireRows(Tensor tensor) {
    tensor.stream().forEach(AffineQ::require);
    return tensor;
  }
}
