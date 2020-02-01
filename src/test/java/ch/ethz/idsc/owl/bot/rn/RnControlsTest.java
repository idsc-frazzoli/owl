// code by jph
package ch.ethz.idsc.owl.bot.rn;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.r2.R2Flows;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class RnControlsTest extends TestCase {
  public void testMaxSpeed() {
    int n = 10;
    R2Flows r2Flows = new R2Flows(Quantity.of(3, "m*s^-1"));
    Collection<Tensor> controls = r2Flows.getFlows(n);
    Scalar maxSpeed = RnControls.maxSpeed(controls);
    Chop._14.requireClose(maxSpeed, Quantity.of(3, "m*s^-1"));
  }
}
