// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.bot.se2.glc.Se2CarFlows;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2StateSpaceModelTest extends TestCase {
  public void testQuantity() {
    Tensor x = Tensors.fromString("{-1[m], -2[m], 3}");
    Scalar h = Quantity.of(1, "s");
    Flow flow = Se2CarFlows.singleton(Quantity.of(2, "m*s^-1"), Quantity.of(-1, "m^-1"));
    // Se2StateSpaceModel.INSTANCE.f(x, flow.getU());
    Tensor expl = Se2CarIntegrator.INSTANCE.step(Se2StateSpaceModel.INSTANCE, x, flow.getU(), h);
    Tensor impl = RungeKutta45Integrator.INSTANCE.step(Se2StateSpaceModel.INSTANCE, x, flow.getU(), h);
    Chop._04.requireClose(expl, impl);
  }
}
