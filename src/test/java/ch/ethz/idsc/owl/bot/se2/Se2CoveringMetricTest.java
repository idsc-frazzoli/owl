// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.math.group.Se2CoveringGeodesic;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2CoveringMetricTest extends TestCase {
  public void testPlanar() {
    Scalar scalar = Se2CoveringMetric.INSTANCE.distance(Tensors.vector(1, 1, 0), Tensors.vector(4, 5, 0));
    assertEquals(scalar, RealScalar.of(5));
  }

  public void testTurn() {
    Scalar scalar = Se2CoveringMetric.INSTANCE.distance(Tensors.vector(1, 2, 3), Tensors.vector(1, 2, 7));
    assertTrue(Chop._14.close(scalar, RealScalar.of(4)));
  }

  public void testMiddle() {
    Tensor p = Tensors.vector(1, .7, 2);
    Tensor q = Tensors.vector(2, .3, 3.3);
    Scalar dq = Se2CoveringMetric.INSTANCE.distance(p, q);
    Tensor m = Se2CoveringGeodesic.INSTANCE.split(p, q, RationalScalar.HALF);
    Scalar dm = Se2CoveringMetric.INSTANCE.distance(p, m);
    assertTrue(Chop._14.close(dq.divide(dm), RealScalar.of(2)));
  }
}
