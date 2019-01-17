// code by jph
package ch.ethz.idsc.owl.gui;

import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.win.GeometricComponent;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;

/** capability for drawing in {@link GeometricComponent} */
@FunctionalInterface
public interface RenderInterface {
  void render(GeometricLayer geometricLayer, Graphics2D graphics);
}
