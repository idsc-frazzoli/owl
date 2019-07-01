// code by jph
package ch.ethz.idsc.owl.glc.core;

import java.util.Collection;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.r2.R2Flows;
import ch.ethz.idsc.owl.bot.rn.RnMinDistGoalManager;
import ch.ethz.idsc.owl.glc.adapter.EmptyObstacleConstraint;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.std.StandardGlcTrajectoryPlanner;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class GlcTrajectoryPlannerTest extends TestCase {
  // static final StateIntegrator STATE_INTEGRATOR = //
  // ;
  public void testSimple() {
    final Tensor stateRoot = Tensors.vector(-2, -2);
    final Tensor stateGoal = Tensors.vector(2, 2);
    final Scalar radius = DoubleScalar.of(0.25);
    // ---
    Tensor eta = Tensors.vector(8, 8);
    R2Flows r2Flows = new R2Flows(RealScalar.ONE);
    Collection<Flow> controls = r2Flows.getFlows(36);
    SphericalRegion sphericalRegion = new SphericalRegion(stateGoal, radius);
    GoalInterface goalInterface = new RnMinDistGoalManager(sphericalRegion);
    // ---
    GlcTrajectoryPlanner trajectoryPlanner = CheckedGlcTrajectoryPlanner.wrap(new StandardGlcTrajectoryPlanner( //
        EtaRaster.state(eta), //
        FixedStateIntegrator.create(EulerIntegrator.INSTANCE, RationalScalar.of(1, 5), 5), //
        controls, EmptyObstacleConstraint.INSTANCE, goalInterface));
    trajectoryPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(200);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    assertTrue(optional.isPresent());
  }
}
