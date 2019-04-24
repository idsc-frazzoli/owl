// code by jph
package ch.ethz.idsc.owl.bot.se2.twd;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;
import junit.framework.TestCase;

public class TwdDuckieFlowsTest extends TestCase {
  public void testRadNoDuplicates() {
    Scalar ms = Quantity.of(3, "m*s^-1");
    Scalar sa = Quantity.of(0.567, "m*rad^-1");
    TwdDuckieFlows twdConfig = new TwdDuckieFlows(ms, sa);
    for (int res = 3; res <= 8; ++res) {
      Collection<Flow> controls = twdConfig.getFlows(res);
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
    TwdDuckieFlows twdConfig = new TwdDuckieFlows(ms, sa);
    for (int res = 3; res <= 8; ++res) {
      Collection<Flow> controls = twdConfig.getFlows(res);
      Set<Tensor> set = new HashSet<>();
      for (Flow flow : controls) {
        Tensor key = flow.getU().map(Round._3);
        assertTrue(set.add(key));
      }
    }
  }

  public void testSize() {
    TwdDuckieFlows twdConfig = new TwdDuckieFlows(RealScalar.of(3), RealScalar.of(0.567));
    assertEquals(twdConfig.getFlows(5).size(), 20);
    assertEquals(twdConfig.getFlows(7).size(), 28);
    assertEquals(twdConfig.getFlows(8).size(), 32);
  }
}
