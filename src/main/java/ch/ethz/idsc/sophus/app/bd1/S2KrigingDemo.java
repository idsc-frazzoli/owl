// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.app.api.S2GeodesicDisplay;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.sca.Sqrt;

/* package */ class S2KrigingDemo extends A2KrigingDemo {
  public S2KrigingDemo() {
    super(S2GeodesicDisplay.INSTANCE);
    setControlPointsSe2(Tensors.fromString("{{0.01, 0, 1}, {0.2, 0, 0}, {0.21, 0.2, 0}, {0, 0.2, 0}}"));
    // ---
    Tensor model2pixel = timerFrame.geometricComponent.getModel2Pixel();
    timerFrame.geometricComponent.setModel2Pixel(Tensors.vector(5, 5, 1).pmul(model2pixel));
    // ---
    timerFrame.configCoordinateOffset(400, 400);
  }

  @Override
  Scalar[][] array(int resolution, TensorScalarFunction tensorScalarFunction) {
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
          array[cy][cx] = clip.apply(tensorScalarFunction.apply(point.append(z)));
        } else
          array[cy][cx] = DoubleScalar.INDETERMINATE;
      }
    });
    return array;
  }

  @Override
  void prepare() {
    Tensor pointsSe2 = getControlPointsSe2().copy();
    pointsSe2.set(Max.function(RealScalar.ZERO), Tensor.ALL, 2);
    setControlPointsSe2(pointsSe2);
  }

  @Override
  double rad() {
    return 1;
  }

  public static void main(String[] args) {
    new S2KrigingDemo().setVisible(1000, 800);
  }
}
