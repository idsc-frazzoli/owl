// code by yn
package ch.ethz.idsc.owl.bot.rn.glc;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.r2.R2Flows;
import ch.ethz.idsc.owl.bot.r2.R2RationalFlows;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.glc.adapter.ConstraintViolationCost;
import ch.ethz.idsc.owl.glc.adapter.EmptyPlannerConstraint;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.adapter.GlcTrajectories;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.adapter.VectorCostGoalAdapter;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.rl.RLTrajectoryPlanner;
import ch.ethz.idsc.owl.glc.rl.StandardRLTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.region.PolygonRegionRender;
import ch.ethz.idsc.owl.gui.ren.EtaRender;
import ch.ethz.idsc.owl.gui.ren.TrajectoryRender;
import ch.ethz.idsc.owl.gui.win.BaseFrame;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.region.BallRegion;
import ch.ethz.idsc.owl.math.region.PolygonRegion;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.qty.Quantity;

public class RLTrajectoryPlanner0Demo implements DemoInterface {
  private static final StateIntegrator STATE_INTEGRATOR = FixedStateIntegrator.create( //
      EulerIntegrator.INSTANCE, SingleIntegratorStateSpaceModel.INSTANCE, RationalScalar.of(4, 7), 1);
  private static final Tensor POLYGON = Tensors.matrixFloat(new float[][] { { 1, 0 }, { 1, -10 }, { 4, -10 }, { 4, 3 } });
  private static final PolygonRegion POLYGON_REGION = new PolygonRegion(POLYGON);

  static Optional<GlcNode> getBest() {
    Tensor slacks = Tensors.vector(5, 2);
    Tensor stateRoot = Tensors.vector(0, 0);
    Tensor stateGoal = Tensors.vector(5, 0);
    int n = 3;
    Tensor eta = Tensors.vector(n, n);
    final Scalar radius = RealScalar.of(Math.sqrt(2) / n);
    R2Flows r2Flows = new R2RationalFlows(RealScalar.ONE);
    Collection<Tensor> controls = r2Flows.getFlows(4);
    for (Tensor flow : controls)
      ExactTensorQ.require(flow);
    RegionWithDistance<Tensor> goalRegion = new BallRegion(stateGoal, radius);
    // the 1st cost penalizes distance of path with slack
    CostFunction distanceCost = new CostFunction() {
      @Override // from CostIncrementFunction
      public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
        return Vector2Norm.between(glcNode.stateTime().state(), Lists.getLast(trajectory).state()); // ||x_prev - x_next||
      }

      @Override // from HeuristicFunction
      public Scalar minCostToGoal(Tensor x) {
        return goalRegion.distance(x);
      }
    };
    // the 2nd cost penalizes membership in region
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(POLYGON_REGION);
    CostFunction regionCost = ConstraintViolationCost.of(plannerConstraint, Quantity.of(2, ""));
    // ---
    // the 3rd cost penalizes distance of path
    GoalInterface goalInterface = //
        new VectorCostGoalAdapter(Arrays.asList(distanceCost, regionCost), goalRegion);
    // ---
    StateTimeRaster stateTimeRaster = EtaRaster.state(eta);
    RLTrajectoryPlanner trajectoryPlanner = new StandardRLTrajectoryPlanner( //
        stateTimeRaster, STATE_INTEGRATOR, controls, EmptyPlannerConstraint.INSTANCE, goalInterface, slacks);
    trajectoryPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(1000);
    return trajectoryPlanner.getBest();
  }

  @Override // from DemoInterface
  public BaseFrame start() {
    Optional<GlcNode> optional = getBest();
    GlcNode goalNode = optional.get();
    // System.out.println(goalNode.merit());
    // System.out.println(goalNode.costFromRoot());
    // @SuppressWarnings("unused")
    // VectorScalar cost = (VectorScalar) goalNode.costFromRoot();
    // System.out.println(cost);
    // ---
    List<TrajectorySample> trajectory = GlcTrajectories.detailedTrajectoryTo(STATE_INTEGRATOR, goalNode);
    // ---
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    owlyAnimationFrame.addBackground(new PolygonRegionRender(POLYGON_REGION));
    TrajectoryRender trajectoryRender = new TrajectoryRender();
    trajectoryRender.trajectory(trajectory);
    owlyAnimationFrame.addBackground(trajectoryRender);
    Tensor eta = Tensors.vector(3, 3);
    owlyAnimationFrame.addBackground(new EtaRender(eta));
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new RLTrajectoryPlanner0Demo().start().jFrame.setVisible(true);
  }
}
