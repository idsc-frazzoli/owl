package ch.ethz.idsc.owl.bot.balloon;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class BalloonPlannerConstraintTest extends TestCase {
  Tensor valid = Tensors.fromString("{5[m],0[m],4[m*s^-1],40[m * K^-1 * s^-2]}");
  Tensor yUnvalid = Tensors.fromString("{5[m],-3[m],4[m*s^-1],40[m * K^-1 * s^-2]}");
  Tensor vUnvalidNegative = Tensors.fromString("{5[m],0[m],-4[m*s^-1],40[m * K^-1 * s^-2]}");
  Tensor vUnvalidMax = Tensors.fromString("{5[m],0[m],6[m*s^-1],40[m * K^-1 * s^-2]}");
  BalloonPlannerConstraint plannerConstraint = new BalloonPlannerConstraint();
  Flow flow = StateSpaceModels.createFlow(BalloonStateSpaceModels.defaultWithUnits(), Tensors.vector(100, 0.1));

  public void testYConstraints() {
    GlcNode pseudoNodeZ = GlcNode.of(flow, new StateTime(yUnvalid, RealScalar.of(2)), RealScalar.ONE, RealScalar.ONE);
    assertFalse(plannerConstraint.isSatisfied(pseudoNodeZ, null, flow));
  }

  public void testVConstraints() {
    GlcNode pseudoNodeVNegative = GlcNode.of(flow, new StateTime(vUnvalidNegative, RealScalar.of(2)), RealScalar.ONE, RealScalar.ONE);
    GlcNode pseudoNodeVMax = GlcNode.of(flow, new StateTime(vUnvalidMax, RealScalar.of(2)), RealScalar.ONE, RealScalar.ONE);
    assertFalse(plannerConstraint.isSatisfied(pseudoNodeVNegative, null, flow) | plannerConstraint.isSatisfied(pseudoNodeVMax, null, flow));
  }

  public void testValidConstraints() {
    GlcNode pseudoNodeValid = GlcNode.of(flow, new StateTime(valid, RealScalar.of(2)), RealScalar.ONE, RealScalar.ONE);
    assertTrue(plannerConstraint.isSatisfied(pseudoNodeValid, null, flow));
  }
}
