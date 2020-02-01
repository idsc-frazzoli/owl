// code by astoll, ynager
package ch.ethz.idsc.owl.bot.rn.glc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.r2.R2Flows;
import ch.ethz.idsc.owl.bot.r2.R2RationalFlows;
import ch.ethz.idsc.owl.bot.rn.RnMinDistGoalManager;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.data.tree.NodesAssert;
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
import ch.ethz.idsc.owl.glc.rl2.RelaxedDebugUtils;
import ch.ethz.idsc.owl.glc.rl2.RelaxedGlcExpand;
import ch.ethz.idsc.owl.glc.rl2.RelaxedTrajectoryPlanner;
import ch.ethz.idsc.owl.glc.rl2.StandardRelaxedLexicographicPlanner;
import ch.ethz.idsc.owl.gui.region.PolygonRegionRender;
import ch.ethz.idsc.owl.gui.ren.BallRegionRender;
import ch.ethz.idsc.owl.gui.ren.DomainQueueMapRender;
import ch.ethz.idsc.owl.gui.ren.EdgeRenders;
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
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;

public class RelaxedTrajectoryPlanner0Demo implements DemoInterface {
  // -------- slacks --------
  final Tensor slacks = Tensors.vector(2, 0);
  // -------- stateTimeRaster --------
  int n = 2;
  final Tensor eta = Tensors.vector(n, n);
  // -------- StateIntegrator --------
  final Scalar timeStep = RationalScalar.of(4, 7);
  final StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
      EulerIntegrator.INSTANCE, SingleIntegratorStateSpaceModel.INSTANCE, timeStep, 1);
  // -------- GoalInterface --------
  final Tensor stateGoal = Tensors.vector(5, 0);
  final Scalar radius = RealScalar.of(Math.sqrt(2) / n);
  final RegionWithDistance<Tensor> goalRegion = new BallRegion(stateGoal, radius);
  private final Tensor polygon = Tensors.matrixFloat(new float[][] { { 1, 0 }, { 1, -10 }, { 4, -10 }, { 4, 3 } });
  private final PolygonRegion polygonRegion = new PolygonRegion(polygon);

  public StandardRelaxedLexicographicPlanner createPlanner() {
    // -------- stateTimeRaster --------
    StateTimeRaster stateTimeRaster = EtaRaster.state(eta);
    // -------- controls --------
    R2Flows r2Flows = new R2RationalFlows(RealScalar.ONE);
    Collection<Tensor> controls = r2Flows.getFlows(7);
    for (Tensor flow : controls)
      ExactTensorQ.require(flow);
    // -------- GoalInterface --------
    // --
    CostFunction distanceCost = new RnMinDistGoalManager(goalRegion);
    // --
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(polygonRegion);
    CostFunction regionCost = ConstraintViolationCost.of(plannerConstraint, Quantity.of(2, ""));
    // ---
    GoalInterface goalInterface = //
        new VectorCostGoalAdapter(Arrays.asList(distanceCost, regionCost), goalRegion);
    // -------------------------------
    return new StandardRelaxedLexicographicPlanner( //
        stateTimeRaster, stateIntegrator, controls, EmptyObstacleConstraint.INSTANCE, goalInterface, slacks);
  }

  @Override // from DemoInterface
  public BaseFrame start() {
    RelaxedTrajectoryPlanner relaxedTrajectoryPlanner = createPlanner();
    final Tensor stateRoot = Tensors.vector(0.1, 0.1);
    relaxedTrajectoryPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    RelaxedGlcExpand glcExpand = new RelaxedGlcExpand(relaxedTrajectoryPlanner);
    Timing timing = Timing.started();
    // glcExpand.findAny(1000);
    glcExpand.untilOptimal(1000);
    System.out.println("Execution Time: " + timing.seconds());
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    owlyAnimationFrame.addBackground(new PolygonRegionRender(polygonRegion));
    owlyAnimationFrame.addBackground(new EtaRender(eta));
    owlyAnimationFrame.addBackground(DomainQueueMapRender.of(relaxedTrajectoryPlanner.getRelaxedDomainQueueMap().getMap(), eta));
    owlyAnimationFrame.addBackground(new BallRegionRender((BallRegion) goalRegion));
    owlyAnimationFrame.addBackground(EdgeRenders.of(relaxedTrajectoryPlanner));
    Optional<GlcNode> optional = relaxedTrajectoryPlanner.getBest();
    if (optional.isPresent()) {
      System.out.println(optional.get().merit());
      Iterator<GlcNode> bestGoalNodes = relaxedTrajectoryPlanner.getAllNodesInGoal().iterator();
      while (bestGoalNodes.hasNext()) {
        GlcNode goalNode = bestGoalNodes.next();
        System.out.println(goalNode.merit());
        // System.out.println(goalNode.costFromRoot());
        List<TrajectorySample> trajectory = GlcTrajectories.detailedTrajectoryTo(stateIntegrator, goalNode);
        TrajectoryRender trajectoryRender = new TrajectoryRender();
        trajectoryRender.trajectory(trajectory);
        owlyAnimationFrame.addBackground(trajectoryRender);
      }
    }
    // ---
    RelaxedDebugUtils.globalQueueSubsetOfQueuesInDomainMap(relaxedTrajectoryPlanner);
    RelaxedDebugUtils.nodeAmountCompare(relaxedTrajectoryPlanner);
    NodesAssert.allLeaf(relaxedTrajectoryPlanner.getQueue());
    // RelaxedDebugUtils.closeMatchesCheck(rlPlanner);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new RelaxedTrajectoryPlanner0Demo().start().jFrame.setVisible(true);
  }
}