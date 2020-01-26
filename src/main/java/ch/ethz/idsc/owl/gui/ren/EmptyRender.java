// code by jph
package ch.ethz.idsc.owl.gui.ren;

import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;

public enum EmptyRender implements RenderInterface {
  INSTANCE;

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // ---
  }
}
