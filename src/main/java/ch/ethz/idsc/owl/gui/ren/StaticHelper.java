// code by jph
package ch.ethz.idsc.owl.gui.ren;

import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum StaticHelper {
  ;
  private static final Tensor ZEROS = Tensors.vector(0, 0).unmodifiable();

  static Tensor length2(Tensor vector) {
    switch (vector.length()) {
    case 0:
      return ZEROS;
    case 1:
      return vector.append(RealScalar.ZERO);
    case 2:
      return vector;
    }
    return Extract2D.FUNCTION.apply(vector);
  }
}
