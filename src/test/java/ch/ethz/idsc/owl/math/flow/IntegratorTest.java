// code by jph
package ch.ethz.idsc.owl.math.flow;

import ch.ethz.idsc.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class IntegratorTest extends TestCase {
  public void testSimple() {
    StateSpaceModel stateSpaceModel = SingleIntegratorStateSpaceModel.INSTANCE;
    Tensor u = Tensors.vector(1, 2);
    Tensor x = Tensors.vector(7, 2);
    Scalar h = RealScalar.of(3);
    Tensor euler_x1 = EulerIntegrator.INSTANCE.step(stateSpaceModel, x, u, h);
    Tensor mid_x1 = MidpointIntegrator.INSTANCE.step(stateSpaceModel, x, u, h);
    Tensor rk4_x1 = RungeKutta4Integrator.INSTANCE.step(stateSpaceModel, x, u, h);
    Tensor rk45_x1 = RungeKutta45Integrator.INSTANCE.step(stateSpaceModel, x, u, h);
    assertEquals(euler_x1, x.add(u.multiply(h)));
    assertEquals(euler_x1, mid_x1);
    for (int n = 1; n < 10; ++n) {
      Tensor mmi_x1 = ModifiedMidpointIntegrator.of(n).step(stateSpaceModel, x, u, h);
      assertEquals(euler_x1, mmi_x1);
    }
    assertEquals(euler_x1, rk4_x1);
    assertEquals(euler_x1, rk45_x1);
    // ---
    ExactTensorQ.require(euler_x1);
    ExactTensorQ.require(mid_x1);
    ExactTensorQ.require(rk4_x1);
    ExactTensorQ.require(rk45_x1);
  }
}
