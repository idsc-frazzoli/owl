// code by jph
package ch.ethz.idsc.sophus.gds;

import java.awt.Dimension;
import java.util.function.Function;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.nrm.Vector2NormSquared;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.sca.Sqrt;

/* package */ enum S2ArrayPlot implements GeodesicArrayPlot {
  INSTANCE;

  private static final double RADIUS = 1;

  @Override // from GeodesicArrayPlot
  public Tensor raster(int resolution, Function<Tensor, ? extends Tensor> function, Tensor fallback) {
    Tensor dx = Subdivide.of(-RADIUS, +RADIUS, resolution - 1);
    Tensor dy = Subdivide.of(+RADIUS, -RADIUS, resolution - 1);
    return Tensor.of(dy.stream().parallel() //
        .map(py -> Tensor.of(dx.stream() //
            .map(px -> Tensors.of(px, py)) // in R2
            .map(point -> {
              Scalar z2 = RealScalar.ONE.subtract(Vector2NormSquared.of(point));
              return Sign.isPositive(z2) ? function.apply(point.append(Sqrt.FUNCTION.apply(z2))) : fallback;
            }))));
  }

  @Override // from GeodesicArrayPlot
  public Tensor pixel2model(Dimension dimension) {
    Tensor range = Tensors.vector(RADIUS, RADIUS).multiply(RealScalar.TWO); // model
    Tensor xy = range.multiply(RationalScalar.HALF.negate());
    return GeodesicArrayPlot.pixel2model(xy, range, dimension);
  }
}
