// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.Dimension;
import java.util.stream.IntStream;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.math.TensorNorm;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.sca.Sqrt;

/* package */ class S2LineDistanceDemo extends A2KrigingDemo {
  private static final Tensor INITIAL = Tensors.fromString("{{-0.5, 0, 0}, {0.5, 0, 0}}").unmodifiable();
  private final SpinnerLabel<SnLineDistances> spinnerLineDistances = SpinnerLabel.of(SnLineDistances.values());

  public S2LineDistanceDemo() {
    super(GeodesicDisplays.S2_ONLY);
    spinnerLineDistances.addToComponentReduced(timerFrame.jToolBar, new Dimension(120, 28), "line distance");
    // ---
    setControlPointsSe2(INITIAL);
    // ---
    Tensor model2pixel = timerFrame.geometricComponent.getModel2Pixel();
    timerFrame.geometricComponent.setModel2Pixel(Tensors.vector(5, 5, 1).pmul(model2pixel));
    // ---
    timerFrame.configCoordinateOffset(400, 400);
  }

  TensorNorm tensorNorm() {
    Tensor cp = getGeodesicControlPoints();
    return 1 < cp.length() //
        ? spinnerLineDistances.getValue().lineDistance().tensorNorm(cp.get(0), cp.get(1))
        : t -> RealScalar.ZERO;
  }

  @Override
  Scalar[][] array(int resolution, TensorScalarFunction tensorScalarFunction) {
    TensorNorm tensorNorm = tensorNorm();
    double rad = rad();
    Tensor dx = Subdivide.of(-rad, +rad, resolution);
    Tensor dy = Subdivide.of(+rad, -rad, resolution);
    int rows = dy.length();
    int cols = dx.length();
    Scalar[][] array = new Scalar[rows][cols];
    Clip clip = Clips.unit();
    IntStream.range(0, rows).parallel().forEach(cx -> {
      for (int cy = 0; cy < cols; ++cy) {
        Tensor point = Tensors.of(dx.get(cx), dy.get(cy)); // in R2
        Scalar z2 = RealScalar.ONE.subtract(Norm2Squared.ofVector(point));
        if (Sign.isPositive(z2)) {
          Scalar z = Sqrt.FUNCTION.apply(z2);
          Tensor xyz = point.append(z);
          array[cy][cx] = clip.apply(tensorNorm.norm(xyz));
        } else
          array[cy][cx] = DoubleScalar.INDETERMINATE;
      }
    });
    return array;
  }

  @Override
  double rad() {
    return 1;
  }

  public static void main(String[] args) {
    new S2LineDistanceDemo().setVisible(1200, 600);
  }
}
