// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.awt.Dimension;
import java.util.function.Function;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ArrayPlot;

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
}
