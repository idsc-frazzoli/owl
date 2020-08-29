// code by jph
package ch.ethz.idsc.owl.bot.sat;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.win.OwlyFrame;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.owl.math.region.EllipsoidRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum SatelliteDemo {
  ;
  public static void main(String[] args) throws Exception {
    Collection<Tensor> controls = new SatelliteControls(RealScalar.of(0.9)).getFlows(6);
    Tensor start = Tensors.vector(2, 0, 0, 2); // pos, vel
    Region<Tensor> obstacleRegion = new EllipsoidRegion( // obstacle at origin
        Tensors.vector(0, 0, 0, 0), //
        Tensors.vector(0.5, 0.5, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    // ---
    Tensor eta = Tensors.vector(3, 3, 2, 2);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        RungeKutta45Integrator.INSTANCE, SatelliteStateSpaceModel.INSTANCE, RationalScalar.of(1, 10), 6);
    PlannerConstraint plannerConstraint = //
        new TrajectoryObstacleConstraint(CatchyTrajectoryRegionQuery.timeInvariant(obstacleRegion));
    EllipsoidRegion goalRegion = new EllipsoidRegion( //
        Tensors.vector(2, -2, 0, 0), Tensors.vector(0.5, 0.5, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    GoalInterface goalInterface = new SatelliteGoalManager(goalRegion);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        EtaRaster.state(eta), stateIntegrator, controls, plannerConstraint, goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(start, RealScalar.ZERO));
    // ---
    OwlyFrame owlyFrame = OwlyGui.start();
    owlyFrame.addBackground(RegionRenders.create(obstacleRegion));
    owlyFrame.addBackground(RegionRenders.create(goalRegion));
    // ---
    owlyFrame.jFrame.setBounds(100, 100, 600, 600);
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    while (!glcExpand.isOptimal() && owlyFrame.jFrame.isVisible()) {
      glcExpand.findAny(50);
      owlyFrame.setGlc(trajectoryPlanner);
      Thread.sleep(1);
    }
    System.out.println("#expand = " + glcExpand.getExpandCount());
  }
}
