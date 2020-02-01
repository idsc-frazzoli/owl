// code by jph
package ch.ethz.idsc.owl.math.flow;

import ch.ethz.idsc.owl.math.model.DoubleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityTensor;
import ch.ethz.idsc.tensor.qty.Unit;
import junit.framework.TestCase;

public class MidpointIntegratorTest extends TestCase {
  public void testSimple() {
    StateSpaceModel stateSpaceModel = SingleIntegratorStateSpaceModel.INSTANCE;
    Tensor u = QuantityTensor.of(Tensors.vector(1, 2), Unit.of("m*s^-1"));
    Tensor x = QuantityTensor.of(Tensors.vector(1, 2), Unit.of("m"));
    Tensor r = MidpointIntegrator.INSTANCE.step(stateSpaceModel, x, u, Quantity.of(2, "s"));
    assertEquals(r, Tensors.fromString("{3[m], 6[m]}"));
  }

  public void testDouble() {
    StateSpaceModel stateSpaceModel = DoubleIntegratorStateSpaceModel.INSTANCE;
    Tensor u = QuantityTensor.of(Tensors.vector(1, 2), Unit.of("m*s^-2"));
    Tensor x = Tensors.fromString("{2[m], 3[m], 4[m*s^-1], 5[m*s^-1]}"); // pos and vel
    Tensor r = MidpointIntegrator.INSTANCE.step(stateSpaceModel, x, u, Quantity.of(2, "s"));
    assertEquals(r, Tensors.fromString("{12[m], 17[m], 6[m*s^-1], 9[m*s^-1]}"));
  }
}
