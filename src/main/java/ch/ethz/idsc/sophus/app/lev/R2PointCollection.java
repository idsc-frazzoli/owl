// code by jph
package ch.ethz.idsc.sophus.app.lev;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum R2PointCollection {
  ;
  public static final Tensor SOME = Tensors.matrix(new Number[][] { //
      { 0, 0, 0 }, //
      { -0.583, -2.317, 0.000 }, //
      { -2.133, -0.933, 0.000 }, //
      { -1.317, 1.567, 0.000 }, //
      { 1.800, 1.033, 0.000 }, //
      { 3.267, -0.550, 0.000 }, //
      { 2.583, -2.133, 0.000 } //
  }).unmodifiable();
}
