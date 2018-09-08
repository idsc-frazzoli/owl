// code by jph
package ch.ethz.idsc.owl.bot.tse2;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class Tse2IntegratorTest extends TestCase {
  public void testQuantity() {
    FlowsInterface flowsInterface = //
        Tse2CarFlows.of(Quantity.of(1, "m^-1"), Tensors.of(Quantity.of(-2, "m*s^-2"), Quantity.of(0, "m*s^-2"), Quantity.of(2, "m*s^-2")));
    Collection<Flow> collection = flowsInterface.getFlows(3);
    for (Flow flow : collection) {
      Tensor x = Tensors.fromString("{2[m],3[m],4,3[m*s^-1]}").unmodifiable();
      Tensor u = flow.getU().unmodifiable();
      Tensor f = Tse2StateSpaceModel.INSTANCE.f(x, u).unmodifiable();
      Scalar h = Quantity.of(1, "s");
      Tensor xp = x.add(f.multiply(h));
      Tensor xn = EulerIntegrator.INSTANCE.step(flow, x, h);
      assertEquals(xp, xn);
      Tensor xr = RungeKutta45Integrator.INSTANCE.step(flow, x, h);
      Tensor xt = Tse2Integrator.INSTANCE.step(flow, x, h);
      Tensor xd = xn.subtract(xt);
      System.out.println(xd);
      // assertTrue(Chop._04.close(xr, xt));
    }
  }
}
