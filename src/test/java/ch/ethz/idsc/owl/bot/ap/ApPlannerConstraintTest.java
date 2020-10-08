// code by astoll
package ch.ethz.idsc.owl.bot.ap;

import java.io.IOException;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.Serialization;
import junit.framework.TestCase;

public class ApPlannerConstraintTest extends TestCase {
  private static final Tensor valid = Tensors.vector(1, 21, 70, -0.05);
  private static final Tensor xUnvalid = Tensors.vector(-1, 0, 70, -0.1);
  private static final Tensor zUnvalid = Tensors.vector(0, -1, 70, -0.1);
  private static final Tensor vUnvalidStall = Tensors.vector(0, 0, 49, -0.1);
  private static final Tensor vUnvalidMax = Tensors.vector(0, 0, 84, -0.1);
  private static final Tensor gammaPositive = Tensors.vector(0, 0, 70, 0.1);
  private static final Tensor gammaTooSteep = Tensors.vector(0, 0, 70, -0.5);
  private static final Tensor zRateTooSteep = Tensors.vector(0, 0, 83, -0.18);
  private static final Tensor zRateValid = Tensors.vector(1000, 5, 60, -0.001);
  private static final ApPlannerConstraint ap = ApPlannerConstraint.INSTANCE;
  private static final Tensor flow = Tensors.vector(100, 0.1);

  public void testXConstraints() {
    GlcNode pseudoNodeX = GlcNode.of(flow, new StateTime(xUnvalid, RealScalar.of(2)), RealScalar.ONE, RealScalar.ONE);
    assertFalse(ap.isSatisfied(pseudoNodeX, null, flow));
  }

  public void testZConstraints() {
    GlcNode pseudoNodeZ = GlcNode.of(flow, new StateTime(zUnvalid, RealScalar.of(2)), RealScalar.ONE, RealScalar.ONE);
    assertFalse(ap.isSatisfied(pseudoNodeZ, null, flow));
  }

  public void testVConstraints() throws ClassNotFoundException, IOException {
    GlcNode pseudoNodeVStall = GlcNode.of(flow, new StateTime(vUnvalidStall, RealScalar.of(2)), RealScalar.ONE, RealScalar.ONE);
    GlcNode pseudoNodeVMax = GlcNode.of(flow, new StateTime(vUnvalidMax, RealScalar.of(2)), RealScalar.ONE, RealScalar.ONE);
    ApPlannerConstraint cp = Serialization.copy(ap);
    assertFalse(cp.isSatisfied(pseudoNodeVStall, null, flow) | ap.isSatisfied(pseudoNodeVMax, null, flow));
  }

  public void testGammaConstraints() {
    GlcNode pseudoNodeGammaPositive = GlcNode.of(flow, new StateTime(gammaPositive, RealScalar.of(2)), RealScalar.ONE, RealScalar.ONE);
    GlcNode pseudoNodeGammaTooSteep = GlcNode.of(flow, new StateTime(gammaTooSteep, RealScalar.of(2)), RealScalar.ONE, RealScalar.ONE);
    assertFalse(ap.isSatisfied(pseudoNodeGammaPositive, null, flow) | ap.isSatisfied(pseudoNodeGammaTooSteep, null, flow));
  }

  public void testTDRateConstraints() {
    GlcNode pseudoNodeZRateTooSteep = GlcNode.of(flow, new StateTime(zRateTooSteep, RealScalar.of(2)), RealScalar.ONE, RealScalar.ONE);
    GlcNode pseudoNodeZRateValid = GlcNode.of(flow, new StateTime(zRateValid, RealScalar.of(2)), RealScalar.ONE, RealScalar.ONE);
    assertTrue(!ap.isSatisfied(pseudoNodeZRateTooSteep, null, flow) & ap.isSatisfied(pseudoNodeZRateValid, null, flow));
  }

  public void testValidConstraints() {
    GlcNode pseudoNodeValid = GlcNode.of(flow, new StateTime(valid, RealScalar.of(2)), RealScalar.ONE, RealScalar.ONE);
    assertTrue(ap.isSatisfied(pseudoNodeValid, null, flow));
  }
}
