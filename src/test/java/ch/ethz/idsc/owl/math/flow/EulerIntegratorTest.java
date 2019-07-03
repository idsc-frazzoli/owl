// code by jph
package ch.ethz.idsc.owl.math.flow;

import ch.ethz.idsc.owl.math.DoubleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityTensor;
import ch.ethz.idsc.tensor.qty.Unit;
import junit.framework.TestCase;

public class EulerIntegratorTest extends TestCase {
  public void testSimple() {
    StateSpaceModel stateSpaceModel = SingleIntegratorStateSpaceModel.INSTANCE;
    Flow flow = StateSpaceModels.createFlow( //
        stateSpaceModel, QuantityTensor.of(Tensors.vector(1, 2), Unit.of("m*s^-1")));
    Tensor x = QuantityTensor.of(Tensors.vector(1, 2), Unit.of("m"));
    Tensor r = EulerIntegrator.INSTANCE.step(flow, x, Quantity.of(2, "s"));
    assertEquals(r, Tensors.fromString("{3[m], 6[m]}"));
  }

  public void testDouble() {
    StateSpaceModel stateSpaceModel = DoubleIntegratorStateSpaceModel.INSTANCE;
    Flow flow = StateSpaceModels.createFlow( // acceleration
        stateSpaceModel, QuantityTensor.of(Tensors.vector(1, 2), Unit.of("m*s^-2")));
    Tensor x = Tensors.fromString("{2[m], 3[m], 4[m*s^-1], 5[m*s^-1]}"); // pos and vel
    Tensor r = EulerIntegrator.INSTANCE.step(flow, x, Quantity.of(2, "s"));
    assertEquals(r, Tensors.fromString("{10[m], 13[m], 6[m*s^-1], 9[m*s^-1]}"));
  }
}
