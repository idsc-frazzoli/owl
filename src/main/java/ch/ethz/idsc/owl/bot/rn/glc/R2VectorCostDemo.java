// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import java.awt.Dimension;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.r2.ImageCostFunction;
import ch.ethz.idsc.owl.bot.r2.WaypointDistanceCost;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.Regions;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.lie.CirclePoints;

/** demo shows the use of a cost image that is added to the distance cost
 * which gives an incentive to stay clear of obstacles */
public class R2VectorCostDemo implements DemoInterface {
  @Override // from DemoInterface
  public OwlyAnimationFrame start() {
    // R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
    // ---
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        EulerIntegrator.INSTANCE, //
        new StateTime(Tensors.vector(7, 6), RealScalar.ZERO));
    TrajectoryControl trajectoryControl = new R2TrajectoryControl();
    Tensor waypoints = CirclePoints.of(30).multiply(RealScalar.of(10));
    // waypoints = new BSpline1CurveSubdivision(Se2Geodesic.INSTANCE).cyclic(waypoints);
    ImageCostFunction waypointCost = WaypointDistanceCost.linear(waypoints, Tensors.vector(8, 10), 10.0f, new Dimension(100, 100));
    Tensor image = waypointCost.image();
    try {
      Export.of(UserHome.Pictures("imagecost.png"), ArrayPlot.of(image, ColorDataGradients.CLASSIC));
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    System.out.println(Dimensions.of(image));
    R2Entity r2Entity = new R2VecEntity(episodeIntegrator, trajectoryControl) {
      @Override
      public Optional<CostFunction> getPrimaryCost() {
        return Optional.of(waypointCost);
      }
    };
    // r2Entity.extraCosts.add(r2ImageRegionWrap.costFunction());
    owlyAnimationFrame.add(r2Entity);
    // ImageRegion imageRegion = r2ImageRegionWrap.imageRegion();
    Region<Tensor> imageRegion = Regions.emptyRegion();
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(imageRegion);
    MouseGoal.simple(owlyAnimationFrame, r2Entity, plannerConstraint);
    // owlyAnimationFrame.addBackground(RegionRenders.create(imageRegion));
    owlyAnimationFrame.addBackground(GridRender.INSTANCE);
    owlyAnimationFrame.addBackground(new ImageRender(RegionRenders.image(image), waypointCost.scale()));
    owlyAnimationFrame.configCoordinateOffset(50, 700);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new R2VectorCostDemo().start().jFrame.setVisible(true);
  }
}
