// code by jph
package ch.ethz.idsc.owl.bot.se2.twd;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;
import junit.framework.TestCase;

public class TwdDuckieFlowsTest extends TestCase {
  public void testRadNoDuplicates() {
    Scalar ms = Quantity.of(3, "m*s^-1");
    Scalar sa = Quantity.of(0.567, "m*rad^-1");
    FlowsInterface flowsInterface = new TwdDuckieFlows(ms, sa);
    for (int res = 3; res <= 8; ++res) {
      Collection<Flow> controls = flowsInterface.getFlows(res);
      Set<Tensor> set = new HashSet<>();
      for (Flow flow : controls) {
        Tensor key = flow.getU().map(Round._3);
        assertTrue(set.add(key));
      }
    }
  }

  public void testNoDuplicates() {
    Scalar ms = Quantity.of(3, "m*s^-1");
    Scalar sa = Quantity.of(0.567, "m");
    FlowsInterface flowsInterface = new TwdDuckieFlows(ms, sa);
    for (int res = 3; res <= 8; ++res) {
      Collection<Flow> controls = flowsInterface.getFlows(res);
      Set<Tensor> set = new HashSet<>();
      for (Flow flow : controls) {
        Tensor key = flow.getU().map(Round._3);
        assertTrue(set.add(key));
      }
    }
  }

  public void testSize() throws ClassNotFoundException, IOException {
    FlowsInterface flowsInterface = Serialization.copy(new TwdDuckieFlows(RealScalar.of(3), RealScalar.of(0.567)));
    assertEquals(flowsInterface.getFlows(5).size(), 20);
    assertEquals(flowsInterface.getFlows(7).size(), 28);
    assertEquals(flowsInterface.getFlows(8).size(), 32);
  }
}
