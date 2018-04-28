// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import ch.ethz.idsc.owl.bot.r2.R2ImageRegions;
import ch.ethz.idsc.owl.bot.r2.R2xTImageStateTimeRegion;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ani.TrajectoryEntity;
//import ch.ethz.idsc.owl.gui.ren.CurveRender;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.map.RigidFamily;
import ch.ethz.idsc.owl.math.map.Se2Family;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;

/** the obstacle region in the demo is the outside of a rotating letter 'a' */
public class R2xTImageAnimationDemo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        EulerIntegrator.INSTANCE, //
        new StateTime(Tensors.vector(1.5, 2), RealScalar.ZERO));
    TrajectoryEntity abstractEntity = new R2xTEntity(episodeIntegrator, RealScalar.of(1.5));
    owlyAnimationFrame.set(abstractEntity);
    // ---
    RigidFamily rigidFamily = Se2Family.rotationAround( //
        Tensors.vectorDouble(1.5, 2), time -> time.multiply(RealScalar.of(0.1)));
    ImageRegion imageRegion = R2ImageRegions.inside_circ();
    Region<StateTime> region = new R2xTImageStateTimeRegion( //
        imageRegion, rigidFamily, () -> abstractEntity.getStateTimeNow().time());
    // ---
    owlyAnimationFrame.setPlannerConstraint(new TrajectoryObstacleConstraint(new SimpleTrajectoryRegionQuery(region)));
    owlyAnimationFrame.addBackground((RenderInterface) region);
    // owlyAnimationFrame.addBackground(new CurveRender());
    owlyAnimationFrame.configCoordinateOffset(200, 400);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new R2xTImageAnimationDemo().start().jFrame.setVisible(true);
  }
}
