// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.r2.R2Bubbles;
import ch.ethz.idsc.owl.bot.r2.R2Flows;
import ch.ethz.idsc.owl.bot.rn.RnMinDistGoalManager;
import ch.ethz.idsc.owl.glc.adapter.EmptyPlannerConstraint;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcNodes;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.region.BallRegion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.sca.Ramp;

/** functionality is used in tests */
/* package */ enum R2Demo {
  ;
  static final StateIntegrator STATE_INTEGRATOR = FixedStateIntegrator.create( //
      EulerIntegrator.INSTANCE, SingleIntegratorStateSpaceModel.INSTANCE, RationalScalar.of(1, 5), 5);

  static TrajectoryPlanner simpleEmpty() {
    return simple(EmptyPlannerConstraint.INSTANCE);
  }

  static TrajectoryPlanner simpleR2Bubbles() {
    return simple(RegionConstraints.timeInvariant(R2Bubbles.INSTANCE));
  }

  private static TrajectoryPlanner simple(PlannerConstraint plannerConstraint) {
    Tensor stateRoot = Tensors.vector(-2, -2);
    Tensor stateGoal = Tensors.vector(2, 2);
    Scalar radius = RealScalar.of(0.25);
    Tensor eta = Tensors.vector(8, 8);
    R2Flows r2Flows = new R2Flows(RealScalar.ONE);
    Collection<Tensor> controls = r2Flows.getFlows(36);
    BallRegion ballRegion = new BallRegion(stateGoal, radius);
    GoalInterface goalInterface = new RnMinDistGoalManager(ballRegion);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        EtaRaster.state(eta), STATE_INTEGRATOR, controls, plannerConstraint, goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(200);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      GlcNode goalNode = optional.get(); // <- throws exception if
      Scalar cost = goalNode.costFromRoot();
      Scalar lowerBound = Ramp.of(Vector2Norm.between(stateGoal, stateRoot).subtract(radius));
      if (Scalars.lessThan(cost, lowerBound))
        throw TensorRuntimeException.of(cost, lowerBound);
    }
    return trajectoryPlanner;
  }

  private static void demo(TrajectoryPlanner trajectoryPlanner) {
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.get());
      StateTimeTrajectories.print(trajectory);
    }
    OwlyGui.glc(trajectoryPlanner);
  }

  public static void main(String[] args) {
    demo(simpleEmpty());
    demo(simpleR2Bubbles());
  }
}
