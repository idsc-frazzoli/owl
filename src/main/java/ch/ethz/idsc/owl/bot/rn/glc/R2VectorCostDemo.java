// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import java.awt.Dimension;
import java.util.Optional;

import ch.ethz.idsc.owl.ani.api.TrajectoryControl;
import ch.ethz.idsc.owl.bot.r2.ImageCostFunction;
import ch.ethz.idsc.owl.bot.r2.WaypointDistanceCost;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.Regions;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.CirclePoints;

/** demo shows the use of a cost image that is added to the distance cost
 * which gives an incentive to stay clear of obstacles */
public class R2VectorCostDemo implements DemoInterface {
  @Override // from DemoInterface
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        EulerIntegrator.INSTANCE, //
        new StateTime(Tensors.vector(7, 6), RealScalar.ZERO));
    TrajectoryControl trajectoryControl = new R2TrajectoryControl();
    Tensor waypoints = CirclePoints.of(30).multiply(RealScalar.of(10));
    ImageCostFunction imageCostFunction = WaypointDistanceCost.of( //
        waypoints, true, RealScalar.ONE, RealScalar.of(10), new Dimension(120, 100));
    Tensor range = imageCostFunction.range();
    System.out.println(range);
    Tensor image = imageCostFunction.image();
    R2Entity r2Entity = new R2VecEntity(episodeIntegrator, trajectoryControl) {
      @Override
      public Optional<CostFunction> getPrimaryCost() {
        return Optional.of(imageCostFunction);
      }
    };
    owlyAnimationFrame.add(r2Entity);
    Region<Tensor> imageRegion = Regions.emptyRegion();
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(imageRegion);
    MouseGoal.simple(owlyAnimationFrame, r2Entity, plannerConstraint);
    owlyAnimationFrame.addBackground(AxesRender.INSTANCE);
    owlyAnimationFrame.addBackground(ImageRender.scale(RegionRenders.image(image), imageCostFunction.scale()));
    owlyAnimationFrame.configCoordinateOffset(50, 700);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new R2VectorCostDemo().start().jFrame.setVisible(true);
  }
}
