package ch.ethz.idsc.owl.glc.rl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.Se2ComboRegion;
import ch.ethz.idsc.owl.bot.se2.Se2MinTimeGoalManager;
import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.bot.se2.glc.Se2CarFlows;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.glc.adapter.ConstraintViolationCost;
import ch.ethz.idsc.owl.glc.adapter.EmptyObstacleConstraint;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.GlcTrajectories;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.adapter.VectorCostGoalAdapter;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.gui.region.PolygonRegionRender;
import ch.ethz.idsc.owl.gui.ren.TrajectoryRender;
import ch.ethz.idsc.owl.gui.win.BaseFrame;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.StateTimeTensorFunction;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.PolygonRegion;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.region.So2Region;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Sqrt;


class RLTrajectoryPlannerSe2Demo implements DemoInterface {
  static final Scalar MAX_TURNING_PLAN = Degree.of(10);
  static final Scalar SPEED = RealScalar.of(0.5);
  static final Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(2), RealScalar.of(2), Degree.of(10).reciprocal()).unmodifiable();
  public static final FixedStateIntegrator FIXEDSTATEINTEGRATOR = // node interval == 2/5
      FixedStateIntegrator.create(Se2CarIntegrator.INSTANCE, RationalScalar.of(1, 20), 2);
  static final FlowsInterface CARFLOWS = Se2CarFlows.forward(SPEED, MAX_TURNING_PLAN);
  @Override
  public BaseFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    Tensor slacks = Tensors.vector(0.0, 0, 0);
    final Tensor stateRoot = Tensors.vector(0, 0, 0);
    final Tensor stateGoal = Tensors.vector(15, 0, 0);
    // ---
    final Scalar goalRadius_xy = Sqrt.of(RealScalar.of(2)).divide(PARTITIONSCALE.Get(0));
    final Scalar goalRadius_theta = Sqrt.of(RealScalar.of(2)).divide(PARTITIONSCALE.Get(2));
    Tensor goalRadius = Tensors.of(goalRadius_xy, goalRadius_xy, goalRadius_theta);
    FlowsInterface se2Flows = Se2CarFlows.of(Tensors.vector(8), MAX_TURNING_PLAN);
    Collection<Flow> controls = se2Flows.getFlows(3);
    // ---
    Tensor polygon = Tensors.matrixFloat(new float[][] { { 5, 0 }, { 5, -10 }, { 10, -10 }, { 10, 5 } });
    PolygonRegion polygonRegion = new PolygonRegion(polygon);
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(polygonRegion);
    CostFunction regionCost = ConstraintViolationCost.of(plannerConstraint, Quantity.of(1, ""));
    owlyAnimationFrame.addBackground(new PolygonRegionRender(polygonRegion));
    // ---
    // the 3rd cost penalizes distance of path
    RegionWithDistance<Tensor> goalRegion = new SphericalRegion(stateGoal.extract(0, 2), goalRadius.Get(0));
    Se2ComboRegion se2ComboRegion = new Se2ComboRegion(goalRegion, new So2Region(stateGoal.Get(2), goalRadius.Get(2)));
    CostFunction timeCost = new Se2MinTimeGoalManager(se2ComboRegion, controls);
    GoalInterface goalInterface = new VectorCostGoalAdapter(Arrays.asList(timeCost, regionCost, timeCost), se2ComboRegion);

    // ---
    EtaRaster raster = new EtaRaster(PARTITIONSCALE, StateTimeTensorFunction.state(Se2Wrap.INSTANCE::represent));
    RLTrajectoryPlanner trajectoryPlanner = new StandardRLTrajectoryPlanner( //
        raster, FIXEDSTATEINTEGRATOR, controls, EmptyObstacleConstraint.INSTANCE, goalInterface, slacks);
    trajectoryPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    GlcRLExpand glcExpand = new GlcRLExpand(trajectoryPlanner);
    glcExpand.untilOptimal(10000);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if(optional.isPresent()) {
      GlcNode goalNode = optional.get();
      VectorScalar cost = (VectorScalar) goalNode.costFromRoot();
      System.out.println("Cost from root: " + cost);
      // ---
      List<TrajectorySample> trajectory = GlcTrajectories.detailedTrajectoryTo(FIXEDSTATEINTEGRATOR, goalNode);
      // ---
      TrajectoryRender trajectoryRender = new TrajectoryRender();
      trajectoryRender.trajectory(trajectory);
      owlyAnimationFrame.addBackground(trajectoryRender);
    }
    return owlyAnimationFrame;
  }

  public static void main(String[] args) throws IOException {
    new RLTrajectoryPlannerSe2Demo().start().jFrame.setVisible(true);
  }
}
