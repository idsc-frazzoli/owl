// code by ob
package ch.ethz.idsc.sophus.hs.h2;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.ArcSinh;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Log;
import junit.framework.TestCase;

public class H2ParametricDistanceTest extends TestCase {
  public void testTrivial() {
    Tensor p = Tensors.vector(-Math.random(), Math.random());
    Scalar actual = H2ParametricDistance.INSTANCE.distance(p, p);
    assertEquals(RealScalar.ZERO, actual);
  }

  public void testYAxis() {
    Tensor p = Tensors.vector(2, 1 + Math.random());
    Tensor q = Tensors.vector(2, 7 + Math.random());
    // ---
    Scalar actual = H2ParametricDistance.INSTANCE.distance(p, q);
    Scalar expected = Abs.FUNCTION.apply(Log.of(q.Get(1).divide(p.Get(1))));
    Chop._12.requireClose(actual, expected);
  }

  public void testXAxis() {
    Tensor p = Tensors.vector(1 + Math.random(), 3);
    Tensor q = Tensors.vector(7 + Math.random(), 3);
    // ---
    Scalar actual = H2ParametricDistance.INSTANCE.distance(p, q);
    Scalar expected = RealScalar.of(2).multiply(ArcSinh.of(Abs.of(q.Get(0).subtract(p.Get(0))).divide(p.Get(1).add(p.Get(1)))));
    Chop._12.requireClose(actual, expected);
  }

  public void testNegativeY() {
    Tensor p = Tensors.vector(1, 3);
    Tensor q = Tensors.vector(2, -Math.random());
    try {
      H2ParametricDistance.INSTANCE.distance(p, q);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
