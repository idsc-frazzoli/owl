// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import ch.ethz.idsc.owl.bot.r2.R2Bubbles;
import ch.ethz.idsc.owl.bot.r2.R2Flows;
import ch.ethz.idsc.owl.bot.rn.RnMinDistGoalManager;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcNodes;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.OwlyFrame;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.region.BallRegion;
import ch.ethz.idsc.owl.math.region.EllipsoidRegion;
import ch.ethz.idsc.owl.math.state.EmptyTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.io.AnimationWriter;
import ch.ethz.idsc.tensor.io.GifAnimationWriter;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.sca.Ramp;

/* package */ enum R2DemoSlow {
  ;
  static TrajectoryPlanner simpleEmpty() throws Exception {
    return simple(EmptyTrajectoryRegionQuery.INSTANCE);
  }

  static TrajectoryPlanner simpleR2Bubbles() throws Exception {
    return simple(CatchyTrajectoryRegionQuery.timeInvariant(R2Bubbles.INSTANCE));
  }

  static TrajectoryPlanner simpleR2Circle() throws Exception {
    return simple(CatchyTrajectoryRegionQuery.timeInvariant( //
        new EllipsoidRegion(Tensors.vector(-1, 0), Tensors.vector(2, 2))));
  }

  private static TrajectoryPlanner simple(TrajectoryRegionQuery obstacleQuery) throws Exception {
    Tensor stateRoot = Tensors.vector(-2.2, -2.2);
    Tensor stateGoal = Tensors.vector(2, 3.5);
    Scalar radius = RealScalar.of(0.8);
    Tensor eta = Tensors.vector(1.5, 1.5);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        EulerIntegrator.INSTANCE, SingleIntegratorStateSpaceModel.INSTANCE, RationalScalar.of(1, 5), 5);
    R2Flows r2Flows = new R2Flows(RealScalar.ONE);
    Collection<Tensor> controls = r2Flows.getFlows(6);
    BallRegion ballRegion = new BallRegion(stateGoal, radius);
    GoalInterface goalInterface = new RnMinDistGoalManager(ballRegion);
    RenderInterface renderInterface = RegionRenders.create(obstacleQuery);
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(obstacleQuery);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        EtaRaster.state(eta), stateIntegrator, controls, plannerConstraint, goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    try (AnimationWriter animationWriter = //
        new GifAnimationWriter(HomeDirectory.Pictures("R2_Slow.gif"), 400, TimeUnit.MILLISECONDS)) {
      OwlyFrame owlyFrame = OwlyGui.start();
      owlyFrame.addBackground(RegionRenders.create(ballRegion));
      owlyFrame.addBackground(renderInterface); // reference to collection
      for (int i = 0; i < 20; ++i) {
        Optional<GlcNode> optional = trajectoryPlanner.getBest();
        if (optional.isPresent())
          break;
        glcExpand.findAny(1);
        owlyFrame.setGlc(trajectoryPlanner);
        animationWriter.write(owlyFrame.offscreen());
      }
      for (int i = 0; i < 4; ++i)
        animationWriter.write(owlyFrame.offscreen());
    }
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

  static void demo(TrajectoryPlanner trajectoryPlanner) {
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.get());
      StateTimeTrajectories.print(trajectory);
    }
    // OwlyGui.glc(trajectoryPlanner);
  }

  public static void main(String[] args) throws Exception {
    simpleR2Circle();
  }
}
