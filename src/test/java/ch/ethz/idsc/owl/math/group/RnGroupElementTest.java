// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class RnGroupElementTest extends TestCase {
  public void testSimple() {
    RnGroupElement rnGroupElement = new RnGroupElement(Tensors.vector(1, 2, 3));
    Tensor result = rnGroupElement.combine(Tensors.vector(4, -2, -7));
    assertEquals(result, Tensors.vector(5, 0, -4));
    assertTrue(ExactScalarQ.all(result));
  }
}
