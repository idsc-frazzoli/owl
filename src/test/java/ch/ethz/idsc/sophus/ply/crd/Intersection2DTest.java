// code by jph
package ch.ethz.idsc.sophus.ply.crd;

import java.util.Optional;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class Intersection2DTest extends TestCase {
  public void testOrthogonal() {
    Tensor tensor = Intersection2D.of(Tensors.vector(-10, 0), Tensors.vector(1, 0), Tensors.vector(2, -1), Tensors.vector(0, 1)).get();
    assertEquals(tensor, Tensors.vector(2, 0));
    ExactTensorQ.require(tensor);
  }

  public void testQuantity() {
    Tensor tensor = Intersection2D.of( //
        Tensors.fromString("{-10[m], 0[m]}"), //
        Tensors.fromString("{1[m], 0[m]}"), //
        Tensors.fromString("{2[m], -1[m]}"), //
        Tensors.fromString("{0[m], 1[m]}")).get();
    assertEquals(tensor, Tensors.fromString("{2[m], 0[m]}"));
    ExactTensorQ.require(tensor);
  }

  public void testParallel() {
    Optional<Tensor> optional = Intersection2D.of(Tensors.vector(0, 0), Tensors.vector(1, 0), Tensors.vector(0, 1), Tensors.vector(1, 0));
    assertFalse(optional.isPresent());
  }

  public void testIdentical() {
    Optional<Tensor> optional = Intersection2D.of(Tensors.vector(0, 0), Tensors.vector(1, 0), Tensors.vector(0, 0), Tensors.vector(1, 0));
    assertFalse(optional.isPresent());
  }
}
