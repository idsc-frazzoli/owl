// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class CurvatureCombTest extends TestCase {
  public void testSimple() {
    Tensor tensor = CurvatureComb.of(Tensors.fromString("{{0,0,0},{1,1,0},{2,0,0}}"), RealScalar.ONE, false);
    String string = "{{-0.7071067811865474, 0.7071067811865474}, {1, 2}, {2.7071067811865475, 0.7071067811865474}}";
    Tensor result = Tensors.fromString(string);
    Chop._12.requireClose(tensor, result);
  }

  public void testStringLength() {
    Distribution distribution = NormalDistribution.standard();
    for (int count = 0; count < 10; ++count) {
      Tensor tensor = RandomVariate.of(distribution, count, 2);
      Tensor string = CurvatureComb.string(tensor);
      assertEquals(string.length(), count);
    }
  }

  public void testCircle() {
    Tensor tensor = CurvatureComb.of(CirclePoints.of(4), RationalScalar.ONE, true);
    assertTrue(Chop._14.close(tensor, CirclePoints.of(4).multiply(RealScalar.of(2))));
  }

  public void testString() {
    Tensor tensor = CurvatureComb.string(Tensors.fromString("{{0,0},{1,1},{2,0}}"));
    String format = "{{-0.7071067811865474, 0.7071067811865474}, {0, 1}, {0.7071067811865474, 0.7071067811865474}}";
    Tensor result = Tensors.fromString(format);
    Chop._12.requireClose(tensor, result);
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
