// code by jph
package ch.ethz.idsc.sophus.crv.spline;

import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class MonomialExtrapolationMaskTest extends TestCase {
  public void testSimple() {
    assertEquals(MonomialExtrapolationMask.INSTANCE.apply(1), Tensors.vector(1));
    assertEquals(MonomialExtrapolationMask.INSTANCE.apply(2), Tensors.vector(-1, 2));
    assertEquals(MonomialExtrapolationMask.INSTANCE.apply(3), Tensors.vector(1, -3, 3));
    assertEquals(MonomialExtrapolationMask.INSTANCE.apply(4), Tensors.vector(-1, 4, -6, 4));
  }
}
