// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import ch.ethz.idsc.owl.ani.api.TrajectoryEntity;
import ch.ethz.idsc.owl.bot.r2.R2ImageRegions;
import ch.ethz.idsc.owl.bot.r2.R2xTImageStateTimeRegion;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
//import ch.ethz.idsc.owl.gui.ren.CurveRender;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.hs.r2.R2RigidFamily;
import ch.ethz.idsc.sophus.hs.r2.Se2Family;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;

/** the obstacle region in the demo is the outside of a rotating letter 'a' */
public class R2xTImageAnimationDemo implements DemoInterface {
  private static final Scalar DELAY = RealScalar.of(1.5);

  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        EulerIntegrator.INSTANCE, //
        new StateTime(Tensors.vector(1.5, 2), RealScalar.ZERO));
    TrajectoryEntity abstractEntity = new R2xTEntity(episodeIntegrator, DELAY);
    owlyAnimationFrame.add(abstractEntity);
    // ---
    R2RigidFamily rigidFamily = Se2Family.rotationAround( //
        Tensors.vectorDouble(1.5, 2), time -> time.multiply(RealScalar.of(0.1)));
    ImageRegion imageRegion = R2ImageRegions.inside_circ();
    Region<StateTime> region = new R2xTImageStateTimeRegion( //
        imageRegion, rigidFamily, () -> abstractEntity.getStateTimeNow().time());
    // ---
    PlannerConstraint plannerConstraint = RegionConstraints.stateTime(region);
    MouseGoal.simple(owlyAnimationFrame, abstractEntity, plannerConstraint);
    owlyAnimationFrame.addBackground((RenderInterface) region);
    // owlyAnimationFrame.addBackground(new CurveRender());
    owlyAnimationFrame.configCoordinateOffset(200, 400);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new R2xTImageAnimationDemo().start().jFrame.setVisible(true);
  }
}
