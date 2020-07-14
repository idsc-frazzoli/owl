// code by jph
package ch.ethz.idsc.owl.math.flow;

import ch.ethz.idsc.owl.math.model.DoubleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.model.StateSpaceModel;
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
  private final Integrator integrator = EulerLieIntegrator.of(RnGroup.INSTANCE, RnExponential.INSTANCE);

  public void testSimple() {
    StateSpaceModel stateSpaceModel = SingleIntegratorStateSpaceModel.INSTANCE;
    Tensor u = QuantityTensor.of(Tensors.vector(1, 2), Unit.of("m*s^-1"));
    Tensor x = QuantityTensor.of(Tensors.vector(1, 2), Unit.of("m"));
    Scalar h = Quantity.of(2, "s");
    Tensor r = EulerIntegrator.INSTANCE.step(stateSpaceModel, x, u, h);
    assertEquals(r, Tensors.fromString("{3[m], 6[m]}"));
    assertEquals(r, integrator.step(stateSpaceModel, x, u, h));
  }

  public void testDouble() {
    StateSpaceModel stateSpaceModel = DoubleIntegratorStateSpaceModel.INSTANCE;
    Tensor u = QuantityTensor.of(Tensors.vector(1, 2), Unit.of("m*s^-2"));
    Tensor x = Tensors.fromString("{2[m], 3[m], 4[m*s^-1], 5[m*s^-1]}"); // pos and vel
    Tensor r = EulerIntegrator.INSTANCE.step(stateSpaceModel, x, u, Quantity.of(2, "s"));
    assertEquals(r, Tensors.fromString("{10[m], 13[m], 6[m*s^-1], 9[m*s^-1]}"));
  }
}
