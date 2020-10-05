// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.awt.Dimension;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

import ch.ethz.idsc.sophus.hs.hn.HnWeierstrassCoordinate;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;

/* package */ class H2ArrayPlot implements GeodesicArrayPlot, Serializable {
  private static final long serialVersionUID = -7206313517468060655L;
  // ---
  private final Scalar radius;

  public H2ArrayPlot(Scalar radius) {
    this.radius = Objects.requireNonNull(radius);
  }

  @Override // from GeodesicArrayPlot
  public Tensor raster(int resolution, Function<Tensor, ? extends Tensor> function, Tensor fallback) {
    Tensor dx = Subdivide.of(radius.negate(), radius, resolution);
    Tensor dy = Subdivide.of(radius, radius.negate(), resolution);
    return Tensor.of(dy.stream().parallel() //
        .map(py -> Tensor.of(dx.stream() //
            .map(px -> Tensors.of(px, py)) //
            .map(HnWeierstrassCoordinate::toPoint) //
            .map(function))));
  }

  @Override // from GeodesicArrayPlot
  public Tensor pixel2model(Dimension dimension) {
    Tensor range = Tensors.of(radius, radius).multiply(RealScalar.TWO); // model
    Tensor scale = Tensors.vector(dimension.width, dimension.height) //
        .pmul(range.map(Scalar::reciprocal)); // model 2 pixel
    return Dot.of( //
        Se2Matrix.translation(range.multiply(RationalScalar.HALF.negate())), //
        DiagonalMatrix.with(scale.map(Scalar::reciprocal).append(RealScalar.ONE)), // pixel 2 model
        Se2Matrix.flipY(dimension.height));
  }
}
