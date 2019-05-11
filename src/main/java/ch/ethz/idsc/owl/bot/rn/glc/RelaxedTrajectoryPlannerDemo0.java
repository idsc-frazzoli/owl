// code by astoll, ynager
package ch.ethz.idsc.owl.bot.rn.glc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.r2.R2Flows;
import ch.ethz.idsc.owl.bot.r2.R2RationalFlows;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.glc.adapter.ConstraintViolationCost;
import ch.ethz.idsc.owl.glc.adapter.EmptyObstacleConstraint;
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
import ch.ethz.idsc.owl.glc.rl2.DebugUtils;
import ch.ethz.idsc.owl.glc.rl2.RelaxedDebugUtils;
import ch.ethz.idsc.owl.glc.rl2.RelaxedTrajectoryPlanner;
import ch.ethz.idsc.owl.glc.rl2.StandardRelaxedLexicographicPlanner;
import ch.ethz.idsc.owl.gui.region.PolygonRegionRender;
import ch.ethz.idsc.owl.gui.ren.EtaRender;
import ch.ethz.idsc.owl.gui.ren.TrajectoryRender;
import ch.ethz.idsc.owl.gui.win.BaseFrame;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.PolygonRegion;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
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
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Norm;

public class RelaxedTrajectoryPlannerDemo0 implements DemoInterface {
  // -------- slacks --------
  final Tensor slacks = Tensors.vector(2, 0);
  // -------- stateTimeRaster --------
  int n = 2;
  final Tensor eta = Tensors.vector(n, n);
  // -------- StateIntegrator --------
  final StateIntegrator STATE_INTEGRATOR = FixedStateIntegrator.create(EulerIntegrator.INSTANCE, RationalScalar.of(4, 7), 1);
  // -------- GoalInterface --------
  final Tensor stateGoal = Tensors.vector(5, 0);
  final Scalar radius = RealScalar.of(Math.sqrt(2) / n);
  private final Tensor POLYGON = Tensors.matrixFloat(new float[][] { { 1, 0 }, { 1, -10 }, { 4, -10 }, { 4, 3 } });
  private final PolygonRegion POLYGON_REGION = new PolygonRegion(POLYGON);

  public StandardRelaxedLexicographicPlanner setupPlanner() {
    // -------- stateTimeRaster --------
    StateTimeRaster stateTimeRaster = EtaRaster.state(eta);
    // -------- controls --------
    R2Flows r2Flows = new R2RationalFlows(RealScalar.ONE);
    Collection<Flow> controls = r2Flows.getFlows(4);
    for (Flow flow : controls)
      ExactTensorQ.require(flow.getU());
    // -------- GoalInterface --------
    RegionWithDistance<Tensor> goalRegion = new SphericalRegion(stateGoal, radius);
    // --
    CostFunction distanceCost = new CostFunction() {
      @Override // from CostIncrementFunction
      public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
        return Norm._2.between(glcNode.stateTime().state(), Lists.getLast(trajectory).state()); // ||x_prev - x_next||
      }

      @Override // from HeuristicFunction
      public Scalar minCostToGoal(Tensor x) {
        return goalRegion.distance(x);
      }
    };
    // --
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(POLYGON_REGION);
    CostFunction regionCost = ConstraintViolationCost.of(plannerConstraint, Quantity.of(2, ""));
    // ---
    GoalInterface goalInterface = //
        new VectorCostGoalAdapter(Arrays.asList(distanceCost, regionCost), goalRegion);
    // -------------------------------
    return new StandardRelaxedLexicographicPlanner( //
        stateTimeRaster, STATE_INTEGRATOR, controls, EmptyObstacleConstraint.INSTANCE, goalInterface, slacks);
  }

  @Override // from DemoInterface
  public BaseFrame start() {
    RelaxedTrajectoryPlanner rlPlanner = setupPlanner();
    final Tensor stateRoot = Tensors.vector(0, 0);
    rlPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(rlPlanner);
    Timing timing = Timing.started();
    glcExpand.findAny(1000);
    // glcExpand.isOptimal();
    System.out.println(timing.seconds());
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    owlyAnimationFrame.addBackground(new PolygonRegionRender(POLYGON_REGION));
    owlyAnimationFrame.addBackground(new EtaRender(eta));
    Optional<GlcNode> optional = rlPlanner.getBest();
    if (optional.isPresent()) {
      Iterator<GlcNode> bestGoalNodes = rlPlanner.getAllNodesInGoal().iterator();
      while (bestGoalNodes.hasNext()) {
        GlcNode goalNode = bestGoalNodes.next();
        System.out.println(goalNode.merit());
        System.out.println(goalNode.costFromRoot());
        List<TrajectorySample> trajectory = GlcTrajectories.detailedTrajectoryTo(STATE_INTEGRATOR, goalNode);
        TrajectoryRender trajectoryRender = new TrajectoryRender();
        trajectoryRender.trajectory(trajectory);
        owlyAnimationFrame.addBackground(trajectoryRender);
      }
    }
    // ---
    RelaxedDebugUtils.globalQueueSubsetOfQueuesInDomainMap(rlPlanner);
    RelaxedDebugUtils.nodeAmountCompare(rlPlanner);
    DebugUtils.assertNoLeaves(rlPlanner.getQueue());
    RelaxedDebugUtils.closeMatchesCheck(rlPlanner);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new RelaxedTrajectoryPlannerDemo0().start().jFrame.setVisible(true);
  }
}