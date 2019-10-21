// code by jph
package ch.ethz.idsc.owl.math.flow;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class ModifiedMidpointIntegratorTest extends TestCase {
  public void testSimple() {
    for (int n = 1; n < 10; ++n) {
      Integrator integrator = ModifiedMidpointIntegrator.of(n);
      Flow flow = new Flow() {
        @Override
        public Tensor getU() {
          return Tensors.vector(1);
        }

        @Override
        public Tensor at(Tensor x) {
          return Tensors.vector(1);
        }
      };
      Tensor tensor = integrator.step(flow, Tensors.vector(0), RealScalar.of(1));
      assertEquals(tensor, Tensors.vector(1));
      ExactTensorQ.require(tensor);
    }
  }
}
