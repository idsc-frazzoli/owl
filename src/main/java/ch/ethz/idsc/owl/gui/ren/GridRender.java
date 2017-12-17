// code by jph
package ch.ethz.idsc.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensors;

public enum GridRender implements RenderInterface {
  INSTANCE;
  // ---
  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    {
      graphics.setColor(Color.LIGHT_GRAY);
      graphics.draw(geometricLayer.toVector(Tensors.vector(-10, 1), Tensors.vector(20, 0)));
      graphics.draw(geometricLayer.toVector(Tensors.vector(1, -10), Tensors.vector(0, 20)));
    }
    {
      graphics.setColor(Color.GRAY);
      graphics.draw(geometricLayer.toVector(Tensors.vector(-10, 0), Tensors.vector(20, 0)));
      graphics.draw(geometricLayer.toVector(Tensors.vector(0, -10), Tensors.vector(0, 20)));
    }
  }
}
