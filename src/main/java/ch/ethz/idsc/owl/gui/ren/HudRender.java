// code by jph
package ch.ethz.idsc.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;

// TODO decide on purpose of HudRender, for now, do nothing
class HudRender implements RenderInterface {
  private static final Color SHADING = new Color(0, 0, 0, 64);
  // ---
  @SuppressWarnings("unused")
  private final TrajectoryPlanner trajectoryPlanner;

  HudRender(TrajectoryPlanner trajectoryPlanner) {
    this.trajectoryPlanner = trajectoryPlanner;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(SHADING);
  }
}
