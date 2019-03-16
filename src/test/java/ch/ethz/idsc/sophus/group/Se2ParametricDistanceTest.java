// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2ParametricDistanceTest extends TestCase {
  public void testSimple() {
    Scalar scalar = Se2ParametricDistance.of(Tensors.vector(1, 2, 3), Tensors.vector(1 + 3, 2 + 4, 3));
    assertEquals(scalar, RealScalar.of(5));
  }

  public void testHalf() {
    Scalar scalar = Se2ParametricDistance.of(Tensors.vector(1, 2, 3), Tensors.vector(1, 2 + 2 * 3, 3 + Math.PI));
    Chop._14.requireClose(scalar, RealScalar.of(3 * Math.PI));
  }
}
