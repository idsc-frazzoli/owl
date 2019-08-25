// code by jph
package ch.ethz.idsc.owl.math.flow;

import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitSystem;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class RungeKutta4IntegratorTest extends TestCase {
  public void testSe2Rk() {
    StateSpaceModel stateSpaceModel = Se2StateSpaceModel.INSTANCE;
    Flow flow = StateSpaceModels.createFlow( //
        stateSpaceModel, Tensors.fromString("{1[m*s^-1], 0, 2[rad*s^-1]}").map(UnitSystem.SI()));
    Tensor x = Tensors.fromString("{1[m], 2[m], 3[rad]}").map(UnitSystem.SI());
    Tensor r = RungeKutta4Integrator.INSTANCE.step(flow, x, Quantity.of(2, "s"));
    Chop._10.requireClose(r, Tensors.fromString("{1.2995194998652546[m], 0.9874698360420342[m], 7}"));
  }
}
