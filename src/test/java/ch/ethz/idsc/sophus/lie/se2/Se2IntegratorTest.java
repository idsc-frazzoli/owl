// code by jph
package ch.ethz.idsc.sophus.lie.se2;

import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2IntegratorTest extends TestCase {
  public void testSimple() {
    Tensor tensor = Se2Integrator.INSTANCE.spin(Tensors.vector(1, 2, 3), Tensors.vector(1, 2, 1));
    Chop._10.requireClose(So2.MOD.apply(RealScalar.of(4)), tensor.get(2));
  }
}
