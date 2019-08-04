// code by jph
package ch.ethz.idsc.sophus.hs.r2;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.tensor.Parallelize;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Flatten;
import ch.ethz.idsc.tensor.alg.Outer;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Norm;

/** used as logo of edelweis */
/* package */ class Se2onR2Demo {
  private static final int RES = 192;
  private final Tensor actions = Tensors.of( //
      Tensors.vector(+0.1, +0.2, +0.3), //
      Tensors.vector(-0.3, +0.2, -0.5), //
      Tensors.vector(-0.2, -0.4, -1.0), //
      Tensors.vector(+0.2, -0.7, -1.5)).unmodifiable();

  public Scalar min(Tensor start) {
    Tensor seed = Tensors.of(start);
    for (int count = 0; count < 4; ++count)
      seed = Flatten.of(Outer.of(Se2onR2Demo::action, actions, seed), 1);
    Tensor tensor = Tensor.of(seed.stream().map(Norm._2::ofVector));
    return RealScalar.of(ArgMin.of(tensor));
    // return tensor.stream().reduce(Min::of).get().Get();
  }

  private static Tensor action(Tensor xya, Tensor uv) {
    return new Se2GroupElement(xya).combine(uv.copy().append(RealScalar.ZERO)).extract(0, 2);
  }

  public static void main(String[] args) throws IOException {
    File folder = HomeDirectory.Pictures(Se2onR2Demo.class.getSimpleName());
    folder.mkdir();
    if (!folder.isDirectory())
      return;
    Tensor x = Subdivide.of(-2, +2, RES - 1);
    Tensor y = Subdivide.of(-2, +2, RES - 1);
    Se2onR2Demo se2onR2Demo = new Se2onR2Demo();
    Tensor matrix = Parallelize.matrix((i, j) -> se2onR2Demo.min(Tensors.of(x.Get(i), y.Get(j))), x.length(), y.length());
    for (ColorDataGradients colorDataGradients : ColorDataGradients.values()) {
      Tensor image = ArrayPlot.of(matrix, colorDataGradients);
      Export.of(new File(folder, colorDataGradients.name() + ".png"), image);
    }
  }
}
