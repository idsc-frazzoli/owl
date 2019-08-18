// code by jph
package ch.ethz.idsc.owl.bot.tse2;

import java.io.IOException;
import java.util.Collection;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class Tse2IntegratorTest extends TestCase {
  public void testQuantity() throws ClassNotFoundException, IOException {
    FlowsInterface flowsInterface = //
        Tse2CarFlows.of(Quantity.of(1, "m^-1"), Tensors.of(Quantity.of(-2, "m*s^-2"), Quantity.of(0, "m*s^-2"), Quantity.of(2, "m*s^-2")));
    Collection<Flow> collection = flowsInterface.getFlows(3);
    Tse2Integrator tse2Integrator = Serialization.copy(new Tse2Integrator(Clips.interval(Quantity.of(-20, "m*s^-1"), Quantity.of(20, "m*s^-1"))));
    for (Flow flow : collection) {
      Tensor x = Tensors.fromString("{2[m], 3[m], 4, 3[m*s^-1]}").unmodifiable();
      Tensor u = flow.getU().unmodifiable();
      Tensor f = Tse2StateSpaceModel.INSTANCE.f(x, u).unmodifiable();
      Scalar h = Quantity.of(0.1, "s");
      Tensor xp = x.add(f.multiply(h));
      Tensor xn = EulerIntegrator.INSTANCE.step(flow, x, h);
      assertEquals(xp, xn);
      Tensor xr = RungeKutta45Integrator.INSTANCE.step(flow, x, h);
      Tensor xt = tse2Integrator.step(flow, x, h);
      Tensor xd = xr.subtract(xt);
      assertTrue(Chop._04.allZero(xd));
    }
  }
}
