// code by jph
package ch.ethz.idsc.owl.bot.delta;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.ani.adapter.EuclideanTrajectoryControl;
import ch.ethz.idsc.owl.ani.api.TrajectoryControl;
import ch.ethz.idsc.owl.bot.r2.ImageGradientInterpolation;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;

public class DeltaAnimationDemo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    // ---
    Scalar amp = RealScalar.of(-.05);
    Tensor range = Tensors.vector(12.6, 9.1).unmodifiable();
    ImageGradientInterpolation imageGradientInterpolation = //
        ImageGradientInterpolation.nearest(ResourceData.of("/io/delta_uxy.png"), range, amp);
    BufferedImage bufferedImage = ResourceData.bufferedImage("/io/delta_free.png");
    Region<Tensor> region = ImageRegion.of(bufferedImage, range, true);
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(region);
    StateTime stateTime = new StateTime(Tensors.vector(10, 3.5), RealScalar.ZERO);
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        new DeltaStateSpaceModel(imageGradientInterpolation), EulerIntegrator.INSTANCE, stateTime);
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl();
    DeltaEntity deltaEntity = new DeltaEntity(episodeIntegrator, trajectoryControl, imageGradientInterpolation);
    MouseGoal.simple(owlyAnimationFrame, deltaEntity, plannerConstraint);
    owlyAnimationFrame.add(deltaEntity);
    StateSpaceModel stateSpaceModel = new DeltaStateSpaceModel(imageGradientInterpolation);
    owlyAnimationFrame.addBackground(RegionRenders.create(region));
    owlyAnimationFrame.addBackground(DeltaHelper.vectorFieldRender(stateSpaceModel, range, region, RealScalar.of(0.5)));
    owlyAnimationFrame.geometricComponent.setOffset(50, 600);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) throws Exception {
    new DeltaAnimationDemo().start().jFrame.setVisible(true);
  }
}
