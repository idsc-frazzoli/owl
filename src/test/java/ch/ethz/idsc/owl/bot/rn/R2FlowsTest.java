// code by jph
package ch.ethz.idsc.owl.bot.rn;

import java.util.Collection;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.opt.ConvexHull;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class R2FlowsTest extends TestCase {
  public void testSimple() {
    int n = 100;
    R2Flows r2Config = new R2Flows(RealScalar.ONE);
    Collection<Flow> flows = r2Config.getFlows(n);
    assertEquals(flows.size(), n);
    Tensor tflow = Tensor.of(flows.stream().map(Flow::getU));
    Tensor hul = ConvexHull.of(tflow);
    assertEquals(Dimensions.of(tflow), Dimensions.of(hul));
  }

  public void testMaxSpeed() {
    int n = 10;
    R2Flows r2Config = new R2Flows(RealScalar.ONE);
    Collection<Flow> controls = r2Config.getFlows(n);
    Scalar maxSpeed = RnControls.maxSpeed(controls);
    assertTrue(Chop._14.close(maxSpeed, RealScalar.ONE));
  }

  public void testFail() {
    R2Flows r2Config = new R2Flows(RealScalar.ONE);
    try {
      r2Config.getFlows(2);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
