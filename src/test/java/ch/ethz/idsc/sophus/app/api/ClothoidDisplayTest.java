// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.crv.clothoid.Clothoid1;
import ch.ethz.idsc.sophus.lie.se2.Se2ParametricDistance;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class ClothoidDisplayTest extends TestCase {
  public void testSimple() {
    // 1 2.5180768787131558
    // 2 2.5597567801548426
    // 3 2.5640965868005288
    // 4 2.564420707620397
    Tensor p = Tensors.vector(0, 0, 0);
    Tensor q = Tensors.vector(0, 2, 0);
    Scalar scalar = ClothoidDisplay.INSTANCE.parametricDistance(p, q);
    Scalar result = Se2ParametricDistance.INSTANCE.distance(p, q);
    Clips.interval(2.56, 2.57).requireInside(scalar);
    assertEquals(result, RealScalar.of(2));
  }

  public void testInstance() {
    assertEquals(ClothoidDisplay.INSTANCE.geodesicInterface(), Clothoid1.INSTANCE);
  }
}
