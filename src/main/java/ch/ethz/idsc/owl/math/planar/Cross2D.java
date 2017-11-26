// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;

public enum Cross2D {
  ;
  /** Cross[{x, y}] == {-y, x}
   * 
   * Cross[{x, y}] == RotationMatrix.of[90 degree] . {x, y}
   * 
   * @param vector with two entries
   * @return */
  public static Tensor of(Tensor vector) {
    if (vector.length() == 2)
      return Tensors.of(vector.Get(1).negate(), vector.Get(0));
    throw TensorRuntimeException.of(vector);
  }
}
