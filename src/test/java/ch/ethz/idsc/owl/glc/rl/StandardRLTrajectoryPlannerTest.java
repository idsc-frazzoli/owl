// code by ynager
package ch.ethz.idsc.owl.glc.rl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.r2.R2Flows;
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
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.owl.math.VectorScalars;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.PolygonRegion;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Ramp;
import junit.framework.TestCase;

public class StandardRLTrajectoryPlannerTest extends TestCase {
  public void testSimple() {
    final Tensor stateRoot = Tensors.vector(0, 0);
    final Tensor stateGoal = Tensors.vector(5, 0);
    final Scalar radius = DoubleScalar.of(.05);
    final Tensor slacks = Tensors.vector(1, 0, 0);
    // ---
    Tensor eta = Tensors.vector(8, 8);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create(EulerIntegrator.INSTANCE, RationalScalar.of(1, 5), 5);
    R2Flows r2Config = new R2Flows(RealScalar.ONE);
    Collection<Flow> controls = r2Config.getFlows(36);
    RegionWithDistance<Tensor> goalRegion = new SphericalRegion(stateGoal, radius);
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
    Tensor polygon = Tensors.matrixFloat(new float[][] { { 1, 0 }, { 1, -10 }, { 4, -10 }, { 4, 3 } });
    PolygonRegion polygonRegion = new PolygonRegion(polygon);
    PlannerConstraint regionConstraint = RegionConstraints.timeInvariant(polygonRegion);
    CostFunction regionCost = ConstraintViolationCost.of(regionConstraint);
    // ---
    GoalInterface goalInterface = new VectorCostGoalAdapter(Arrays.asList(distanceCost, regionCost, distanceCost), goalRegion);
    // ---
    StateTimeRaster stateTimeRaster = EtaRaster.state(eta);
    RLTrajectoryPlanner trajectoryPlanner = new StandardRLTrajectoryPlanner( //
        stateTimeRaster, stateIntegrator, controls, EmptyObstacleConstraint.INSTANCE, goalInterface, slacks);
    assertEquals(trajectoryPlanner.getStateIntegrator(), stateIntegrator);
    trajectoryPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    GlcRLExpand glcExpand = new GlcRLExpand(trajectoryPlanner);
    glcExpand.untilOptimal(1000);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    assertTrue(optional.isPresent());
    GlcNode goalNode = optional.get(); // <- throws exception if
    VectorScalar cost = (VectorScalar) goalNode.costFromRoot();
    // System.out.println("best: " + cost + " hash: " + goalNode.hashCode());
    Scalar lowerBound = Ramp.of(Norm._2.ofVector(stateGoal.subtract(stateRoot)).subtract(radius));
    if (Scalars.lessThan(cost.vector().Get(0), lowerBound))
      throw TensorRuntimeException.of(cost, lowerBound);
    // ---
    GlcNode minCostNode = StaticHelper.getMin(trajectoryPlanner.reachingSet.collection(), 0);
    Tensor minComp = VectorScalars.vector(minCostNode.merit()); // min cost component in goal
    System.out.println("minComp=" + minComp);
    Scalar upperBound = minComp.Get(0).add(slacks.Get(0));
    assertTrue(Scalars.lessEquals(cost.vector().Get(0), upperBound));
  }
}
