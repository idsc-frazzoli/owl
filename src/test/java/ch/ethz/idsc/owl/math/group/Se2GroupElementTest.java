// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class Se2GroupElementTest extends TestCase {
  public void testSimple() {
    Se2GroupElement element = Se2Group.INSTANCE.element(Tensors.vector(1, 2, 3));
    Tensor tensor = element.combine(Tensors.vector(6, 7, 8));
    assertTrue(Sign.isNegative(tensor.Get(2)));
  }

  public void testInverse() {
    Se2GroupElement element = Se2Group.INSTANCE.element(Tensors.vector(1, 2, 3));
    assertTrue(element.inverse() instanceof Se2GroupElement);
  }
}
