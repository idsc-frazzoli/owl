// code by ynager
package ch.ethz.idsc.owl.glc.rl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.r2.R2Flows;
import ch.ethz.idsc.owl.bot.r2.R2RationalFlows;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.glc.adapter.ConstraintViolationCost;
import ch.ethz.idsc.owl.glc.adapter.EmptyPlannerConstraint;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.adapter.VectorCostGoalAdapter;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcNodes;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.owl.math.VectorScalars;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.region.BallRegion;
import ch.ethz.idsc.owl.math.region.PolygonRegion;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class StandardRLTrajectoryPlannerTest extends TestCase {
  private static GlcNode _withSlack(Tensor slacks) {
    final Tensor stateRoot = Tensors.vector(0, 0);
    final Tensor stateGoal = Tensors.vector(5, 0);
    // ---
    int n = 8;
    Tensor eta = Tensors.vector(n, n);
    // radius is chosen so that goal region contains at least one domain entirely
    final Scalar radius = RealScalar.of(Math.sqrt(2) / n);
    StateIntegrator stateIntegrator = //
        FixedStateIntegrator.create(EulerIntegrator.INSTANCE, SingleIntegratorStateSpaceModel.INSTANCE, RationalScalar.of(1, 5), 5);
    R2Flows r2Flows = new R2RationalFlows(RealScalar.ONE);
    Collection<Tensor> controls = r2Flows.getFlows(4);
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
    Tensor polygon = Tensors.matrixFloat(new float[][] { { 1, 0 }, { 1, -10 }, { 4, -10 }, { 4, 3 } });
    PolygonRegion polygonRegion = new PolygonRegion(polygon);
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(polygonRegion);
    CostFunction regionCost = ConstraintViolationCost.of(plannerConstraint, Quantity.of(1, ""));
    // ---
    // the 3rd cost penalizes distance of path
    GoalInterface goalInterface = //
        new VectorCostGoalAdapter(Arrays.asList(distanceCost, regionCost, distanceCost), goalRegion);
    // ---
    StateTimeRaster stateTimeRaster = EtaRaster.state(eta);
    RLTrajectoryPlanner trajectoryPlanner = new StandardRLTrajectoryPlanner( //
        stateTimeRaster, stateIntegrator, controls, EmptyPlannerConstraint.INSTANCE, goalInterface, slacks);
    assertEquals(trajectoryPlanner.getStateIntegrator(), stateIntegrator);
    trajectoryPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.untilOptimal(1000);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    assertTrue(optional.isPresent()); // guarantee optimal solution exists
    GlcNode goalNode = optional.get();
    VectorScalar cost = (VectorScalar) goalNode.costFromRoot();
    // System.out.println("best: " + cost + " hash: " + goalNode.hashCode());
    Scalar lowerBound = goalRegion.distance(stateRoot);
    // System.out.println("lowerBound=" + lowerBound);
    Scalar marginDist = cost.vector().Get(0).subtract(lowerBound);
    // System.out.println("marginDist=" + marginDist);
    Sign.requirePositiveOrZero(marginDist);
    // ---
    GlcNode minCostNode = StaticHelper.getMin(trajectoryPlanner.reachingSet.collection(), 0);
    Tensor minComp = VectorScalars.vector(minCostNode.merit()); // min cost component in goal
    // System.out.println("minComp=" + minComp);
    Scalar upperBound = minComp.Get(0).add(slacks.Get(0));
    assertTrue(Scalars.lessEquals(cost.vector().Get(0), upperBound));
    // List<StateTime> pathFromRootTo =
    GlcNodes.getPathFromRootTo(goalNode);
    // pathFromRootTo.stream().map(StateTime::toInfoString).forEach(System.out::println);
    return goalNode;
  }

  public void testFour() {
    GlcNode goalNode = _withSlack(Tensors.vector(4, 0, 0));
    Tensor costFromRoot = VectorScalars.vector(goalNode.costFromRoot());
    assertEquals(costFromRoot, Tensors.vector(9, 2, 9));
  }

  public void testTwo() {
    GlcNode goalNode = _withSlack(Tensors.vector(2, 0, 0));
    Tensor costFromRoot = VectorScalars.vector(goalNode.costFromRoot());
    assertEquals(costFromRoot, Tensors.vector(7, 3, 7));
  }

  public void testZeroEbbes() {
    GlcNode goalNode = _withSlack(Tensors.vector(1.3, 0, 0));
    Tensor costFromRoot = VectorScalars.vector(goalNode.costFromRoot());
    assertEquals(costFromRoot, Tensors.vector(5, 4, 5));
  }

  public void testZero() {
    GlcNode bestNode = _withSlack(Tensors.vector(0, 0, 0));
    Tensor costFromRoot = VectorScalars.vector(bestNode.costFromRoot());
    assertEquals(costFromRoot, Tensors.vector(5, 4, 5));
  }
}
