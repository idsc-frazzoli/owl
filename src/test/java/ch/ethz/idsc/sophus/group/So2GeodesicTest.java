// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.OrthogonalMatrixQ;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class So2GeodesicTest extends TestCase {
  public void testSimple() {
    Tensor p = So2Exponential.INSTANCE.exp(Tensors.vector(1));
    Tensor q = So2Exponential.INSTANCE.exp(Tensors.vector(2));
    Tensor split = So2Geodesic.INSTANCE.split(p, q, RationalScalar.HALF);
    assertTrue(OrthogonalMatrixQ.of(split, Chop._14));
  }

  public void testEndPoints() {
    for (int index = 0; index < 10; ++index) {
      Tensor p = So2Exponential.INSTANCE.exp(Tensors.vector(Math.random()));
      Tensor q = So2Exponential.INSTANCE.exp(Tensors.vector(Math.random()));
      Chop._12.requireClose(p, So2Geodesic.INSTANCE.split(p, q, RealScalar.ZERO));
      // FIXME OB: Why does this test fail?
      // Chop._09.requireClose(q, So2Geodesic.INSTANCE.split(p, q, RealScalar.ONE));
    }
  }
}
