// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.util.Arrays;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class DodgsonSabinCurveSubdivisionTest extends TestCase {
  public void testThree() {
    Tensor a = Tensors.vector(1, 1.1);
    Tensor b = Tensors.vector(2.2, 0.5);
    Tensor c = Tensors.vector(3, 1.5);
    Tensor tensor = Tensors.of(a, b, c);
    Tensor string = DodgsonSabinCurveSubdivision.INSTANCE.string(tensor);
    assertEquals(Dimensions.of(string), Arrays.asList(5, 2));
    assertEquals(string.get(0), a);
    assertEquals(string.get(2), b);
    assertEquals(string.get(4), c);
  }

  public void testFour() {
    Tensor a = Tensors.vector(1, 1.1);
    Tensor b = Tensors.vector(2.2, 0.5);
    Tensor c = Tensors.vector(3, 1.5);
    Tensor d = Tensors.vector(3.5, 2.9);
    Tensor tensor = Tensors.of(a, b, c, d);
    Tensor string = DodgsonSabinCurveSubdivision.INSTANCE.string(tensor);
    assertEquals(Dimensions.of(string), Arrays.asList(7, 2));
    assertEquals(string.get(0), a);
    assertEquals(string.get(2), b);
    assertEquals(string.get(4), c);
    assertEquals(string.get(6), d);
    assertTrue(Chop._13.close(string.get(3), //
        Tensors.fromString("{2.64619661516195, 0.8388990046231528}")));
  }
}
