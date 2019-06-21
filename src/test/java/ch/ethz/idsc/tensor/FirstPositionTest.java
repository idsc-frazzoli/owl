// code by jph
package ch.ethz.idsc.tensor;

import junit.framework.TestCase;

public class FirstPositionTest extends TestCase {
  public void testSimple() {
    int index = FirstPosition.of(Tensors.vector(5, 6, 7, 8, 9), RealScalar.of(7)).getAsInt();
    assertEquals(index, 2);
  }
}
