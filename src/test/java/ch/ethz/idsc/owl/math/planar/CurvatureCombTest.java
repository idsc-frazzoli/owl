// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class CurvatureCombTest extends TestCase {
  public void testSimple() {
    Tensor tensor = CurvatureComb.of(Tensors.fromString("{{0,0,0},{1,1,0},{2,0,0}}"), RealScalar.ONE);
    System.out.println(tensor);
  }

  public void testString() {
    Tensor tensor = CurvatureComb.string(Tensors.fromString("{{0,0},{1,1},{2,0}}"));
    assertTrue(Chop._12.close(tensor, Tensors.fromString("{{0,0},{0,1},{0,0}}")));
  }

  public void testEmpty() {
    Tensor tensor = CurvatureComb.of(Tensors.empty(), RealScalar.of(2));
    assertTrue(Tensors.isEmpty(tensor));
  }

  public void testOne() {
    Tensor tensor = CurvatureComb.of(Tensors.fromString("{{1,2,3}}"), RealScalar.of(2));
    assertEquals(tensor, Tensors.fromString("{{1,2}}"));
  }

  public void testTwo() {
    Tensor tensor = CurvatureComb.of(Tensors.fromString("{{1,2,3},{4,5,6}}"), RealScalar.of(2));
    assertEquals(tensor, Tensors.fromString("{{1,2},{4,5}}"));
  }
}
