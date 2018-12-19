// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se3NumericsTest extends TestCase {
  public void testSimple() {
    Se3Numerics se3Numerics1 = new Se3Numerics(RealScalar.of(1e-2));
    Se3Numerics se3Numerics2 = new Se3Numerics(RealScalar.of(0.9e-2));
    Tensor diff = se3Numerics1.vector().subtract(se3Numerics2.vector());
    assertTrue(Scalars.nonZero(diff.Get(0)));
    assertTrue(Chop._05.allZero(diff.extract(1, 5)));
  }
}
