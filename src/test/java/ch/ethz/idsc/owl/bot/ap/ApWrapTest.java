// code by astoll
package ch.ethz.idsc.owl.bot.ap;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class ApWrapTest extends TestCase {
  public void testWrap() {
    double pa = 2 * Math.PI;
    Tensor toBeTested = Tensors.vector(100, 20, -30, 6.6);
    Tensor expected = Tensors.vector(100, 20, -30, 6.6 - pa);
    assertTrue(Chop._12.close(expected, ApWrap.INSTANCE.represent(toBeTested)));
    assertTrue(Chop._14.allZero(ApWrap.INSTANCE.difference(expected, toBeTested)));
  }
}
