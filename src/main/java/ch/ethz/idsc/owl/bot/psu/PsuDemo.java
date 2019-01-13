// code by jph
package ch.ethz.idsc.owl.bot.psu;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.glc.adapter.EmptyObstacleConstraint;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcNodes;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.math.StateTimeTensorFunction;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta4Integrator;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;

/** Pendulum Swing-up
 * 
 * bapaden phd thesis: 5.5.2 Torque-Limited Pendulum Swing-Up
 * 
 * "A Generalized Label Correcting Method for Optimal Kinodynamic Motion Planning" [Paden/Frazzoli] */
/* package */ enum PsuDemo {
  ;
  private static final Tensor ETA = Tensors.vector(5, 7);

  public static TrajectoryPlanner simple() {
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        RungeKutta4Integrator.INSTANCE, RationalScalar.of(1, 4), 5);
    Collection<Flow> controls = PsuControls.createControls(0.2, 6);
    PsuWrap psuWrap = PsuWrap.INSTANCE;
    GoalInterface goalInterface = PsuGoalManager.of( //
        PsuMetric.INSTANCE, Tensors.vector(Math.PI * 0.7, 0.5), RealScalar.of(0.3));
    // ---
    StateTimeRaster stateTimeRasterization = new EtaRaster(ETA, StateTimeTensorFunction.state(psuWrap::represent));
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRasterization, stateIntegrator, controls, EmptyObstacleConstraint.INSTANCE, goalInterface);
    // ---
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(2), RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(1000);
    return trajectoryPlanner;
  }

  public static TrajectoryPlanner medium() {
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        RungeKutta45Integrator.INSTANCE, RationalScalar.of(1, 4), 5);
    Collection<Flow> controls = PsuControls.createControls(0.2, 6);
    PsuWrap psuWrap = PsuWrap.INSTANCE;
    GoalInterface goalInterface = PsuGoalManager.of( //
        PsuMetric.INSTANCE, Tensors.vector(Math.PI, 2), RealScalar.of(0.3));
    // ---
    StateTimeRaster stateTimeRaster = new EtaRaster(ETA, StateTimeTensorFunction.state(psuWrap::represent));
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster, stateIntegrator, controls, EmptyObstacleConstraint.INSTANCE, goalInterface);
    // ---
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(2), RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(1000);
    System.out.println("ExpandCount=" + glcExpand.getExpandCount());
    return trajectoryPlanner;
  }

  public static void main(String[] args) {
    TrajectoryPlanner trajectoryPlanner = medium();
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.get());
      StateTimeTrajectories.print(trajectory);
    }
    OwlyGui.glc(trajectoryPlanner);
  }
}
