// code by astoll, ynager
package ch.ethz.idsc.owl.bot.rn.glc;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import ch.ethz.idsc.owl.bot.r2.R2Flows;
import ch.ethz.idsc.owl.bot.r2.R2RationalFlows;
import ch.ethz.idsc.owl.bot.rn.RnMinDistGoalManager;
import ch.ethz.idsc.owl.glc.adapter.ConstraintViolationCost;
import ch.ethz.idsc.owl.glc.adapter.EmptyObstacleConstraint;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
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
import ch.ethz.idsc.owl.gui.ren.DomainQueueMapRender;
import ch.ethz.idsc.owl.gui.ren.EdgeRender;
import ch.ethz.idsc.owl.gui.ren.EtaRender;
import ch.ethz.idsc.owl.gui.ren.SphericalRegionRender;
import ch.ethz.idsc.owl.gui.win.OwlyFrame;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.PolygonRegion;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;

public class RelaxedTrajectoryPlanner1Demo {
  // -------- slacks --------
  final Tensor slacks = Tensors.vector(2, 0);
  // -------- stateTimeRaster --------
  int n = 4;
  final Tensor eta = Tensors.vector(n, n);
  // -------- StateIntegrator --------
  final Scalar timeStep = RationalScalar.of(3, 7);
  final StateIntegrator stateIntegrator = FixedStateIntegrator.create(EulerIntegrator.INSTANCE, timeStep, 1);
  // -------- GoalInterface --------
  final Tensor stateGoal = Tensors.vector(5, 0);
  final Scalar radius = RealScalar.of(Math.sqrt(2) / n);
  final RegionWithDistance<Tensor> goalRegion = new SphericalRegion(stateGoal, radius);
  private final Tensor polygon = Tensors.matrixFloat(new float[][] { { 1, 0 }, { 1, -10 }, { 4, -10 }, { 4, 3 } });
  private final PolygonRegion polygonRegion = new PolygonRegion(polygon);

  public StandardRelaxedLexicographicPlanner createPlanner() {
    // -------- stateTimeRaster --------
    StateTimeRaster stateTimeRaster = EtaRaster.state(eta);
    // -------- controls --------
    R2Flows r2Flows = new R2RationalFlows(RealScalar.ONE);
    Collection<Flow> controls = r2Flows.getFlows(7);
    for (Flow flow : controls)
      ExactTensorQ.require(flow.getU());
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

  public static void main(String[] args) throws InterruptedException, ClassNotFoundException, IOException {
    // .start().jFrame.setVisible(true);
    RelaxedTrajectoryPlanner1Demo relaxedTrajectoryPlanner1Demo = new RelaxedTrajectoryPlanner1Demo();
    RelaxedTrajectoryPlanner relaxedTrajectoryPlanner = relaxedTrajectoryPlanner1Demo.createPlanner();
    Serialization.copy(relaxedTrajectoryPlanner);
    final Tensor stateRoot = Tensors.vector(0.1, 0.1);
    relaxedTrajectoryPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    RelaxedGlcExpand glcExpand = new RelaxedGlcExpand(relaxedTrajectoryPlanner);
    Timing timing = Timing.started();
    // glcExpand.findAny(1000);
    System.out.println("Execution Time: " + timing.seconds());
    OwlyFrame owlyFrame = OwlyGui.start();
    owlyFrame.addBackground(new PolygonRegionRender(relaxedTrajectoryPlanner1Demo.polygonRegion));
    owlyFrame.addBackground(new EtaRender(relaxedTrajectoryPlanner1Demo.eta));
    owlyFrame.addBackground(new DomainQueueMapRender(relaxedTrajectoryPlanner.getRelaxedDomainQueueMap().getMap(), relaxedTrajectoryPlanner1Demo.eta));
    owlyFrame.addBackground(new SphericalRegionRender((SphericalRegion) relaxedTrajectoryPlanner1Demo.goalRegion));
    {
      EdgeRender treeRender = new EdgeRender(1000, Color.BLUE);
      Collection<GlcNode> collection;
      collection = relaxedTrajectoryPlanner.getDomainMap().values();
      collection = RelaxedDebugUtils.allNodes(relaxedTrajectoryPlanner);
      treeRender.setCollection(collection);
      owlyFrame.addBackground(treeRender.getRender());
    }
    while (!relaxedTrajectoryPlanner.getBest().isPresent() && owlyFrame.jFrame.isVisible()) {
      glcExpand.findAny(40);
      owlyFrame.setGlc(relaxedTrajectoryPlanner);
      // animationWriter.append(owlyFrame.offscreen());
      Thread.sleep(1);
    }
    // Optional<GlcNode> optional = relaxedTrajectoryPlanner.getBest();
    // if (optional.isPresent()) {
    // System.out.println(optional.get().merit());
    // Iterator<GlcNode> bestGoalNodes = relaxedTrajectoryPlanner.getAllNodesInGoal().iterator();
    // while (bestGoalNodes.hasNext()) {
    // GlcNode goalNode = bestGoalNodes.next();
    // System.out.println(goalNode.merit());
    // // System.out.println(goalNode.costFromRoot());
    // List<TrajectorySample> trajectory = GlcTrajectories.detailedTrajectoryTo(stateIntegrator, goalNode);
    // TrajectoryRender trajectoryRender = new TrajectoryRender();
    // trajectoryRender.trajectory(trajectory);
    // owlyAnimationFrame.addBackground(trajectoryRender);
    // }
    // }
    // // ---
    // RelaxedDebugUtils.globalQueueSubsetOfQueuesInDomainMap(relaxedTrajectoryPlanner);
    // RelaxedDebugUtils.nodeAmountCompare(relaxedTrajectoryPlanner);
    // NodesAssert.allLeaf(relaxedTrajectoryPlanner.getQueue());
    // RelaxedDebugUtils.closeMatchesCheck(rlPlanner);
  }
}