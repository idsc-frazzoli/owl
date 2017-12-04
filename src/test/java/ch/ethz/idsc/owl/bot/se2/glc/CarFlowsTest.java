// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.Collection;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitSystem;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class CarFlowsTest extends TestCase {
  public void testUnits() {
    Scalar speed = Quantity.of(2, "m*s^-1");
    Scalar rate_max = (Scalar) Quantity.of(1, "rad*m^-1").map(UnitSystem.SI());
    CarFlows carFlows = new CarStandardFlows(speed, rate_max);
    Collection<Flow> collection = carFlows.getFlows(8);
    Flow flow = collection.iterator().next();
    Tensor x = Tensors.fromString("{1[m],2[m],3[rad]}").map(UnitSystem.SI());
    Tensor r = RungeKutta45Integrator.INSTANCE.step(flow, x, Quantity.of(2, "s"));
    assertTrue(Chop._10.close(r, //
        Tensors.fromString("{1.9786265584792444[m], 3.5241205617280174[m], -1}")));
  }
}
