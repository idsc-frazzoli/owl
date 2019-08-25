// code by jph
package ch.ethz.idsc.sophus.lie.gl;

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
    Chop._10.requireClose(tensor, IdentityMatrix.of(2));
  }
}
