// code by jph
package ch.ethz.idsc.owl.bot.tse2;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Tse2StateSpaceModelTest extends TestCase {
  public void testSimple() {
    Tensor tensor = Tse2StateSpaceModel.INSTANCE.f(Tensors.vector(1, 2, Math.PI, 5), Tensors.vector(3, 4));
    assertTrue(Chop._13.close(tensor, Tensors.vector(-5, 0, 5 * 3, 4)));
  }

  public void testQuantity() {
    FlowsInterface flowsInterface = //
        Tse2CarFlows.of(Quantity.of(1, "m^-1"), Tensors.of(Quantity.of(2, "m*s^-2")));
    Collection<Flow> collection = flowsInterface.getFlows(3);
    for (Flow flow : collection) {
      Tensor x = Tensors.fromString("{2[m],3[m],4,3[m*s^-1]}");
      Tensor u = flow.getU();
      Tensor f = Tse2StateSpaceModel.INSTANCE.f(x, u);
      Tensor dx = f.multiply(Quantity.of(1, "s"));
      dx.copy(); // prevent warning
      // System.out.println(dx);
    }
  }
}
