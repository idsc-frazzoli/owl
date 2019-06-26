// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import junit.framework.TestCase;

public class Clothoid1Test extends TestCase {
  public void testSimple() {
    ScalarTensorFunction scalarTensorFunction = Clothoid1.INSTANCE.curve(Tensors.vector(1, 2, 3), Array.zeros(3));
    assertTrue(scalarTensorFunction instanceof ClothoidCurve1);
  }
}
