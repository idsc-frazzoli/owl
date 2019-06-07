// code by jph
package ch.ethz.idsc.sophus.planar;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class AnalyticClothoidDistanceTest extends TestCase {
  public void testEuclidean() {
    Tensor p = Tensors.vector(1, 2, 0);
    Tensor q = Tensors.vector(9, 2, 0);
    Scalar scalar = AnalyticClothoidDistance.LR1.distance(p, q);
    assertEquals(scalar, RealScalar.of(8));
    ExactScalarQ.require(scalar);
  }

  public void testOrigin() {
    assertEquals(AnalyticClothoidDistance.LR1.norm(Tensors.vector(0, 0, 0)), RealScalar.of(0));
    assertEquals(AnalyticClothoidDistance.LR3.norm(Tensors.vector(0, 0, 0)), RealScalar.of(0));
  }
}
