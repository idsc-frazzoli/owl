// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se3ExponentialTest extends TestCase {
  public void testSimple() {
    Tensor input = Tensors.of( //
        Tensors.vector(1, 2, 3), //
        Tensors.vector(.2, .3, -.1));
    Tensor g = Se3Exponential.INSTANCE.exp(input);
    Tensor u_w = Se3Exponential.INSTANCE.log(g);
    assertTrue(Chop._12.close(input, u_w));
  }
}
