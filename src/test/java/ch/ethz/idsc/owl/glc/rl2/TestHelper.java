// code by astoll
package ch.ethz.idsc.owl.glc.rl2;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.owl.bot.r2.R2Flows;
import ch.ethz.idsc.owl.bot.r2.R2RationalFlows;
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
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

public enum TestHelper {
  ;
  /* package */ static RelaxedTrajectoryPlanner createPlanner() {
    // -------- slacks --------
    final Tensor slacks = Tensors.vector(5, 2);
    // -------- stateTimeRaster --------
    int n = 2;
    final Tensor eta = Tensors.vector(n, n);
    StateTimeRaster stateTimeRaster = EtaRaster.state(eta);
    // -------- StateIntegrator --------
    Scalar timeStep = RationalScalar.of(4, 7);
    final StateIntegrator stateIntegrator = FixedStateIntegrator.create(EulerIntegrator.INSTANCE, timeStep, 1);
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
        // Scalar scalar = Norm._2.between(glcNode.stateTime().state(), Lists.getLast(trajectory).state());
        return timeStep;
      }

      @Override // from HeuristicFunction
      public Scalar minCostToGoal(Tensor x) {
        return goalRegion.distance(x);
      }
    };
    // --
    Tensor polygon = Tensors.matrixFloat(new float[][] { { 1, 0 }, { 1, -10 }, { 4, -10 }, { 4, 3 } });
    PolygonRegion polygonRegion = new PolygonRegion(polygon);
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(polygonRegion);
    CostFunction regionCost = ConstraintViolationCost.of(plannerConstraint, Quantity.of(2, ""));
    // ---
    GoalInterface goalInterface = //
        new VectorCostGoalAdapter(Arrays.asList(distanceCost, regionCost), goalRegion);
    // -------------------------------
    return new StandardRelaxedLexicographicPlanner( //
        stateTimeRaster, stateIntegrator, controls, EmptyObstacleConstraint.INSTANCE, goalInterface, slacks);
  }
}
