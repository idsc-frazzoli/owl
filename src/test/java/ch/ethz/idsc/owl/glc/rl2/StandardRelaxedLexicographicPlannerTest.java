// code by astoll
package ch.ethz.idsc.owl.glc.rl2;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.owl.bot.r2.R2Flows;
import ch.ethz.idsc.owl.bot.r2.R2RationalFlows;
import ch.ethz.idsc.owl.data.Lists;
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
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.PolygonRegion;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.VectorScalar;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Norm;
import junit.framework.TestCase;

public class StandardRelaxedLexicographicPlannerTest extends TestCase {
  public StandardRelaxedLexicographicPlanner setupPlanner() {
    // -------- slacks --------
    final Tensor slacks = Tensors.vector(5, 2);
    // -------- stateTimeRaster --------
    int n = 2;
    final Tensor eta = Tensors.vector(n, n);
    StateTimeRaster stateTimeRaster = EtaRaster.state(eta);
    // -------- StateIntegrator --------
    final StateIntegrator STATE_INTEGRATOR = FixedStateIntegrator.create(EulerIntegrator.INSTANCE, RationalScalar.of(4, 7), 1);
    // -------- controls --------
    R2Flows r2Flows = new R2RationalFlows(RealScalar.ONE);
    Collection<Flow> controls = r2Flows.getFlows(4);
    for (Flow flow : controls)
      ExactTensorQ.require(flow.getU());
    // -------- GoalInterface --------
    final Tensor stateGoal = Tensors.vector(5, 0);
    final Scalar radius = RealScalar.of(Math.sqrt(2) / n);
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
    final Tensor POLYGON = Tensors.matrixFloat(new float[][] { { 1, 0 }, { 1, -10 }, { 4, -10 }, { 4, 3 } });
    final PolygonRegion POLYGON_REGION = new PolygonRegion(POLYGON);
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(POLYGON_REGION);
    CostFunction regionCost = ConstraintViolationCost.of(plannerConstraint, Quantity.of(2, ""));
    // ---
    GoalInterface goalInterface = //
        new VectorCostGoalAdapter(Arrays.asList(distanceCost, regionCost), goalRegion);
    // -------------------------------
    return new StandardRelaxedLexicographicPlanner( //
        stateTimeRaster, STATE_INTEGRATOR, controls, EmptyObstacleConstraint.INSTANCE, goalInterface, slacks);
  }

  public void testSimple() {
    RelaxedTrajectoryPlanner rlPlanner = setupPlanner();
    assertTrue(rlPlanner.getStateIntegrator() != null);
    assertTrue(rlPlanner.getQueue().isEmpty());
    assertTrue(rlPlanner.getBest() != null);
    assertTrue(rlPlanner.getNodesInDomainQueueMap().isEmpty());
    final Tensor stateRoot = Tensors.vector(0, 0);
    rlPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    assertFalse(rlPlanner.getQueue().isEmpty());
    System.out.println(rlPlanner.getNodesInDomainQueueMap().size());
    rlPlanner.pollNext();
    assertTrue(rlPlanner.getQueue().isEmpty());
    assertFalse(rlPlanner.getNodesInDomainQueueMap().isEmpty());
  }

  public void testAddToGlobal() {
    RelaxedTrajectoryPlanner rlPlanner = setupPlanner();
    final Tensor state = Tensors.vector(10, 10);
    GlcNode node1 = GlcNode.of(null, new StateTime(state, RealScalar.ZERO), VectorScalar.of(1, 2), VectorScalar.of(0, 0));
    GlcNode node2 = GlcNode.of(null, new StateTime(state, RealScalar.ZERO), VectorScalar.of(2, 1), VectorScalar.of(0, 0));
    rlPlanner.addToGlobalQueue(node1);
    rlPlanner.addToGlobalQueue(node2);
    assertTrue(rlPlanner.getQueue().contains(node1));
    assertTrue(rlPlanner.getQueue().contains(node2));
  }
}
