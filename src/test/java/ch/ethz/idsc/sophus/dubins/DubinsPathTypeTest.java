// code by jph
package ch.ethz.idsc.sophus.dubins;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class DubinsPathTypeTest extends TestCase {
  public void testFirstTurnRight() {
    assertFalse(DubinsPathType.LSR.isFirstTurnRight());
    assertTrue(DubinsPathType.RSL.isFirstTurnRight());
    assertFalse(DubinsPathType.LSL.isFirstTurnRight());
    assertTrue(DubinsPathType.RSR.isFirstTurnRight());
    assertFalse(DubinsPathType.LRL.isFirstTurnRight());
    assertTrue(DubinsPathType.RLR.isFirstTurnRight());
  }

  public void testFirstEqualsLast() {
    assertFalse(DubinsPathType.LSR.isFirstEqualsLast());
    assertFalse(DubinsPathType.RSL.isFirstEqualsLast());
    assertTrue(DubinsPathType.LSL.isFirstEqualsLast());
    assertTrue(DubinsPathType.RSR.isFirstEqualsLast());
    assertTrue(DubinsPathType.LRL.isFirstEqualsLast());
    assertTrue(DubinsPathType.RLR.isFirstEqualsLast());
  }

  public void testTangentUnit() {
    Tensor tensor = DubinsPathType.LSR.tangent(2, Quantity.of(10, "m"));
    assertEquals(tensor.get(0), RealScalar.ONE);
    assertEquals(tensor.get(1), RealScalar.ZERO);
    assertEquals(tensor.get(2), Quantity.of(RationalScalar.of(-1, 10), "m^-1"));
  }

  public void testTangentDimensionless() {
    Tensor tensor = DubinsPathType.LSR.tangent(2, RealScalar.of(10));
    assertEquals(tensor.get(0), RealScalar.ONE);
    assertEquals(tensor.get(1), RealScalar.ZERO);
    assertEquals(tensor.get(2), RationalScalar.of(-1, 10));
  }

  public void testFail() {
    try {
      DubinsPathType.LSR.tangent(2, RealScalar.of(-10));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testAnticipateFail() {
    try {
      DubinsPathType.LSR.tangent(-2, RealScalar.of(10));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
