// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.awt.Dimension;
import java.util.function.Function;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface GeodesicArrayPlot {
  Scalar[][] array(int resolution, Function<Tensor, Scalar> tensorScalarFunction);

  Tensor pixel2model(Dimension dimension);
}
