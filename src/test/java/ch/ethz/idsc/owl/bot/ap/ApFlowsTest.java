// code by jph
package ch.ethz.idsc.owl.bot.ap;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Degree;
import junit.framework.TestCase;

public class ApFlowsTest extends TestCase {
  public void testSimple() {
    FlowsInterface flowsInterface = ApFlows.of(Degree.of(10), Tensors.vector(0, 1, 2));
    Collection<Flow> collection = flowsInterface.getFlows(10);
    assertEquals(collection.size(), 33);
  }
}
