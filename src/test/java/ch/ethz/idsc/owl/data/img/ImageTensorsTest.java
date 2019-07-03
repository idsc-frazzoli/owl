// code by jph
package ch.ethz.idsc.owl.data.img;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class ImageTensorsTest extends TestCase {
  public void testColor() {
    Tensor tensor = Tensors.fromString("{{{1, 2, 3, 4}, {5, 6, 7, 8}}}");
    Tensor reduce = ImageTensors.reduce(tensor, Tensors.vector(1, 2, 3, 4));
    assertEquals(reduce, Tensors.fromString("{{255, 0}}"));
  }

  public void testGrayscale() {
    Tensor tensor = Tensors.fromString("{{1, 2, 3, 4}, {5, 6, 7, 8}}");
    Tensor reduce = ImageTensors.reduce(tensor, RealScalar.of(2));
    assertEquals(reduce, Tensors.fromString("{{0, 255, 0, 0}, {0, 0, 0, 0}}"));
  }
}
