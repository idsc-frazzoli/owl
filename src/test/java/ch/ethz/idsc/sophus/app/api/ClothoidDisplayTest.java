// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.crv.clothoid.Se2Clothoids;
import ch.ethz.idsc.sophus.hs.r2.Se2ParametricDistance;
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
    Scalar scalar = Se2ClothoidDisplay.INSTANCE.parametricDistance(p, q);
    Clips.interval(2.545, 2.55).requireInside(scalar);
    Scalar result = Se2ParametricDistance.INSTANCE.distance(p, q);
    assertEquals(result, RealScalar.of(2));
  }

  public void testInstance() {
    assertEquals(Se2ClothoidDisplay.INSTANCE.geodesicInterface(), Se2Clothoids.INSTANCE);
  }
}
