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
  final static Scalar MAX_AOA = ApStateSpaceModel.MAX_AOA;
  static StateSpaceModel stateSpaceModel = ApStateSpaceModel.INSTANCE;
  final static int THRUST_PARTIONING = 160;
  final static Tensor THRUSTS = Subdivide.of(RealScalar.ZERO, ApStateSpaceModel.MAX_THRUST, THRUST_PARTIONING);
  final static Tensor GOAL = Tensors.vector(10000, 0, 0, 0); // goal {x,z,velocity, pathAngle}
  final static Tensor RADIUS_VECTOR = Tensors.of(RealScalar.of(5), RealScalar.of(5), RealScalar.of(10), Degree.of(1));
  final static int FLOWRES = 7;
  final static FlowsInterface AP_FLOWS = ApFlows.of(stateSpaceModel, MAX_AOA, THRUSTS);
  final static Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(5), RealScalar.of(5), RealScalar.of(5), Degree.of(1)).unmodifiable();
  static final Integrator INTEGRATOR = RungeKutta45Integrator.INSTANCE;

  static protected StateTimeRaster stateTimeRaster() {
    return new EtaRaster(PARTITIONSCALE, StateTimeTensorFunction.state(ApWrap.INSTANCE::represent));
  }

  static StandardTrajectoryPlanner ApStandardTrajectoryPlanner() {
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        INTEGRATOR, RationalScalar.of(1, 10), 5);
    Collection<Flow> controls = AP_FLOWS.getFlows(FLOWRES);
    ApComboRegion apComboRegion = ApComboRegion.spherical(GOAL, RADIUS_VECTOR);
    ApMinTimeGoalManager apMinTimeGoalManager = new ApMinTimeGoalManager(apComboRegion, ApStateSpaceModel.MAX_SPEED);
    GoalInterface goalInterface = apMinTimeGoalManager.getGoalInterface();
    return new StandardTrajectoryPlanner(stateTimeRaster(), stateIntegrator, controls, EmptyObstacleConstraint.INSTANCE, goalInterface);
  }
}
