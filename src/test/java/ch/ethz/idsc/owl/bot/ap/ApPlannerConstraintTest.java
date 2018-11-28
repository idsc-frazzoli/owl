package ch.ethz.idsc.owl.bot.ap;

import java.util.Arrays;
import java.util.Collection;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Degree;
import junit.framework.TestCase;

public class ApPlannerConstraintTest extends TestCase {
  public void testOutOfBounds() {
    Tensor xUnvalid = Tensors.vector(-1, 0, 70, 0.1);
    Tensor zUnvalid = Tensors.vector(0, -1, 70, 0.1);
    Tensor vUnvalidStall = Tensors.vector(0, 0, 49, 0.1);
    Tensor vUnvalidMax = Tensors.vector(0, 0, 84, 0.1);
    ApPlannerConstraint ap = new ApPlannerConstraint();
    Flow flow = StateSpaceModels.createFlow(ApStateSpaceModel.INSTANCE, Tensors.vector(100,0.1));
   
    GlcNode pseudoNodeX = GlcNode.of(flow, new StateTime(xUnvalid, RealScalar.of(2)), RealScalar.ONE, RealScalar.ONE);
    assertFalse(ap.isSatisfied(pseudoNodeX, null, flow));

    GlcNode pseudoNodeZ = GlcNode.of(flow, new StateTime(zUnvalid, RealScalar.of(2)), RealScalar.ONE, RealScalar.ONE);
    assertFalse(ap.isSatisfied(pseudoNodeZ, null, flow));
    
    GlcNode pseudoNodeVStall = GlcNode.of(flow, new StateTime(vUnvalidStall, RealScalar.of(2)), RealScalar.ONE, RealScalar.ONE);
    assertFalse(ap.isSatisfied(pseudoNodeVStall, null, flow));

    GlcNode pseudoNodeVMax = GlcNode.of(flow, new StateTime(vUnvalidMax, RealScalar.of(2)), RealScalar.ONE, RealScalar.ONE);
    assertFalse(ap.isSatisfied(pseudoNodeVMax, null, flow));
  }
}
