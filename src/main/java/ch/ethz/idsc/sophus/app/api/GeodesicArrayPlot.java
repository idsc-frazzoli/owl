// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.awt.Dimension;
import java.util.function.Function;

import ch.ethz.idsc.tensor.Tensor;

public interface GeodesicArrayPlot {
  /** @param resolution
   * @param tensorScalarFunction
   * @param fallback
   * @return */
  Tensor array(int resolution, Function<Tensor, ? extends Tensor> function, Tensor fallback);

  /** @param dimension
   * @return */
  Tensor pixel2model(Dimension dimension);
}
