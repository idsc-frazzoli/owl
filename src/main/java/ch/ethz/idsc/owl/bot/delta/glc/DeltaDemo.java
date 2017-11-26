// code by jph
package ch.ethz.idsc.owl.bot.delta.glc;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.delta.DeltaFlows;
import ch.ethz.idsc.owl.bot.delta.DeltaMinTimeGoalManager;
import ch.ethz.idsc.owl.bot.delta.DeltaStateSpaceModel;
import ch.ethz.idsc.owl.bot.delta.ImageGradient;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.Expand;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.core.DebugUtils;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ani.OwlyFrame;
import ch.ethz.idsc.owl.gui.ani.OwlyGui;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;

/** simple animation of small boat driving upstream, or downstream in a river delta */
enum DeltaDemo {
  ;
  public static void main(String[] args) throws Exception {
    // for 0.5 (in direction of river):
    // mintime w/o heuristic requires 1623 expands
    // mintime w/. heuristic requires 1334 expands
    // for -.02 (against direction of river)
    // mintime w/o heuristic requires 2846 expands
    // mintime w/. heuristic requires 2844 expands
    Scalar amp = RealScalar.of(-.25); // -.25 .5
    // ---
    Tensor eta = Tensors.vector(8, 8);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        RungeKutta45Integrator.INSTANCE, RationalScalar.of(1, 10), 4);
    Tensor range = Tensors.vector(9, 6.5);
    ImageGradient imageGradient = ImageGradient.linear(ResourceData.of("/io/delta_uxy.png"), range, amp);
    Scalar maxInput = RealScalar.ONE;
    StateSpaceModel stateSpaceModel = new DeltaStateSpaceModel(imageGradient);
    Collection<Flow> controls = new DeltaFlows(stateSpaceModel, maxInput).getFlows(25);
    Tensor obstacleImage = ResourceData.of("/io/delta_free.png"); //
    Region<Tensor> region = new ImageRegion(obstacleImage, range, true);
    TrajectoryRegionQuery obstacleQuery = //
        SimpleTrajectoryRegionQuery.timeInvariant(region);
    Scalar maxMove = stateSpaceModel.getLipschitz().add(maxInput);
    SphericalRegion sphericalRegion = new SphericalRegion(Tensors.vector(2.1, 0.3), RealScalar.of(0.3));
    GoalInterface goalInterface = new DeltaMinTimeGoalManager(sphericalRegion, maxMove);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        eta, stateIntegrator, controls, obstacleQuery, goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(Tensors.vector(8.8, 0.5), RealScalar.ZERO));
    // ---
    OwlyFrame owlyFrame = OwlyGui.start();
    owlyFrame.configCoordinateOffset(33, 416);
    owlyFrame.addBackground(RegionRenders.create(region));
    owlyFrame.addBackground(RegionRenders.create(sphericalRegion));
    // ---
    owlyFrame.addBackground(DeltaHelper.vectorFieldRender(stateSpaceModel, range, region, RealScalar.of(0.1)));
    // ---
    owlyFrame.jFrame.setBounds(100, 100, 620, 475);
    int steps = 0;
    while (!trajectoryPlanner.getBest().isPresent() && owlyFrame.jFrame.isVisible()) {
      steps += Expand.maxSteps(trajectoryPlanner, 30);
      owlyFrame.setGlc(trajectoryPlanner);
      Thread.sleep(1);
      DebugUtils.heuristicConsistencyCheck(trajectoryPlanner);
    }
    System.out.println("#expand = " + steps);
  }
}
