// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.sophus.group.Se2CoveringIntegrator;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitSystem;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2CarFlowsTest extends TestCase {
  public void testUnits() {
    Scalar speed = Quantity.of(2, "m*s^-1");
    Scalar rate_max = (Scalar) Quantity.of(1, "rad*m^-1").map(UnitSystem.SI());
    FlowsInterface carFlows = Se2CarFlows.standard(speed, rate_max);
    Collection<Flow> collection = carFlows.getFlows(8);
    Flow flow = collection.iterator().next();
    Tensor x = Tensors.fromString("{1[m],2[m],3[rad]}").map(UnitSystem.SI());
    Tensor r = RungeKutta45Integrator.INSTANCE.step(flow, x, Quantity.of(2, "s"));
    Chop._10.requireClose(r, //
        Tensors.fromString("{1.9786265584792444[m], 3.5241205617280174[m], -1}"));
  }

  public void testRadius() {
    Scalar speed = Quantity.of(1.423, "m*s^-1");
    Scalar rate = (Scalar) Quantity.of(2.384, "rad*m^-1").map(UnitSystem.SI());
    Flow flow = CarHelper.singleton(speed, rate);
    Tensor u = flow.getU();
    Tensor origin = Tensors.fromString("{0[m],0[m],0}");
    Scalar half_turn = DoubleScalar.of(Math.PI).divide(u.Get(2));
    Tensor res = Se2CoveringIntegrator.INSTANCE.spin(origin, u.multiply(half_turn));
    res = res.map(Chop._12);
    Scalar radius = res.Get(1).divide(RealScalar.of(2));
    Chop._12.requireClose(radius.reciprocal(), rate);
  }
}
