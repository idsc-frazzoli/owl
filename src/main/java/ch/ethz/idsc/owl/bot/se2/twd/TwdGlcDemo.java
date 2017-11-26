// code by jl
package ch.ethz.idsc.owl.bot.se2.twd;

import java.util.Arrays;
import java.util.Collection;

import ch.ethz.idsc.owl.bot.se2.Se2LateralAcceleration;
import ch.ethz.idsc.owl.bot.se2.Se2MinTimeGoalManager;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.adapter.MultiCostGoalAdapter;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.par.Parameters;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ani.OwlyFrame;
import ch.ethz.idsc.owl.gui.ani.OwlyGui;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.MidpointIntegrator;
import ch.ethz.idsc.owl.math.region.HyperplaneRegion;
import ch.ethz.idsc.owl.math.region.RegionUnion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** if the controls are significantly larger than 1,
 * the gui features long black lines indicating the control vectors */
enum TwdGlcDemo {
  ;
  public static void main(String[] args) throws Exception {
    RationalScalar resolution = (RationalScalar) RealScalar.of(6);
    Scalar timeScale = RealScalar.of(32);
    Scalar depthScale = RealScalar.of(20);
    Tensor partitionScale = Tensors.vector(5, 5, 2 * Math.PI / 360 * 20);
    Scalar dtMax = RationalScalar.of(1, 10);
    int maxIter = 2000;
    Parameters parameters = new TwdParameters( //
        resolution, timeScale, depthScale, partitionScale, dtMax, maxIter, RealScalar.ONE); // TODO check lipschitz
    parameters.printResolution();
    System.out.println("DomainSize: 1/Eta: " + parameters.getEta().map(n -> RealScalar.ONE.divide(n)));
    // ---
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        MidpointIntegrator.INSTANCE, parameters.getdtMax(), parameters.getTrajectorySize());
    // ---
    TwdDuckieFlows twdControls = new TwdDuckieFlows(RealScalar.ONE, RealScalar.ONE);
    Collection<Flow> controls = twdControls.getFlows(parameters.getResolutionInt());
    // ---
    TrajectoryRegionQuery obstacleQuery = SimpleTrajectoryRegionQuery.timeInvariant( //
        RegionUnion.wrap(Arrays.asList( //
            new HyperplaneRegion(Tensors.vector(0, -1, 0), RealScalar.of(4)), //
            new HyperplaneRegion(Tensors.vector(0, +1, 0), RealScalar.of(3)) //
        )));
    // ---
    Tensor goalCenter = Tensors.vector(2, -2, -1 * Math.PI);
    GoalInterface goalInterface = MultiCostGoalAdapter.of( //
        Se2MinTimeGoalManager.create(goalCenter, Tensors.vector(0.5, 0.5, 50 * Math.PI / 180), controls), //
        Arrays.asList(Se2LateralAcceleration.COSTFUNCTION));
    // ---
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner(parameters.getEta(), //
        stateIntegrator, controls, obstacleQuery, goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(Tensors.vector(0, 0, 0.5 * Math.PI), RealScalar.ZERO));
    OwlyFrame owlyFrame = OwlyGui.start();
    owlyFrame.configCoordinateOffset(200, 300);
    owlyFrame.jFrame.setBounds(100, 100, 620, 475);
    while (!trajectoryPlanner.getBest().isPresent() && owlyFrame.jFrame.isVisible()) {
      GlcExpand.maxSteps(trajectoryPlanner, 30, parameters.getDepthLimit());
      owlyFrame.setGlc(trajectoryPlanner);
      Thread.sleep(1);
    }
  }
}
