// code by jph
package ch.ethz.idsc.owl.bot.se2.tse2;

import ch.ethz.idsc.owl.bot.tse2.Tse2StateSpaceModel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Tse2StateSpaceModelTest extends TestCase {
  public void testSimple() {
    Tensor tensor = Tse2StateSpaceModel.INSTANCE.f(Tensors.vector(1, 2, Math.PI, 5), Tensors.vector(3, 4));
    assertTrue(Chop._13.close(tensor, Tensors.vector(-5, 0, 5 * 3, 4)));
  }
}
