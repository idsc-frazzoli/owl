// code by jph
package ch.ethz.idsc.owl.bot.delta;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.owl.ani.api.TrajectoryControl;
import ch.ethz.idsc.owl.bot.r2.ImageGradientInterpolation;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.sca.Round;

/** class controls delta using {@link StandardTrajectoryPlanner} */
/* package */ class DeltaxTEntity extends DeltaEntity {
  public DeltaxTEntity(EpisodeIntegrator episodeIntegrator, TrajectoryControl trajectoryControl, ImageGradientInterpolation imageGradientInterpolation) {
    super(episodeIntegrator, trajectoryControl, imageGradientInterpolation);
  }

  @Override
  protected StateTimeRaster stateTimeRaster() {
    return EtaRaster.timeDependent(PARTITION_SCALE, FIXED_STATE_INTEGRATOR.getTimeStepTrajectory(), StateTime::joined);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    super.render(geometricLayer, graphics);
    // ---
    StateTime stateTime = getStateTimeNow();
    graphics.setColor(Color.GRAY);
    graphics.drawString(stateTime.time().map(Round._3).toString(), 0, 12 * 2);
  }
}
