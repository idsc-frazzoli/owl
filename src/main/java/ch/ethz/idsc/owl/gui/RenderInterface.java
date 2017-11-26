// code by jph
package ch.ethz.idsc.owl.gui;

import java.awt.Graphics2D;

/** capability for drawing in {@link GeometricComponent} */
public interface RenderInterface {
  void render(GeometricLayer geometricLayer, Graphics2D graphics);
}
