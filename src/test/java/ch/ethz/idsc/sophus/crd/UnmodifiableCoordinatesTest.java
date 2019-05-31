// code by gjoel
package ch.ethz.idsc.sophus.crd;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class UnmodifiableCoordinatesTest extends TestCase {
  private static final CoordinateSystem CS = CoordinateSystem.of("test");

  public void testSimple() {
    Tensor vector = Tensors.vector(1, 2, 3);
    testSimple(new UnmodifiableCoordinates(vector, CS));
    testSimple(CS.origin().unmodifiable());
  }

  public static void testSimple(Tensor coords) {
    boolean thrown = false;
    try {
      coords.set(RealScalar.ZERO, 1);
    } catch (UnsupportedOperationException e) {
      thrown = true;
    }
    assertTrue(thrown);
  }
}
