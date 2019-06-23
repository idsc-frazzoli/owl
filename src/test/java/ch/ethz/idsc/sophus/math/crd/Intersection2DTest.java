// code by jph
package ch.ethz.idsc.sophus.math.crd;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class Intersection2DTest extends TestCase {
  // public void testSimple() {
  // Tensor aux1 = PowerCoordinates.aux(Tensors.vector(1, 2), Tensors.vector(3, 4), RealScalar.of(.2), RealScalar.of(.4));
  // Tensor aux2 = PowerCoordinates.aux(Tensors.vector(3, 4), Tensors.vector(5, 2.5), RealScalar.of(.4), RealScalar.of(.8));
  // Tensor inter = Intersection2D.of(aux1.get(0), aux1.get(1), aux2.get(0), aux2.get(1));
  // Chop._10.requireClose(inter, Tensors.vector(2.957142857142857, 1.9928571428571429));
  // }
  public void testOrthogonal() {
    Tensor tensor = Intersection2D.of(Tensors.vector(-10, 0), Tensors.vector(1, 0), Tensors.vector(2, -1), Tensors.vector(0, 1));
    assertEquals(tensor, Tensors.vector(2, 0));
    ExactTensorQ.require(tensor);
  }
}
