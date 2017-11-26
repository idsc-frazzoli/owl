// code by jl
package ch.ethz.idsc.owl.bot.rnxt.glc;

import ch.ethz.idsc.owl.math.Degree;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class TimeDependentTurningRingRegionTest extends TestCase {
  public void testSimple() {
    Tensor center = Tensors.vector(0, 0);
    Scalar initialGapAngle = Degree.of(0);
    Scalar gapLength = Degree.of(40);
    Scalar ringThickness = RealScalar.of(0.4);
    Scalar ringRadius = RealScalar.ONE;
    TimeDependentTurningRingRegion test = new TimeDependentTurningRingRegion(center, initialGapAngle, gapLength, ringThickness, ringRadius);
    assertFalse(test.isMember(new StateTime(Tensors.vector(0.5, 0), RealScalar.of(0)))); // inside
    assertFalse(test.isMember(new StateTime(Tensors.vector(0, 0.5), RealScalar.of(0)))); // inside
    assertFalse(test.isMember(new StateTime(Tensors.vector(0, -1.5), RealScalar.of(0)))); // outside
    assertFalse(test.isMember(new StateTime(Tensors.vector(0, 1.5), RealScalar.of(0)))); // outside
    assertFalse(test.isMember(new StateTime(Tensors.vector(1, 0), RealScalar.of(0)))); // in gap
    assertTrue(test.isMember(new StateTime(Tensors.vector(1, 0), RealScalar.of(3)))); // 3s = 90° later
    // --
    assertTrue(test.isMember(new StateTime(Tensors.vector(0, 1), RealScalar.of(0)))); // North
    assertFalse(test.isMember(new StateTime(Tensors.vector(0, 1), RealScalar.of(3)))); // 3s = 90° later in gap at North
    assertTrue(test.isMember(new StateTime(Tensors.vector(1, 0), RealScalar.of(3))));
    // --
    assertTrue(test.isMember(new StateTime(Tensors.vector(-1, 0), RealScalar.of(0)))); // West
    assertFalse(test.isMember(new StateTime(Tensors.vector(-1, 0), RealScalar.of(6)))); // 6s = 180° later in gap
    assertTrue(test.isMember(new StateTime(Tensors.vector(1, 0), RealScalar.of(6))));
    // --
    assertTrue(test.isMember(new StateTime(Tensors.vector(0, -1), RealScalar.of(0)))); // South
    assertFalse(test.isMember(new StateTime(Tensors.vector(0, -1), RealScalar.of(9))));
    assertTrue(test.isMember(new StateTime(Tensors.vector(1, 0), RealScalar.of(9))));// 9s=270° later in gap
    // --
  }

  public void testFail() {
    Tensor center = Tensors.vector(0, 0);
    Scalar initialGapAngle = Degree.of(0);
    Scalar gapLength = Degree.of(40);
    Scalar ringThickness = RealScalar.of(0.4);
    Scalar ringRadius = RealScalar.ONE;
    // TimeDependentTurningRingRegion test =
    new TimeDependentTurningRingRegion(center, initialGapAngle, gapLength, ringThickness, ringRadius);
    try {
      // throws exception, since time info is not consistent
      // test.isMember(new StateTime(Tensors.vector(0.5, 1, 0), RealScalar.of(1)));
      // assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
