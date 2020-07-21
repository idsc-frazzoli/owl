// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.awt.Dimension;
import java.util.function.Function;

import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;

/** @see ArrayPlot */
public interface GeodesicArrayPlot {
  /** @param resolution
   * @param function
   * @param fallback
   * @return */
  Tensor raster(int resolution, Function<Tensor, ? extends Tensor> function, Tensor fallback);

  /** @param dimension
   * @return */
  Tensor pixel2model(Dimension dimension);

  /** @param xy lower left corner
   * @param range of image in model space
   * @param dimension of image
   * @return */
  static Tensor pixel2model(Tensor xy, Tensor range, Dimension dimension) {
    // pixel 2 model
    Tensor scale = range.pmul(Tensors.vector(dimension.width, dimension.height).map(Scalar::reciprocal));
    return Dot.of( //
        Se2Matrix.translation(xy), //
        DiagonalMatrix.with(scale.append(RealScalar.ONE)), // TODO use pmul
        Se2Matrix.flipY(dimension.height));
  }
}
