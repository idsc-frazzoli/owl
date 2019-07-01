// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
import ch.ethz.idsc.owl.glc.core.GlcTrajectoryPlanner;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.std.StandardGlcTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.OwlyFrame;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.EllipsoidRegion;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.owl.math.state.EmptyTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.AnimationWriter;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Ramp;

/* package */ enum R2DemoSlow {
  ;
  static GlcTrajectoryPlanner simpleEmpty() throws Exception {
    return simple(EmptyTrajectoryRegionQuery.INSTANCE);
  }

  static GlcTrajectoryPlanner simpleR2Bubbles() throws Exception {
    return simple(CatchyTrajectoryRegionQuery.timeInvariant(R2Bubbles.INSTANCE));
  }

  static GlcTrajectoryPlanner simpleR2Circle() throws Exception {
    return simple(CatchyTrajectoryRegionQuery.timeInvariant( //
        new EllipsoidRegion(Tensors.vector(-1, 0), Tensors.vector(2, 2))));
  }

  private static GlcTrajectoryPlanner simple(TrajectoryRegionQuery obstacleQuery) throws Exception {
    final Tensor stateRoot = Tensors.vector(-2.2, -2.2);
    final Tensor stateGoal = Tensors.vector(2, 3.5);
    final Scalar radius = DoubleScalar.of(0.8);
    // ---
    Tensor eta = Tensors.vector(1.5, 1.5);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create(EulerIntegrator.INSTANCE, RationalScalar.of(1, 5), 5);
    R2Flows r2Flows = new R2Flows(RealScalar.ONE);
    Collection<Flow> controls = r2Flows.getFlows(6);
    SphericalRegion sphericalRegion = new SphericalRegion(stateGoal, radius);
    GoalInterface goalInterface = new RnMinDistGoalManager(sphericalRegion);
    RenderInterface renderInterface = RegionRenders.create(obstacleQuery);
    // ---
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(obstacleQuery);
    GlcTrajectoryPlanner trajectoryPlanner = new StandardGlcTrajectoryPlanner( //
        EtaRaster.state(eta), stateIntegrator, controls, plannerConstraint, goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    try (AnimationWriter animationWriter = AnimationWriter.of(HomeDirectory.Pictures("R2_Slow.gif"), 400)) {
      OwlyFrame owlyFrame = OwlyGui.start();
      owlyFrame.addBackground(RegionRenders.create(sphericalRegion));
      owlyFrame.addBackground(renderInterface); // reference to collection
      for (int i = 0; i < 20; ++i) {
        Optional<GlcNode> optional = trajectoryPlanner.getBest();
        if (optional.isPresent())
          break;
        glcExpand.findAny(1);
        owlyFrame.setGlc(trajectoryPlanner);
        animationWriter.append(owlyFrame.offscreen());
      }
      for (int i = 0; i < 4; ++i)
        animationWriter.append(owlyFrame.offscreen());
    }
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      GlcNode goalNode = optional.get(); // <- throws exception if
      Scalar cost = goalNode.costFromRoot();
      Scalar lowerBound = Ramp.of(Norm._2.between(stateGoal, stateRoot).subtract(radius));
      if (Scalars.lessThan(cost, lowerBound))
        throw TensorRuntimeException.of(cost, lowerBound);
    }
    return trajectoryPlanner;
  }

  static void demo(GlcTrajectoryPlanner trajectoryPlanner) {
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
