package ch.ethz.idsc.owl.bot.balloon;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class BalloonFlowsTest extends TestCase {
  public void testSimple() {
    FlowsInterface flowsInterface = BalloonFlows.of(RealScalar.of(10), BalloonStateSpaceModels.defaultWithUnits());
    Collection<Flow> collection = flowsInterface.getFlows(10);
    assertEquals(collection.size(), 11);
  }
}
