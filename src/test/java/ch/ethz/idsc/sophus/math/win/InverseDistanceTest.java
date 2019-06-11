// code by jph
package ch.ethz.idsc.sophus.math.win;

import ch.ethz.idsc.tensor.red.Norm2Squared;
import junit.framework.TestCase;

public class InverseDistanceTest extends TestCase {
  public void testSimple() {
    InverseDistance inverseDistance = new InverseDistance(Norm2Squared::between);
    // Scalar scalar = (Scalar) shepardInterpolation.at( //
    // Range.of(0, 10).map(Tensors::of), Range.of(0, 10), Tensors.vector(5.4));
    // Chop._12.requireClose(scalar, RealScalar.of(5.268238109178673));
  }
}
