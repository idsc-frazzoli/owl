// code by jph
package ch.ethz.idsc.owl.bot.lv;

import java.util.Collection;

import ch.ethz.idsc.owl.glc.adapter.EmptyObstacleConstraint;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.HeuristicAssert;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.math.StateTimeTensorFunction;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.owl.math.region.EllipsoidRegion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Log;
import junit.framework.TestCase;

public class LvDemoTest extends TestCase {
  public void testPlan() {
    for (int index = 0; index < 5; ++index) {
      Tensor eta = Tensors.vector(10, 10);
      StateSpaceModel stateSpaceModel = LvStateSpaceModel.of(1, 2);
      StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
          RungeKutta45Integrator.INSTANCE, stateSpaceModel, RationalScalar.of(1, 30), 4);
      Collection<Flow> controls = LvControls.create(stateSpaceModel, 2);
      EllipsoidRegion ellipsoidRegion = new EllipsoidRegion(Tensors.vector(2, 1), Tensors.vector(0.1, 0.1));
      GoalInterface goalInterface = new LvGoalInterface(ellipsoidRegion);
      // ---
      StateTimeRaster stateTimeRaster = new EtaRaster(eta, StateTimeTensorFunction.state(Log::of));
      TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
          stateTimeRaster, stateIntegrator, controls, EmptyObstacleConstraint.INSTANCE, goalInterface);
      // ---
      // trajectoryPlanner.represent = StateTimeTensorFunction.state(Log::of);
      trajectoryPlanner.insertRoot(new StateTime(Tensors.vector(2, 0.3), RealScalar.ZERO));
      GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
      glcExpand.findAny(10_000);
      if (glcExpand.getExpandCount() < 9800) {
        HeuristicAssert.check(trajectoryPlanner);
        // TrajectoryPlannerConsistency.check(trajectoryPlanner);
        return;
      }
    }
    fail();
  }
}
