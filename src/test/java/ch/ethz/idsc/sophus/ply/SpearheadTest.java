// code by jph
package ch.ethz.idsc.sophus.ply;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class SpearheadTest extends TestCase {
  public void testSimple() {
    Tensor polygon = Spearhead.of(Tensors.vector(-0.806, -0.250, -0.524), RealScalar.of(0.1));
    Sign.requirePositive(PolygonArea.of(polygon));
  }
}
