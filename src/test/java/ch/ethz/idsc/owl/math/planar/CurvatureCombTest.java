// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class CurvatureCombTest extends TestCase {
  public void testSimple() {
    Tensor tensor = CurvatureComb.of(Tensors.fromString("{{0,0,0},{1,1,0},{2,0,0}}"), RealScalar.ONE, false);
    Tensor expected = Tensors.fromString("{{0, 0}, {1, 2}, {2, 0}}");
    assertTrue(Chop._14.close(tensor, expected));
  }

  public void testCircle() {
    Tensor tensor = CurvatureComb.of(CirclePoints.of(4), RationalScalar.ONE, true);
    assertTrue(Chop._14.close(tensor, CirclePoints.of(4).multiply(RealScalar.of(2))));
  }

  public void testString() {
    Tensor tensor = CurvatureComb.string(Tensors.fromString("{{0,0},{1,1},{2,0}}"));
    assertTrue(Chop._14.close(tensor, Tensors.fromString("{{0,0},{0,1},{0,0}}")));
  }

  public void testEmpty() {
    assertTrue(Tensors.isEmpty(CurvatureComb.of(Tensors.empty(), RealScalar.of(2), true)));
    assertTrue(Tensors.isEmpty(CurvatureComb.of(Tensors.empty(), RealScalar.of(2), false)));
  }

  public void testOne() {
    Tensor tensor = CurvatureComb.of(Tensors.fromString("{{1,2,3}}"), RealScalar.of(2), false);
    assertEquals(tensor, Tensors.fromString("{{1,2}}"));
  }

  public void testTwo() {
    Tensor tensor = CurvatureComb.of(Tensors.fromString("{{1,2,3},{4,5,6}}"), RealScalar.of(2), false);
    assertEquals(tensor, Tensors.fromString("{{1,2},{4,5}}"));
  }
}
