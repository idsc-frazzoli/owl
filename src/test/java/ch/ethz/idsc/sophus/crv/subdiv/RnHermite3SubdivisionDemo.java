// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import java.io.IOException;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Put;

/* package */ enum RnHermite3SubdivisionDemo {
  ;
  public static void main(String[] args) throws IOException {
    Tensor control = Tensors.fromString("{{0, 0}, {1, 0}, {0, -1}, {-1/2, 1}}");
    HermiteSubdivision hermiteSubdivision = RnHermite3Subdivision.string(control);
    for (int count = 1; count <= 5; ++count)
      hermiteSubdivision.iterate();
    Tensor tensor = hermiteSubdivision.iterate();
    Put.of(HomeDirectory.file("conti.file"), tensor);
  }
}
