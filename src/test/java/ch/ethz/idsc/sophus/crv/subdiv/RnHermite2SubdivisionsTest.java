// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class RnHermite2SubdivisionsTest extends TestCase {
  public void testA1() {
    RnHermite2Subdivision a1 = RnHermite2Subdivisions.a1();
    // lambda == -1/8, mu == -1/2
    RnHermite2Subdivision hs = RnHermite2Subdivisions.of(RationalScalar.of(-1, 8), RationalScalar.of(-1, 2));
    assertEquals(a1.ALP, hs.ALP);
    assertEquals(a1.ALQ, hs.ALQ);
    assertEquals(a1.AHP, hs.AHP);
    assertEquals(a1.AHQ, hs.AHQ);
  }

  public void testMoreA1() {
    Tensor ALP = Tensors.fromString("{{27/32, +9/64}, {-9/16,  3/32}}"); // A(+0)
    Tensor ALQ = Tensors.fromString("{{ 5/32, -3/64}, {+9/16, -5/32}}"); // A(-2)
    Tensor AHP = Tensors.fromString("{{ 5/32, +3/64}, {-9/16, -5/32}}"); // A(+1)
    Tensor AHQ = Tensors.fromString("{{27/32, -9/64}, {+9/16,  3/32}}"); // A(-1)
    RnHermite2Subdivision hs = RnHermite2Subdivisions.a1();
    assertEquals(hs.ALP, ALP);
    assertEquals(hs.ALQ, ALQ);
    assertEquals(hs.AHP, AHP);
    assertEquals(hs.AHQ, AHQ);
  }

  public void testA2() {
    RnHermite2Subdivision a2 = RnHermite2Subdivisions.a2();
    // lambda == -1/5, mu == 9/10
    RnHermite2Subdivision hs = RnHermite2Subdivisions.of(RationalScalar.of(-1, 5), RationalScalar.of(9, 10));
    assertEquals(a2.ALP, hs.ALP);
    assertEquals(a2.ALQ, hs.ALQ);
    assertEquals(a2.AHP, hs.AHP);
    assertEquals(a2.AHQ, hs.AHQ);
  }

  public void testMoreA2() {
    Tensor ALP = Tensors.fromString("{{19/25, +31/200}, {-29/400, 277/800}}"); // A(+0)
    Tensor ALQ = Tensors.fromString("{{ 6/25, -29/200}, {+29/400,  65/800}}"); // A(-2)
    Tensor AHP = Tensors.fromString("{{ 6/25, +29/200}, {-29/400,  65/800}}"); // A(+1)
    Tensor AHQ = Tensors.fromString("{{19/25, -31/200}, {+29/400, 277/800}}"); // A(-1)
    RnHermite2Subdivision hs = RnHermite2Subdivisions.a2();
    assertEquals(hs.ALP, ALP);
    assertEquals(hs.ALQ, ALQ);
    assertEquals(hs.AHP, AHP);
    assertEquals(hs.AHQ, AHQ);
  }
}
