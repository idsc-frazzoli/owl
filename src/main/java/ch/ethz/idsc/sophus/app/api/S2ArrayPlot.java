// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.awt.Dimension;
import java.util.function.Function;

import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.sca.Sqrt;

/* package */ class S2ArrayPlot implements GeodesicArrayPlot {
  @Override // from GeodesicArrayPlot
  public Tensor array(int resolution, Function<Tensor, ? extends Tensor> function, Tensor fallback) {
    double rad = rad();
    Tensor dx = Subdivide.of(-rad, +rad, resolution);
    Tensor dy = Subdivide.of(+rad, -rad, resolution);
    return Tensor.of(dy.stream().parallel() //
        .map(vy -> Tensor.of(dx.stream().map(vx -> {
          Tensor point = Tensors.of(vx, vy); // in R2
          Scalar z2 = RealScalar.ONE.subtract(Norm2Squared.ofVector(point));
          return Sign.isPositive(z2) ? function.apply(point.append(Sqrt.FUNCTION.apply(z2))) : fallback;
        }))));
  }

  @Override // from GeodesicArrayPlot
  public Tensor pixel2model(Dimension dimension) {
    double rad = rad();
    Tensor range = Tensors.vector(rad, rad).multiply(RealScalar.of(2)); // model
    Tensor scale = Tensors.vector(dimension.width, dimension.height) //
        .pmul(range.map(Scalar::reciprocal)); // model 2 pixel
    return Dot.of( //
        Se2Matrix.translation(range.multiply(RationalScalar.HALF.negate())), //
        DiagonalMatrix.with(scale.map(Scalar::reciprocal).append(RealScalar.ONE)), // pixel 2 model
        Se2Matrix.flipY(dimension.height));
  }

  private static double rad() {
    return 1;
  }
}
