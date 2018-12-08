// code by astoll
package ch.ethz.idsc.owl.bot.balloon;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class BalloonFlowsTest extends TestCase {
  public void testSimple() {
    FlowsInterface flowsInterface = //
        BalloonFlows.of(RealScalar.of(10), BalloonStateSpaceModels.defaultWithUnits());
    Collection<Flow> collection = flowsInterface.getFlows(0);
    assertEquals(collection.size(), 2);
  }

  public void testFail() {
    FlowsInterface flowsInterface = //
        BalloonFlows.of(RealScalar.of(10), BalloonStateSpaceModels.defaultWithUnits());
    try {
      flowsInterface.getFlows(-1);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
