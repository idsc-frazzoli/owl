// code by astoll
package ch.ethz.idsc.owl.bot.balloon;

import ch.ethz.idsc.owl.glc.core.GlcNode;
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
  BalloonPlannerConstraint plannerConstraint = new BalloonPlannerConstraint(Quantity.of(5, "m*s^-1"));
  Scalar time = Quantity.of(2, "s");
  Flow flow = StateSpaceModels.createFlow(BalloonStateSpaceModels.defaultWithUnits(), Tensors.vector(100, 0.1));

  public void testYConstraints() {
    Tensor yUnvalid = Tensors.fromString("{5[m],-3[m],4[m*s^-1],40[m * K^-1 * s^-2]}");
    GlcNode pseudoNodeZ = GlcNode.of(flow, new StateTime(yUnvalid, time), RealScalar.ONE, RealScalar.ONE);
    assertFalse(plannerConstraint.isSatisfied(pseudoNodeZ, null, flow));
  }

  public void testVConstraints() {
    Tensor vUnvalidMax = Tensors.fromString("{5[m],0[m],6[m*s^-1],40[m * K^-1 * s^-2]}");
    Tensor vUnvalidNegative = Tensors.fromString("{5[m],0[m],-4[m*s^-1],40[m * K^-1 * s^-2]}");
    GlcNode pseudoNodeVNegative = GlcNode.of(flow, new StateTime(vUnvalidNegative, time), RealScalar.ONE, RealScalar.ONE);
    GlcNode pseudoNodeVMax = GlcNode.of(flow, new StateTime(vUnvalidMax, time), RealScalar.ONE, RealScalar.ONE);
    assertTrue(plannerConstraint.isSatisfied(pseudoNodeVNegative, null, flow));
    assertFalse(plannerConstraint.isSatisfied(pseudoNodeVMax, null, flow));
  }

  public void testValidConstraints() {
    Tensor valid = Tensors.fromString("{5[m],0[m],4[m*s^-1],40[m * K^-1 * s^-2]}");
    GlcNode pseudoNodeValid = GlcNode.of(flow, new StateTime(valid, time), RealScalar.ONE, RealScalar.ONE);
    assertTrue(plannerConstraint.isSatisfied(pseudoNodeValid, null, flow));
  }
}
