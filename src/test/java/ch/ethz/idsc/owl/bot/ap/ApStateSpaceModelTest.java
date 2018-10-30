//code by andre
package ch.ethz.idsc.owl.bot.ap;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class ApStateSpaceModelTest extends TestCase {
  public void testSimple() {
    Tensor f = ApStateSpaceModel.INSTANCE.f(Tensors.vector(81,-2,55), Tensors.vector(0,0));
    System.out.println(f);
    assertTrue(Chop._10.close(f, Tensors.vector(162,123,-73)));
  }
}
