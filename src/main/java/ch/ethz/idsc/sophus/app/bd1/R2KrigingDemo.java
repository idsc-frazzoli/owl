// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class R2KrigingDemo extends A2KrigingDemo {
  public R2KrigingDemo() {
    super(GeodesicDisplays.R2_H2);
    timerFrame.configCoordinateOffset(400, 400);
    // ---
    setControlPointsSe2(Tensors.fromString("{{0, 0, 1}, {3, 0, 1}, {-3, 1, 0}, {-2, -3, 0}, {1, 3, 0}}"));
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
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    IntStream.range(0, rows).parallel().forEach(cx -> {
      for (int cy = 0; cy < cols; ++cy) {
        Tensor point = geodesicDisplay.project(Tensors.of(dx.get(cx), dy.get(cy), RealScalar.ZERO));
        array[cy][cx] = clip.apply(tensorScalarFunction.apply(point));
      }
    });
    return array;
  }

  @Override
  double rad() {
    return 5;
  }

  public static void main(String[] args) {
    new R2KrigingDemo().setVisible(1000, 800);
  }
}
