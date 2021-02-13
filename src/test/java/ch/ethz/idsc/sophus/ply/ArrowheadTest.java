// code by jph
package ch.ethz.idsc.sophus.ply;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class ArrowheadTest extends TestCase {
  public void testOriented() {
    Tensor polygon = Arrowhead.of(1);
    Scalar scalar = PolygonArea.of(polygon);
    assertEquals(scalar, RationalScalar.HALF);
    Sign.requirePositive(scalar);
    Tensor centroid = PolygonCentroid.of(polygon);
    ExactTensorQ.require(centroid);
    assertEquals(centroid, Array.zeros(2));
  }

  public void testExact() {
    ExactTensorQ.require(Arrowhead.of(RealScalar.ONE));
  }

  public void testLength() {
    assertEquals(Arrowhead.of(6).length(), 3);
  }

  public void testMean() {
    Tensor tensor = Mean.of(Arrowhead.of(2));
    assertEquals(tensor, tensor.map(Scalar::zero));
  }
}
