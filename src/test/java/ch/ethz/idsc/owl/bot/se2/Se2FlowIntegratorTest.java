// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.owl.math.model.StateSpaceModels;
import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitSystem;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2FlowIntegratorTest extends TestCase {
  public void testSe2Exact() {
    StateSpaceModel stateSpaceModel = Se2StateSpaceModel.INSTANCE;
    Flow flow = StateSpaceModels.createFlow( //
        stateSpaceModel, Tensors.fromString("{1[m*s^-1], 0, 2[rad*s^-1]}").map(UnitSystem.SI()));
    Tensor x = Tensors.fromString("{1[m], 2[m], 3[rad]}").map(UnitSystem.SI());
    Tensor r = Se2FlowIntegrator.INSTANCE.step(Se2StateSpaceModel.INSTANCE, x, flow.getU(), Quantity.of(2, "s"));
    assertTrue(Chop._10.close(r, //
        Tensors.fromString("{1.2579332953294609[m], 1.128052624528125[m], " + So2.MOD.apply(RealScalar.of(7)) + "}")));
  }
}
