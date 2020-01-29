// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class SingleIntegratorStateSpaceModelTest extends TestCase {
  public void testSimple() {
    Tensor u = Tensors.vector(1, 2, 3);
    Tensor r = SingleIntegratorStateSpaceModel.INSTANCE.f(null, u);
    assertEquals(u, r);
  }
}
