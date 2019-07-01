// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.Arrays;
import java.util.Collection;

import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.Se2ComboRegion;
import ch.ethz.idsc.owl.bot.se2.Se2MinTimeGoalManager;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.ethz.idsc.owl.glc.core.GlcTrajectoryPlanner;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.std.StandardGlcTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.win.OwlyFrame;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.HyperplaneRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.RegionUnion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.qty.Degree;

enum Se2rAnimateDemo {
  ;
  public static GlcTrajectoryPlanner trajectoryPlanner() {
    Tensor eta = Tensors.vector(6, 6, 50 / Math.PI);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        Se2CarIntegrator.INSTANCE, RationalScalar.of(1, 6), 5);
    FlowsInterface carFlows = Se2CarFlows.standard(RealScalar.ONE, Degree.of(45));
    Collection<Flow> controls = carFlows.getFlows(6);
    // place holder for parameter class
    Se2ComboRegion se2ComboRegion = //
        Se2ComboRegion.spherical(Tensors.vector(-1, -1, Math.PI * 2), Tensors.vector(0.1, 0.1, 0.17));
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager( //
        se2ComboRegion, controls);
    GoalInterface goalInterface = se2MinTimeGoalManager.getGoalInterface();
    Region<Tensor> region = RegionUnion.wrap(Arrays.asList( //
        new HyperplaneRegion(Tensors.vector(0, -1, 0), RealScalar.of(1.5)), //
        new HyperplaneRegion(Tensors.vector(0, +1, 0), RealScalar.of(2.0)) //
    ));
    PlannerConstraint plannerConstraint = //
        new TrajectoryObstacleConstraint(CatchyTrajectoryRegionQuery.timeInvariant(region));
    // ---
    return new StandardGlcTrajectoryPlanner( //
        EtaRaster.state(eta), stateIntegrator, controls, plannerConstraint, goalInterface);
  }

  public static void main(String[] args) throws Exception {
    GlcTrajectoryPlanner trajectoryPlanner = trajectoryPlanner();
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(3), RealScalar.ZERO));
    OwlyFrame owlyFrame = OwlyGui.start();
    owlyFrame.configCoordinateOffset(169, 71);
    owlyFrame.jFrame.setBounds(100, 100, 300, 200);
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    while (!trajectoryPlanner.getBest().isPresent() && owlyFrame.jFrame.isVisible()) {
      glcExpand.findAny(1);
      owlyFrame.setGlc(trajectoryPlanner);
      Thread.sleep(100);
    }
  }
}
