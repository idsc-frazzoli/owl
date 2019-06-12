// code by jph
package ch.ethz.idsc.sophus.crv.spline;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class LanczosCurveTest extends TestCase {
  public void testSimple() {
    Tensor refine = LanczosCurve.refine(Tensors.vector(1, 2, 3, 2, 1, 2, 3), 100);
    assertEquals(refine.length(), 101);
  }
}
