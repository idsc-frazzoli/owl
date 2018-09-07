// code by jph
package ch.ethz.idsc.owl.bot.se2.tse2;

import java.util.Arrays;

import ch.ethz.idsc.owl.bot.tse2.Tse2VelocityConstraint;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class Tse2VelocityConstraintTest extends TestCase {
  public void testSimple() {
    PlannerConstraint plannerConstraint = //
        new Tse2VelocityConstraint(Quantity.of(-3, "m*s^-1"), Quantity.of(5, "m*s^-1"));
    assertTrue(plannerConstraint.isSatisfied(null, //
        Arrays.asList(new StateTime(Tensors.fromString("{-Infinity,-Infinity,-Infinity,2[m*s^-1]}"), RealScalar.of(2))), null));
    assertFalse(plannerConstraint.isSatisfied(null, //
        Arrays.asList(new StateTime(Tensors.fromString("{-Infinity,-Infinity,-Infinity,6[m*s^-1]}"), RealScalar.of(2))), null));
  }

  public void testEquals() {
    PlannerConstraint plannerConstraint = //
        new Tse2VelocityConstraint(Quantity.of(5, "m*s^-1"), Quantity.of(5, "m*s^-1"));
    assertTrue(plannerConstraint.isSatisfied(null, //
        Arrays.asList(new StateTime(Tensors.fromString("{-Infinity,-Infinity,-Infinity,5[m*s^-1]}"), RealScalar.of(2))), null));
    assertFalse(plannerConstraint.isSatisfied(null, //
        Arrays.asList(new StateTime(Tensors.fromString("{-Infinity,-Infinity,-Infinity,6[m*s^-1]}"), RealScalar.of(2))), null));
  }
}
