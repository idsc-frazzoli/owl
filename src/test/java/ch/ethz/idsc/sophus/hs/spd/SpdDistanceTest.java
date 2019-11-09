// code by jph
package ch.ethz.idsc.sophus.hs.spd;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.Symmetrize;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class SpdDistanceTest extends TestCase {
  public void testSimple() {
    for (int n = 1; n < 6; ++n) {
      Tensor g = TestHelper.generateSpd(n);
      Scalar dP = SpdDistance.n(g);
      Tensor matrix = Inverse.of(g);
      Scalar dN = SpdDistance.n(Symmetrize.of(matrix));
      Chop._06.requireClose(dP, dN);
    }
  }

  public void testSwap() {
    for (int n = 1; n < 6; ++n) {
      Tensor p = TestHelper.generateSpd(n);
      Tensor q = TestHelper.generateSpd(n);
      Scalar pq = SpdDistance.INSTANCE.distance(p, q);
      Scalar qp = SpdDistance.INSTANCE.distance(q, p);
      Chop._05.requireClose(pq, qp);
    }
  }
}
