// code by astoll
package ch.ethz.idsc.owl.bot.balloon;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import junit.framework.TestCase;

public class BalloonFlowsTest extends TestCase {
  public void testSimple() {
    FlowsInterface flowsInterface = BalloonFlows.of(RealScalar.of(10));
    Collection<Tensor> collection = flowsInterface.getFlows(0);
    assertEquals(collection.size(), 2);
  }

  public void testFail() {
    FlowsInterface flowsInterface = BalloonFlows.of(RealScalar.of(10));
    try {
      flowsInterface.getFlows(-1);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
