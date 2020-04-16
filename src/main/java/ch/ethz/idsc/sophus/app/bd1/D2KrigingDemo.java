// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ abstract class D2KrigingDemo extends A2KrigingDemo {
  public D2KrigingDemo(GeodesicDisplay geodesicDisplay) {
    super(geodesicDisplay);
  }

  @Override
  final Scalar[][] array(int resolution, TensorScalarFunction tensorScalarFunction) {
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
}
