// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.bot.se2.glc.CarHelper;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2StateSpaceModelTest extends TestCase {
  public void testSimple() {
    StateSpaceModel stateSpaceModel = Se2StateSpaceModel.INSTANCE;
    assertEquals(stateSpaceModel.getLipschitz(), RealScalar.ONE);
  }

  public void testQuantity() {
    Tensor x = Tensors.fromString("{-1[m], -2[m], 3}");
    Scalar h = Quantity.of(1, "s");
    Flow flow = CarHelper.singleton(Quantity.of(2, "m*s^-1"), Quantity.of(-1, "m^-1"));
    Se2StateSpaceModel.INSTANCE.f(x, flow.getU());
    Tensor expl = Se2CarIntegrator.INSTANCE.step(flow, x, h);
    Tensor impl = RungeKutta45Integrator.INSTANCE.step(flow, x, h);
    Chop._04.requireClose(expl, impl);
  }
}
