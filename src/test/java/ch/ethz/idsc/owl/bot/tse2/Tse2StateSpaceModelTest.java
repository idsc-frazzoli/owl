// code by jph
package ch.ethz.idsc.owl.bot.tse2;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Tse2StateSpaceModelTest extends TestCase {
  public void testSimple() {
    Tensor tensor = Tse2StateSpaceModel.INSTANCE.f(Tensors.vector(1, 2, Math.PI, 5), Tensors.vector(3, 4));
    Chop._13.requireClose(tensor, Tensors.vector(-5, 0, 5 * 3, 4));
  }

  public void testQuantity() {
    FlowsInterface flowsInterface = //
        Tse2CarFlows.of(Quantity.of(1, "m^-1"), Tensors.of(Quantity.of(-2, "m*s^-2"), Quantity.of(0, "m*s^-2"), Quantity.of(2, "m*s^-2")));
    Collection<Tensor> collection = flowsInterface.getFlows(3);
    for (Tensor flow : collection) {
      Tensor x = Tensors.fromString("{2[m], 3[m], 4, 3[m*s^-1]}").unmodifiable();
      Tensor u = flow.unmodifiable();
      Tensor f = Tse2StateSpaceModel.INSTANCE.f(x, u).unmodifiable();
      Scalar h = Quantity.of(1, "s");
      Tensor xp = x.add(f.multiply(h));
      Tensor xn = EulerIntegrator.INSTANCE.step(Tse2StateSpaceModel.INSTANCE, x, u, h);
      assertEquals(xp, xn);
    }
  }
}
