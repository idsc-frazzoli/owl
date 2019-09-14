// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.sophus.crv.clothoid.Clothoid;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class LimitedClothoidNdCenterTest extends TestCase {
  public void testSimple() {
    Tensor center = Tensors.vector(2, 1, 0);
    LimitedClothoidNdCenter limitedClothoidNdCenter = //
        new LimitedClothoidNdCenter(center, Clips.absolute(1)) {
          @Override
          protected Clothoid clothoid(Tensor other) {
            return new Clothoid(other, center);
          }
        };
    Scalar scalar = limitedClothoidNdCenter.ofVector(Tensors.vector(0, 1, 0));
    assertEquals(scalar, RealScalar.of(2));
  }
}
