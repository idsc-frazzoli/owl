// Code by astoll
package ch.ethz.idsc.owl.bot.ap;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.glc.adapter.EmptyObstacleConstraint;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.math.StateTimeTensorFunction;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Degree;

public class ApPlanningEvaluation {
  static final Scalar MAX_AOA = ApStateSpaceModel.MAX_AOA;
  static final Tensor THRUSTS = Tensors.vector(0, 10);
  // TODO Partioning
  static final Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(2), RealScalar.of(2), Degree.of(10).reciprocal(), RealScalar.of(10)).unmodifiable();
  // State Integrator
  // TODO StepSize and TrajectorySize -> 2nd and 3rd input to stateIntegrator
  private static final Integrator INTEGRATOR = RungeKutta45Integrator.INSTANCE;
  StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
      INTEGRATOR, RationalScalar.of(1, 10), 5);
  // Controls
  static final int FLOWRES = 7;
  static final FlowsInterface AP_FLOWS = ApFlows.of(MAX_AOA, THRUSTS);
  final Collection<Flow> controls = AP_FLOWS.getFlows(FLOWRES);
  // Planner Constraints
  // TODO Add planner constraints, if there some
  // Goal Interface
  static final Tensor GOAL = Tensors.vector(0, 0, 1000, 0); // goal {velocity, pathAngle, x, z}
  static final Tensor RADIUS_VECTOR = Tensors.vector(5, 1, 10, 10);
  ApComboRegion apComboRegion = ApComboRegion.spherical(GOAL, RADIUS_VECTOR);
  static final Scalar MAX_SPEED = RealScalar.of(83);
  ApMinTimeGoalManager apMinTimeGoalManager = new ApMinTimeGoalManager(apComboRegion, MAX_SPEED);
  GoalInterface goalInterface = apMinTimeGoalManager.getGoalInterface();
  public StandardTrajectoryPlanner standardTrajectoryPlanner = new StandardTrajectoryPlanner(//
      stateTimeRaster(), stateIntegrator, controls, EmptyObstacleConstraint.INSTANCE, goalInterface);

  protected StateTimeRaster stateTimeRaster() {
    return new EtaRaster(PARTITIONSCALE, StateTimeTensorFunction.state(ApWrap.INSTANCE::represent));
  }

  public static void main(String[] args) {
    System.out.println("Planning...");
  }
}
