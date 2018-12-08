// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class BSplineLimitMaskTest extends TestCase {
  public void testLimitMask() {
    assertEquals(BSplineLimitMask.FUNCTION.apply(0), Tensors.fromString("{1}"));
    assertEquals(BSplineLimitMask.FUNCTION.apply(1), Tensors.fromString("{1/6, 2/3, 1/6}"));
    assertEquals(BSplineLimitMask.FUNCTION.apply(2), Tensors.fromString("{1/120, 13/60, 11/20, 13/60, 1/120}"));
    assertEquals(BSplineLimitMask.FUNCTION.apply(3), Tensors.fromString("{1/5040, 1/42, 397/1680, 151/315, 397/1680, 1/42, 1/5040}"));
  }
}
