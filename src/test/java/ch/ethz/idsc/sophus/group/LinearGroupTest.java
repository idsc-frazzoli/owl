// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class LinearGroupTest extends TestCase {
  public void testSimple() {
    LinearGroupElement element = LinearGroup.INSTANCE.element(RotationMatrix.of(RealScalar.of(.24)));
    Tensor tensor = element.inverse().combine(RotationMatrix.of(RealScalar.of(.24)));
    assertTrue(Chop._10.close(tensor, IdentityMatrix.of(2)));
  }
}
