// code by jph
package ch.ethz.idsc.sophus.lie.se2;

import java.io.IOException;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.owl.math.flow.LieEulerIntegrator;
import ch.ethz.idsc.owl.math.flow.LieMidpointIntegrator;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2IntegratorTest extends TestCase {
  public void testSimple() {
    Tensor tensor = Se2Integrator.INSTANCE.spin(Tensors.vector(1, 2, 3), Tensors.vector(1, 2, 1));
    Chop._10.requireClose(So2.MOD.apply(RealScalar.of(4)), tensor.get(2));
  }

  public void testMatch() throws ClassNotFoundException, IOException {
    Tensor x0 = Tensors.vector(1, 2, 3).unmodifiable();
    Tensor tangent = Tensors.vector(1, 2, 1).unmodifiable();
    Flow flow = new Flow() {
      @Override
      public Tensor getU() {
        return null;
      }

      @Override
      public Tensor at(Tensor x) {
        return tangent;
      }
    };
    Scalar h = RealScalar.ONE;
    Tensor tensor = Se2Integrator.INSTANCE.spin(x0, tangent);
    {
      Integrator integrator = //
          Serialization.copy(LieEulerIntegrator.of(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE));
      Tensor result = integrator.step(flow, x0, h);
      Chop._12.requireClose(tensor, result);
    }
    {
      Integrator integrator = //
          Serialization.copy(LieMidpointIntegrator.of(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE));
      Tensor result = integrator.step(flow, x0, h);
      Chop._12.requireClose(tensor, result);
    }
  }
}
