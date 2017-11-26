// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.awt.geom.AffineTransform;

import ch.ethz.idsc.tensor.Tensor;

public enum AffineTransforms {
  ;
  /** @param matrix 3 x 3 in SE2
   * @return */
  public static AffineTransform toAffineTransform(Tensor matrix) {
    return new AffineTransform( //
        matrix.Get(0, 0).number().doubleValue(), //
        matrix.Get(1, 0).number().doubleValue(), //
        matrix.Get(0, 1).number().doubleValue(), //
        matrix.Get(1, 1).number().doubleValue(), //
        matrix.Get(0, 2).number().doubleValue(), //
        matrix.Get(1, 2).number().doubleValue());
  }
}
