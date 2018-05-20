// code by jph
package ch.ethz.idsc.owl.bot.delta;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.r2.ImageGradientInterpolation;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.math.state.TrajectoryWrap;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Round;

/** class controls delta using {@link StandardTrajectoryPlanner} */
/* package */ class DeltaxTEntity extends DeltaEntity {
  public DeltaxTEntity(EpisodeIntegrator episodeIntegrator, TrajectoryControl trajectoryControl, ImageGradientInterpolation imageGradientInterpolation) {
    super(episodeIntegrator, trajectoryControl, imageGradientInterpolation);
  }

  @Override
  public TrajectoryPlanner createTrajectoryPlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    TrajectoryPlanner trajectoryPlanner = super.createTrajectoryPlanner(plannerConstraint, goal);
    trajectoryPlanner.represent = StateTime::joined;
    return trajectoryPlanner;
  }

  @Override
  protected Tensor eta() {
    Scalar dt = FIXEDSTATEINTEGRATOR.getTimeStepTrajectory();
    return super.eta().copy().append(dt.reciprocal());
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    super.render(geometricLayer, graphics);
    // ---
    if (Objects.nonNull(trajectory)) {
      StateTime stateTime = getStateTimeNow();
      Scalar now = stateTime.time();
      // TODO not efficient
      TrajectoryWrap trajectoryWrap = TrajectoryWrap.of(trajectory);
      Optional<TrajectorySample> optional = trajectoryWrap.findTrajectorySample(now);
      if (optional.isPresent()) {
        TrajectorySample trajectorySample = optional.get();
        Path2D path2d = geometricLayer.toPath2D(Tensors.of(stateTime.state(), trajectorySample.stateTime().state()));
        graphics.setColor(Color.PINK);
        graphics.draw(path2d);
      }
    }
    {
      StateTime stateTime = getStateTimeNow();
      graphics.setColor(Color.GRAY);
      graphics.drawString("" + stateTime.time().map(Round._3), 0, 12 * 2);
    }
  }
}
