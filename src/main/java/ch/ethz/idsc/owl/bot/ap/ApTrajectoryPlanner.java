package ch.ethz.idsc.owl.bot.ap;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.glc.adapter.EmptyObstacleConstraint;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.math.StateSpaceModel;
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
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.qty.Degree;

public class ApTrajectoryPlanner {
  /* Creation of ApStateSpaceModel instance */
  static StateSpaceModel stateSpaceModel = ApStateSpaceModel.INSTANCE;
  /* Setting up parameters for the ApComboRegion
   * Note: GOAL and RADIUS_VECTOR are 3D, since x is omitted in ApComboRegion */
  final static Tensor GOAL = Tensors.vector(0, 10, 0.1); // goal = {zCenter,vCenter, gammaCenter}
  final static Tensor RADIUS_VECTOR = Tensors.of(RealScalar.of(5), RealScalar.of(10), Degree.of(5)); // radius_vector = {zRadius,vRadius, GammaRadius}
  /* Creation of control flows */
  final static Scalar MAX_AOA = ApStateSpaceModel.MAX_AOA;
  final static int THRUST_PARTIONING = 4;
  final static Tensor THRUSTS = Subdivide.of(RealScalar.ZERO, ApStateSpaceModel.MAX_THRUST, THRUST_PARTIONING);
  final static int FLOWRES = 5;
  final static FlowsInterface AP_FLOWS = ApFlows.of(stateSpaceModel, MAX_AOA, THRUSTS);
  /* Setting up integrator */
  static final Integrator INTEGRATOR = RungeKutta45Integrator.INSTANCE;
  /* Setting up Time Raster */
  final static Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(2), RealScalar.of(2), RealScalar.of(1), Degree.of(10).reciprocal()).unmodifiable();

  static protected StateTimeRaster stateTimeRaster() {
    return new EtaRaster(PARTITIONSCALE, StateTimeTensorFunction.state(ApWrap.INSTANCE::represent));
  }

  /** This function creates an object of the StandardTrajectoryPlanner class.
   * All necessary parameters are defined in {@link ApTrajectoryPlanner}
   * 
   * @return New StandardTrajectoryPlanner for airplane simulation */
  static StandardTrajectoryPlanner ApStandardTrajectoryPlanner() {
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        INTEGRATOR, RationalScalar.of(1, 7), 10);
    Collection<Flow> controls = AP_FLOWS.getFlows(FLOWRES);
    ApComboRegion apComboRegion = ApComboRegion.createApRegion(GOAL, RADIUS_VECTOR);
    ApMinTimeGoalManager apMinTimeGoalManager = new ApMinTimeGoalManager(apComboRegion, ApStateSpaceModel.MAX_SPEED);
    GoalInterface goalInterface = apMinTimeGoalManager.getGoalInterface();
    return new StandardTrajectoryPlanner(stateTimeRaster(), stateIntegrator, controls, EmptyObstacleConstraint.INSTANCE, goalInterface);
  }
}
