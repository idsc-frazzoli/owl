// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.app.api.H2GeodesicDisplay;
import ch.ethz.idsc.sophus.krg.Kriging;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class H2KrigingDemo extends A2KrigingDemo {
  public H2KrigingDemo() {
    super(H2GeodesicDisplay.INSTANCE);
    setControlPointsSe2(Tensors.fromString("{{0.01, 0, 1}, {0.2, 0, 0}, {0.21, 0.2, 0}, {0, 0.2, 0}, {-1, 0.3, 1}}"));
    // ---
    timerFrame.configCoordinateOffset(400, 400);
  }

  @Override
  Scalar[][] array(int resolution, Kriging kriging) {
    double rad = rad();
    Tensor dx = Subdivide.of(-rad, +rad, resolution);
    Tensor dy = Subdivide.of(+rad, -rad, resolution);
    int rows = dy.length();
    int cols = dx.length();
    Scalar[][] array = new Scalar[rows][cols];
    Clip clip = Clips.unit();
    IntStream.range(0, rows).parallel().forEach(cx -> {
      for (int cy = 0; cy < cols; ++cy) {
        Tensor point = H2GeodesicDisplay.INSTANCE.project(Tensors.of(dx.get(cx), dy.get(cy))); // in H2
        array[cy][cx] = clip.apply((Scalar) kriging.estimate(point));
      }
    });
    return array;
  }

  @Override
  double rad() {
    return 5;
  }

  public static void main(String[] args) {
    new H2KrigingDemo().setVisible(1000, 800);
  }
}
