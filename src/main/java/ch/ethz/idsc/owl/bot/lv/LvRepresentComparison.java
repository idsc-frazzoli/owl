// code by jph
package ch.ethz.idsc.owl.bot.lv;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.Expand;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.EmptyPlannerConstraint;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.win.OwlyFrame;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.StateTimeTensorFunction;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.owl.math.region.EllipsoidRegion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Log;

/** the coordinates represent the population of predators and prey.
 * the domain coordinates are computed from the log of the state coordinates */
/* package */ enum LvRepresentComparison {
  ;
  static void launch(TensorUnaryOperator represent) {
    Tensor eta = Tensors.vector(10, 10);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        RungeKutta45Integrator.INSTANCE, RationalScalar.of(1, 30), 4);
    StateSpaceModel stateSpaceModel = LvStateSpaceModel.of(1, 2);
    Collection<Flow> controls = LvControls.create(stateSpaceModel, 2);
    EllipsoidRegion ellipsoidRegion = new EllipsoidRegion(Tensors.vector(2, 1), Tensors.vector(0.1, 0.1));
    GoalInterface goalInterface = new LvGoalInterface(ellipsoidRegion);
    // ---
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        eta, stateIntegrator, controls, EmptyPlannerConstraint.INSTANCE, goalInterface);
    // ---
    trajectoryPlanner.represent = StateTimeTensorFunction.state(represent);
    trajectoryPlanner.insertRoot(new StateTime(Tensors.vector(2, .5), RealScalar.ZERO));
    Expand.maxSteps(trajectoryPlanner, 4000);
    OwlyFrame owlyFrame = OwlyGui.glc(trajectoryPlanner);
    owlyFrame.addBackground(RegionRenders.create(ellipsoidRegion));
    owlyFrame.configCoordinateOffset(100, 300);
    owlyFrame.jFrame.setBounds(100, 100, 500, 500);
  }

  public static void main(String[] args) {
    launch(Log::of);
    launch(t -> t);
  }
}
