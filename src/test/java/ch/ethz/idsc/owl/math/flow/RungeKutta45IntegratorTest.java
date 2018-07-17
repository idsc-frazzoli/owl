// code by jph
package ch.ethz.idsc.owl.math.flow;

import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.map.Se2Integrator;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitSystem;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class RungeKutta45IntegratorTest extends TestCase {
  public void testSe2() {
    StateSpaceModel stateSpaceModel = Se2StateSpaceModel.INSTANCE;
    Flow flow = StateSpaceModels.createFlow( //
        stateSpaceModel, Tensors.fromString("{1[m*s^-1],0,2[rad*s^-1]}").map(UnitSystem.SI()));
    Tensor x = Tensors.fromString("{1[m],2[m],3[rad]}").map(UnitSystem.SI());
    Tensor r = EulerIntegrator.INSTANCE.step(flow, x, Quantity.of(2, "s"));
    assertTrue(Chop._10.close(r, //
        Tensors.fromString("{-0.9799849932008908[m], 2.2822400161197343[m], 7}")));
  }

  public void testSe2Exact() {
    StateSpaceModel stateSpaceModel = Se2StateSpaceModel.INSTANCE;
    Flow flow = StateSpaceModels.createFlow( //
        stateSpaceModel, Tensors.fromString("{1[m*s^-1],0,2[rad*s^-1]}").map(UnitSystem.SI()));
    Tensor x = Tensors.fromString("{1[m],2[m],3[rad]}").map(UnitSystem.SI());
    Tensor r = Se2Integrator.INSTANCE.step(flow, x, Quantity.of(2, "s"));
    assertTrue(Chop._10.close(r, //
        Tensors.fromString("{1.2579332953294609[m], 1.128052624528125[m], 7}")));
  }

  public void testSe2Rk() {
    StateSpaceModel stateSpaceModel = Se2StateSpaceModel.INSTANCE;
    Flow flow = StateSpaceModels.createFlow( //
        stateSpaceModel, Tensors.fromString("{1[m*s^-1],0,2[rad*s^-1]}").map(UnitSystem.SI()));
    Tensor x = Tensors.fromString("{1[m],2[m],3[rad]}").map(UnitSystem.SI());
    Tensor r = RungeKutta45Integrator.INSTANCE.step(flow, x, Quantity.of(2, "s"));
    assertTrue(Chop._10.close(r, //
        Tensors.fromString("{1.2568926185541083[m], 1.1315706479838576[m], 7}")));
  }
}
