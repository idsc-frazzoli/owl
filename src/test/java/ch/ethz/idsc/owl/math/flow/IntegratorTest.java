// code by jph
package ch.ethz.idsc.owl.math.flow;

import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class IntegratorTest extends TestCase {
  public void testSimple() {
    StateSpaceModel ssm = SingleIntegratorStateSpaceModel.INSTANCE;
    Tensor u = Tensors.vector(1, 2);
    Flow flow = StateSpaceModels.createFlow(ssm, u);
    Tensor x = Tensors.vector(7, 2);
    Scalar h = RealScalar.of(3);
    Tensor euler_x1 = EulerIntegrator.INSTANCE.step(flow, x, h);
    Tensor mid_x1 = MidpointIntegrator.INSTANCE.step(flow, x, h);
    Tensor rk4_x1 = RungeKutta4Integrator.INSTANCE.step(flow, x, h);
    Tensor rk45_x1 = RungeKutta45Integrator.INSTANCE.step(flow, x, h);
    assertEquals(euler_x1, x.add(u.multiply(h)));
    assertEquals(euler_x1, mid_x1);
    assertEquals(euler_x1, rk4_x1);
    assertEquals(euler_x1, rk45_x1);
    // ---
    ExactTensorQ.require(euler_x1);
    ExactTensorQ.require(mid_x1);
    ExactTensorQ.require(rk4_x1);
    ExactTensorQ.require(rk45_x1);
  }
}
