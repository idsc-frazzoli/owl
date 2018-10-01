// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.Tensor;

public enum Extract2D {
  ;
  public static Tensor of(Tensor vector) {
    return vector.extract(0, 2);
  }
}
