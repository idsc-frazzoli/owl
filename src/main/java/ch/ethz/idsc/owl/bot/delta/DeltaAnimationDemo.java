// code by jph
package ch.ethz.idsc.owl.bot.delta;

import ch.ethz.idsc.owl.bot.r2.ImageGradientInterpolation;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.EuclideanTrajectoryControl;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
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
    Tensor obstacleImage = ResourceData.of("/io/delta_free.png"); //
    ImageRegion imageRegion = new ImageRegion(obstacleImage, range, true);
    TrajectoryRegionQuery obstacleQuery = SimpleTrajectoryRegionQuery.timeInvariant(imageRegion);
    StateTime stateTime = new StateTime(Tensors.vector(10, 3.5), RealScalar.ZERO);
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        new DeltaStateSpaceModel(imageGradientInterpolation), EulerIntegrator.INSTANCE, stateTime);
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl(Array.zeros(2));
    owlyAnimationFrame.set(new DeltaEntity(episodeIntegrator, trajectoryControl, imageGradientInterpolation));
    owlyAnimationFrame.setObstacleQuery(obstacleQuery);
    StateSpaceModel stateSpaceModel = new DeltaStateSpaceModel(imageGradientInterpolation);
    owlyAnimationFrame.addBackground(RegionRenders.create(imageRegion));
    owlyAnimationFrame.addBackground(DeltaHelper.vectorFieldRender(stateSpaceModel, range, imageRegion, RealScalar.of(0.5)));
    owlyAnimationFrame.configCoordinateOffset(50, 600);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) throws Exception {
    new DeltaAnimationDemo().start().jFrame.setVisible(true);
  }
}
