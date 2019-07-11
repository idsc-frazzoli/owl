// code by jph
package ch.ethz.idsc.owl.bot.se2;

import java.util.Optional;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class TurningGeometryTest extends TestCase {
  public void test90() {
    Optional<Scalar> offsetY = TurningGeometry.offset_y(RealScalar.ONE, RealScalar.of(Math.PI / 2));
    assertTrue(Chop._10.allZero(offsetY.get()));
  }

  public void test45() {
    Optional<Scalar> offsetY = TurningGeometry.offset_y(RealScalar.ONE, RealScalar.of(Math.PI / 4));
    assertTrue(Chop._10.close(offsetY.get(), RealScalar.ONE));
  }

  public void test45neg() {
    Optional<Scalar> offsetY = TurningGeometry.offset_y(RealScalar.ONE, RealScalar.of(-Math.PI / 4));
    assertTrue(Chop._10.close(offsetY.get(), RealScalar.ONE.negate()));
  }

  public void test0() {
    Optional<Scalar> offsetY = TurningGeometry.offset_y(RealScalar.ONE, RealScalar.ZERO);
    assertFalse(offsetY.isPresent());
  }

  public void testClose0() {
    Optional<Scalar> offsetY = TurningGeometry.offset_y(RealScalar.ONE, RealScalar.of(TurningGeometry.CHOP.threshold()));
    assertTrue(offsetY.isPresent());
  }

  public void test45Units() {
    Optional<Scalar> offsetY = TurningGeometry.offset_y(Quantity.of(1.23, "m"), RealScalar.of(0.345));
    assertTrue(Chop._10.close(offsetY.get(), Quantity.of(3.4226321090018064, "m")));
  }

  public void testRatio() {
    Scalar angle = Quantity.of(0.23, "m^-1");
    Optional<Scalar> offsetY = TurningGeometry.offset_y(angle);
    assertEquals(offsetY.get(), angle.reciprocal());
  }
}
