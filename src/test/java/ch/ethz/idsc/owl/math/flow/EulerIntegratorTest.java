// code by jph
package ch.ethz.idsc.owl.math.flow;

import ch.ethz.idsc.owl.math.model.DoubleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.owl.math.model.StateSpaceModels;
import ch.ethz.idsc.sophus.integ.EulerLieIntegrator;
import ch.ethz.idsc.sophus.lie.rn.RnExponential;
import ch.ethz.idsc.sophus.lie.rn.RnGroup;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityTensor;
import ch.ethz.idsc.tensor.qty.Unit;
import junit.framework.TestCase;

public class EulerIntegratorTest extends TestCase {
  private final Integrator lieEulerIntegrator = EulerLieIntegrator.of(RnGroup.INSTANCE, RnExponential.INSTANCE);

  public void testSimple() {
    StateSpaceModel stateSpaceModel = SingleIntegratorStateSpaceModel.INSTANCE;
    Flow flow = StateSpaceModels.createFlow( //
        stateSpaceModel, QuantityTensor.of(Tensors.vector(1, 2), Unit.of("m*s^-1")));
    Tensor x = QuantityTensor.of(Tensors.vector(1, 2), Unit.of("m"));
    Scalar h = Quantity.of(2, "s");
    Tensor r = EulerIntegrator.INSTANCE.step(flow, x, h);
    assertEquals(r, Tensors.fromString("{3[m], 6[m]}"));
    assertEquals(r, lieEulerIntegrator.step(flow, x, h));
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
