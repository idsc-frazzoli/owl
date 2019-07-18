// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class ClothoidCurvatureTest extends TestCase {
  public void testSimple() {
    Tensor p = Tensors.vector(1, 2, 1);
    Tensor q = Tensors.vector(8, 6, 2);
    ClothoidTerminalRatios clothoidTerminalRatios = ClothoidTerminalRatios.of(p, q);
    Scalar head = clothoidTerminalRatios.head();
    System.out.println(head);
    ClothoidCurvature clothoidCurvature = new ClothoidCurvature(p, q);
    Scalar scalar = clothoidCurvature.apply(RealScalar.ZERO);
    System.out.println(scalar);
  }
}
