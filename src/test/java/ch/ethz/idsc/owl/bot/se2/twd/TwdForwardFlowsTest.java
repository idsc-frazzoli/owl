// code by jph
package ch.ethz.idsc.owl.bot.se2.twd;

import java.util.Collection;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class TwdForwardFlowsTest extends TestCase {
  public void testSimple() {
    TwdFlows twdFlows = new TwdForwardFlows(Quantity.of(3, "m*s^-1"), Quantity.of(1, "m"));
    int n = 3;
    Collection<Flow> collection = twdFlows.getFlows(n);
    assertEquals(collection.size(), 2 * n + 1);
  }

  public void testZeros() {
    TwdFlows twdFlows = new TwdForwardFlows(Quantity.of(3, "m*s^-1"), Quantity.of(1, "m*rad^-1"));
    int n = 3;
    Collection<Flow> collection = twdFlows.getFlows(n);
    for (Flow flow : collection) {
      // System.out.println(Round.of(flow.getU()));
      assertFalse(Chop._05.allZero(flow.getU()));
    }
  }
}
