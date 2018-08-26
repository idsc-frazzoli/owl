// code by jph
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.RotateLeft;
import junit.framework.TestCase;

public class CircleCurveIntersectionTest extends TestCase {
  public void testString() {
    Tensor curve = Tensors.fromString("{{0,0},{1,0},{1,1},{0,1}}").unmodifiable();
    CurveIntersection curveIntersection = new CircleCurveIntersection(RationalScalar.HALF);
    for (int index = 0; index < curve.length(); ++index) {
      Optional<Tensor> optional = curveIntersection.string(RotateLeft.of(curve, index));
      assertEquals(optional.isPresent(), index != 1);
      if (index != 1) {
        Tensor tensor = optional.get();
        assertTrue(ExactScalarQ.all(tensor));
        assertEquals(tensor, Tensors.vector(0.5, 0));
      }
    }
  }

  public void testCyclic() {
    Tensor curve = Tensors.fromString("{{0,0},{1,0},{1,1},{0,1}}").unmodifiable();
    CurveIntersection curveIntersection = new CircleCurveIntersection(RationalScalar.HALF);
    for (int index = 0; index < curve.length(); ++index) {
      Optional<Tensor> optional = curveIntersection.cyclic(RotateLeft.of(curve, index));
      assertTrue(optional.isPresent());
      Tensor tensor = optional.get();
      assertTrue(ExactScalarQ.all(tensor));
      assertEquals(tensor, Tensors.vector(0.5, 0));
    }
  }
}
