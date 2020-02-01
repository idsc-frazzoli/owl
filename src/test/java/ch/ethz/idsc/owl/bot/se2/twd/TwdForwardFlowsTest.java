// code by jph
package ch.ethz.idsc.owl.bot.se2.twd;

import java.util.Collection;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class TwdForwardFlowsTest extends TestCase {
  public void testSimple() {
    TwdFlows twdFlows = new TwdForwardFlows(Quantity.of(3, "m*s^-1"), Quantity.of(1, "m"));
    int n = 3;
    Collection<Tensor> collection = twdFlows.getFlows(n);
    assertEquals(collection.size(), 2 * n + 1);
  }

  public void testZeros() {
    TwdFlows twdFlows = new TwdForwardFlows(Quantity.of(3, "m*s^-1"), Quantity.of(1, "m*rad^-1"));
    int n = 3;
    Collection<Tensor> collection = twdFlows.getFlows(n);
    for (Tensor flow : collection)
      assertFalse(Chop._05.allZero(flow));
  }
}
