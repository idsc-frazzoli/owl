// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.awt.Dimension;
import java.io.Serializable;
import java.util.function.Function;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.sca.Sqrt;

/* package */ class S2ArrayPlot implements GeodesicArrayPlot, Serializable {
  private static final double RADIUS = 1;

  @Override // from GeodesicArrayPlot
  public Tensor raster(int resolution, Function<Tensor, ? extends Tensor> function, Tensor fallback) {
    Tensor dx = Subdivide.of(-RADIUS, +RADIUS, resolution);
    Tensor dy = Subdivide.of(+RADIUS, -RADIUS, resolution);
    return Tensor.of(dy.stream().parallel() //
        .map(vy -> Tensor.of(dx.stream().map(vx -> {
          Tensor point = Tensors.of(vx, vy); // in R2
          Scalar z2 = RealScalar.ONE.subtract(Norm2Squared.ofVector(point));
          return Sign.isPositive(z2) ? function.apply(point.append(Sqrt.FUNCTION.apply(z2))) : fallback;
        }))));
  }

  @Override // from GeodesicArrayPlot
  public Tensor pixel2model(Dimension dimension) {
    Tensor range = Tensors.vector(RADIUS, RADIUS).multiply(RealScalar.of(2)); // model
    Tensor xy = range.multiply(RationalScalar.HALF.negate());
    return GeodesicArrayPlot.pixel2model(xy, range, dimension);
  }
}
