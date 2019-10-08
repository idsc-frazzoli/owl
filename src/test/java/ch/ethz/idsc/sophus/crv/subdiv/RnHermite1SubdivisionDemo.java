// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import java.io.IOException;

import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Put;

/* package */ enum RnHermite1SubdivisionDemo {
  ;
  public static void main(String[] args) throws IOException {
    Tensor control = Tensors.fromString("{{0, 0}, {1, 0}, {0, -1}, {-1/2, 1}}");
    TensorIteration tensorIteration = RnHermite1Subdivision.string(control);
    for (int count = 1; count <= 5; ++count)
      tensorIteration.iterate();
    Tensor iterate = tensorIteration.iterate();
    Put.of(HomeDirectory.file("merrien.file"), iterate);
    // System.out.println(iterate);
  }
}
