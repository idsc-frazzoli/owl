// code by astoll
package ch.ethz.idsc.owl.bot.balloon;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class BalloonPlannerConstraintTest extends TestCase {
  private static final PlannerConstraint PLANNER_CONSTRAINT = //
      new BalloonPlannerConstraint(Quantity.of(5, "m*s^-1"));
  private static final Scalar TIME = Quantity.of(2, "s");
  private static final Flow FLOW = //
      StateSpaceModels.createFlow(BalloonStateSpaceModels.defaultWithUnits(), Tensors.vector(100, 0.1));

  public void testYConstraints() {
    Tensor yUnvalid = Tensors.fromString("{5[m], -3[m], 4[m*s^-1], 40[m * K^-1 * s^-2]}");
    GlcNode pseudoNodeZ = GlcNode.of(FLOW, new StateTime(yUnvalid, TIME), RealScalar.ONE, RealScalar.ONE);
    assertFalse(PLANNER_CONSTRAINT.isSatisfied(pseudoNodeZ, null, FLOW));
  }

  public void testVConstraints() {
    Tensor vUnvalidMax = Tensors.fromString("{5[m], 0[m], 6[m*s^-1], 40[m * K^-1 * s^-2]}");
    Tensor vUnvalidNegative = Tensors.fromString("{5[m], 0[m], -4[m*s^-1], 40[m * K^-1 * s^-2]}");
    GlcNode pseudoNodeVNegative = GlcNode.of(FLOW, new StateTime(vUnvalidNegative, TIME), RealScalar.ONE, RealScalar.ONE);
    GlcNode pseudoNodeVMax = GlcNode.of(FLOW, new StateTime(vUnvalidMax, TIME), RealScalar.ONE, RealScalar.ONE);
    assertTrue(PLANNER_CONSTRAINT.isSatisfied(pseudoNodeVNegative, null, FLOW));
    assertFalse(PLANNER_CONSTRAINT.isSatisfied(pseudoNodeVMax, null, FLOW));
  }

  public void testValidConstraints() {
    Tensor valid = Tensors.fromString("{5[m], 0[m], 4[m*s^-1], 40[m * K^-1 * s^-2]}");
    GlcNode pseudoNodeValid = GlcNode.of(FLOW, new StateTime(valid, TIME), RealScalar.ONE, RealScalar.ONE);
    assertTrue(PLANNER_CONSTRAINT.isSatisfied(pseudoNodeValid, null, FLOW));
  }
}
