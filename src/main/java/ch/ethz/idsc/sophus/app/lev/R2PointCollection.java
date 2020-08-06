// code by jph
package ch.ethz.idsc.sophus.app.lev;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum R2PointCollection {
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
  public static final Tensor MISC = Tensors.matrix(new Number[][] { //
      { 0.000, 0.000, 0.000 }, //
      { -0.133, -1.583, 0.000 }, //
      { -0.333, -0.458, 0.000 }, //
      { -0.483, 0.242, 0.000 }, //
      { 0.733, 0.450, 0.000 }, //
      { -0.121, 0.753, 0.000 }, //
      { 1.075, -0.375, 0.000 }, //
      { 0.415, -0.750, 0.000 } }).unmodifiable();
}
