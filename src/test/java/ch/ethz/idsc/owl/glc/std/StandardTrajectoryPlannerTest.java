// code by jph
package ch.ethz.idsc.owl.glc.std;

import java.util.Collection;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.r2.R2Flows;
import ch.ethz.idsc.owl.bot.rn.RnMinDistGoalManager;
import ch.ethz.idsc.owl.glc.adapter.EmptyPlannerConstraint;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.core.CheckedTrajectoryPlanner;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.HeuristicAssert;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.region.BallRegion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.sca.Ramp;
import junit.framework.TestCase;

public class StandardTrajectoryPlannerTest extends TestCase {
  public void testSimple() {
    final Tensor stateRoot = Tensors.vector(-2, -2);
    final Tensor stateGoal = Tensors.vector(2, 2);
    final Scalar radius = DoubleScalar.of(0.25);
    // ---
    Tensor eta = Tensors.vector(8, 8);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        EulerIntegrator.INSTANCE, SingleIntegratorStateSpaceModel.INSTANCE, RationalScalar.of(1, 5), 5);
    R2Flows r2Flows = new R2Flows(RealScalar.ONE);
    Collection<Tensor> controls = r2Flows.getFlows(36);
    GoalInterface goalInterface = RnMinDistGoalManager.sperical(stateGoal, radius);
    // ---
    StateTimeRaster stateTimeRaster = EtaRaster.state(eta);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster, stateIntegrator, controls, EmptyPlannerConstraint.INSTANCE, goalInterface);
    assertEquals(trajectoryPlanner.getStateIntegrator(), stateIntegrator);
    trajectoryPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(200);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      GlcNode goalNode = optional.get(); // <- throws exception if
      Scalar cost = goalNode.costFromRoot();
      // FIXME abs!?
      Scalar lowerBound = Ramp.of(Vector2Norm.of(stateGoal.subtract(stateRoot)).subtract(radius));
      if (Scalars.lessThan(cost, lowerBound))
        throw TensorRuntimeException.of(cost, lowerBound);
    }
    HeuristicAssert.check(trajectoryPlanner);
    // TrajectoryPlannerConsistency.check(trajectoryPlanner);
    assertTrue(glcExpand.getExpandCount() < 100);
  }

  public void testSimple2() {
    final Tensor stateRoot = Tensors.vector(-2, -2);
    final Tensor stateGoal = Tensors.vector(2, 2);
    final Scalar radius = DoubleScalar.of(0.25);
    // ---
    Tensor eta = Tensors.vector(8, 8);
    R2Flows r2Flows = new R2Flows(RealScalar.ONE);
    Collection<Tensor> controls = r2Flows.getFlows(36);
    BallRegion ballRegion = new BallRegion(stateGoal, radius);
    GoalInterface goalInterface = new RnMinDistGoalManager(ballRegion);
    // ---
    TrajectoryPlanner trajectoryPlanner = CheckedTrajectoryPlanner.wrap(new StandardTrajectoryPlanner( //
        EtaRaster.state(eta), //
        FixedStateIntegrator.create(EulerIntegrator.INSTANCE, SingleIntegratorStateSpaceModel.INSTANCE, RationalScalar.of(1, 5), 5), //
        controls, EmptyPlannerConstraint.INSTANCE, goalInterface));
    trajectoryPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(200);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    assertTrue(optional.isPresent());
  }
}
