// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.awt.Dimension;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;

/* package */ class R2ArrayPlot implements GeodesicArrayPlot, Serializable {
  private final Scalar radius;

  public R2ArrayPlot(Scalar radius) {
    this.radius = Objects.requireNonNull(radius);
  }

  @Override // from GeodesicArrayPlot
  public Tensor raster(int resolution, Function<Tensor, ? extends Tensor> function, Tensor fallback) {
    Tensor dx = Subdivide.of(radius.negate(), radius, resolution);
    Tensor dy = Subdivide.of(radius, radius.negate(), resolution);
    return Tensor.of(dy.stream().parallel() //
        .map(vy -> Tensor.of(dx.stream().map(px -> Tensors.of(px, vy)).map(function))));
  }

  @Override // from GeodesicArrayPlot
  public Tensor pixel2model(Dimension dimension) {
    Tensor range = Tensors.of(radius, radius).multiply(RealScalar.of(2));
    Tensor xy = range.multiply(RationalScalar.HALF.negate());
    return GeodesicArrayPlot.pixel2model(xy, range, dimension);
  }
}
