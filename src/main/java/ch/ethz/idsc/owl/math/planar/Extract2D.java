// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.Tensor;

public enum Extract2D {
  ;
  /** @param tensor
   * @return first two entries of given tensor
   * @throws Exception if given tensor does not contain at least two elements */
  public static Tensor of(Tensor tensor) {
    return tensor.extract(0, 2);
  }
}
