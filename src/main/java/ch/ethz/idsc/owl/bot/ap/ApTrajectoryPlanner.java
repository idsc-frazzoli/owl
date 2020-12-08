// code by astoll
package ch.ethz.idsc.owl.bot.ap;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.math.StateTimeTensorFunction;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta4Integrator;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.qty.Degree;

/* package */ class ApTrajectoryPlanner {
  /* Setting up parameters for the ApComboRegion
   * Note: GOAL and RADIUS_VECTOR are 3D, since x is omitted in ApComboRegion */
  final static Tensor GOAL = Tensors.vector(5, 60, 0); // goal = {zCenter, vCenter, gammaCenter}
  final static Tensor RADIUS_VECTOR = Tensors.of(RealScalar.of(5), RealScalar.of(200), Degree.of(50)); // radius_vector = {zRadius, vRadius, GammaRadius}
  /* Creation of control flows */
  final static Scalar MAX_AOA = ApStateSpaceModel.MAX_AOA;
  final static int THRUST_PARTIONING = 3;
  final static Tensor THRUSTS = Subdivide.of( //
      ApStateSpaceModel.MAX_THRUST.zero(), //
      ApStateSpaceModel.MAX_THRUST, //
      THRUST_PARTIONING);
  final static int FLOWRES = 2;
  final static FlowsInterface AP_FLOWS = ApFlows.of(MAX_AOA, THRUSTS);
  /* Setting up integrator */
  static final Integrator INTEGRATOR = RungeKutta4Integrator.INSTANCE;
  /* Setting up Time Raster */
  final static Tensor PARTITION_SCALE = Tensors.of(RealScalar.ONE, RealScalar.ONE, RealScalar.ONE, Degree.of(1)).unmodifiable();

  static protected StateTimeRaster stateTimeRaster() {
    return new EtaRaster(PARTITION_SCALE, StateTimeTensorFunction.state(ApWrap.INSTANCE::represent));
  }

  /** This function creates an object of the StandardTrajectoryPlanner class.
   * All necessary parameters are defined in {@link ApTrajectoryPlanner}
   * 
   * @return New StandardTrajectoryPlanner for airplane simulation */
  static StandardTrajectoryPlanner apStandardTrajectoryPlanner() {
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        INTEGRATOR, ApStateSpaceModel.INSTANCE, RationalScalar.of(1, 5), 3);
    Collection<Tensor> controls = AP_FLOWS.getFlows(FLOWRES);
    ApComboRegion apComboRegion = ApComboRegion.createApRegion(GOAL, RADIUS_VECTOR);
    ApMinTimeGoalManager apMinTimeGoalManager = new ApMinTimeGoalManager(apComboRegion, ApStateSpaceModel.Z_DOT_FLIGHT_MAX);
    GoalInterface goalInterface = apMinTimeGoalManager.getGoalInterface();
    PlannerConstraint apPlannerConstraint = ApPlannerConstraint.INSTANCE;
    return new StandardTrajectoryPlanner(stateTimeRaster(), stateIntegrator, controls, apPlannerConstraint, goalInterface);
  }
}
