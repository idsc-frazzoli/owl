// code by jph
package ch.ethz.idsc.owl.bot.lv;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.EmptyPlannerConstraint;
import ch.ethz.idsc.owl.glc.adapter.Expand;
import ch.ethz.idsc.owl.glc.adapter.GlcTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ren.TrajectoryRender;
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
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Log;

/** the coordinates represent the population of predators and prey.
 * the domain coordinates are computed from the log of the state coordinates */
/* package */ enum LvDemo {
  ;
  public static void main(String[] args) {
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
    trajectoryPlanner.represent = StateTimeTensorFunction.state(Log::of);
    trajectoryPlanner.insertRoot(new StateTime(Tensors.vector(2, 0.1), RealScalar.ZERO));
    int steps = Expand.maxSteps(trajectoryPlanner, 5000);
    System.out.println(steps);
    OwlyFrame owlyFrame = OwlyGui.glc(trajectoryPlanner);
    owlyFrame.addBackground(RegionRenders.create(ellipsoidRegion));
    Optional<GlcNode> goalNode = trajectoryPlanner.getBest();
    if (goalNode.isPresent()) {
      List<TrajectorySample> trajectory = //
          GlcTrajectories.detailedTrajectoryTo(stateIntegrator, goalNode.get());
      owlyFrame.addBackground(new TrajectoryRender(trajectory));
    } else
      System.out.println("no trajectory found");
  }
}
