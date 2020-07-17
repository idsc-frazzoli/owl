// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.awt.Dimension;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;

public class R2ArrayPlot implements GeodesicArrayPlot {
  private final Scalar radius;

  public R2ArrayPlot(Scalar radius) {
    this.radius = Objects.requireNonNull(radius);
  }

  @Override // from GeodesicArrayPlot
  public Scalar[][] array(int resolution, Function<Tensor, Scalar> tensorScalarFunction) {
    Tensor dx = Subdivide.of(radius.negate(), radius, resolution);
    Tensor dy = Subdivide.of(radius, radius.negate(), resolution);
    int rows = dy.length();
    int cols = dx.length();
    Scalar[][] array = new Scalar[rows][cols];
    IntStream.range(0, rows).parallel().forEach(cx -> {
      for (int cy = 0; cy < cols; ++cy) {
        Tensor point = Tensors.of(dx.get(cx), dy.get(cy));
        array[cy][cx] = tensorScalarFunction.apply(point);
      }
    });
    return array;
  }

  @Override
  public Tensor[][] arrai(int resolution, Function<Tensor, Tensor> tensorTensorFunction, Tensor fallback) {
    Tensor dx = Subdivide.of(radius.negate(), radius, resolution);
    Tensor dy = Subdivide.of(radius, radius.negate(), resolution);
    int rows = dy.length();
    int cols = dx.length();
    Tensor[][] array = new Tensor[rows][cols];
    IntStream.range(0, rows).parallel().forEach(cx -> {
      for (int cy = 0; cy < cols; ++cy) {
        Tensor point = Tensors.of(dx.get(cx), dy.get(cy));
        array[cy][cx] = tensorTensorFunction.apply(point);
      }
    });
    return array;
  }

  @Override // from GeodesicArrayPlot
  public Tensor pixel2model(Dimension dimension) {
    Tensor range = Tensors.of(radius, radius).multiply(RealScalar.of(2)); // model
    Tensor scale = Tensors.vector(dimension.width, dimension.height) //
        .pmul(range.map(Scalar::reciprocal)); // model 2 pixel
    return Dot.of( //
        Se2Matrix.translation(range.multiply(RationalScalar.HALF.negate())), //
        DiagonalMatrix.with(scale.map(Scalar::reciprocal).append(RealScalar.ONE)), // pixel 2 model
        Se2Matrix.flipY(dimension.height));
  }
}
