// code by jph
package ch.ethz.idsc.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

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
      graphics.draw(new Line2D.Double(geometricLayer.toPoint2D(Tensors.vector(-10, 1)), geometricLayer.toPoint2D(Tensors.vector(10, 1))));
      graphics.draw(new Line2D.Double(geometricLayer.toPoint2D(Tensors.vector(1, -10)), geometricLayer.toPoint2D(Tensors.vector(1, 10))));
    }
    {
      graphics.setColor(Color.GRAY);
      graphics.draw(new Line2D.Double(geometricLayer.toPoint2D(Tensors.vector(-10, 0)), geometricLayer.toPoint2D(Tensors.vector(10, 0))));
      graphics.draw(new Line2D.Double(geometricLayer.toPoint2D(Tensors.vector(0, -10)), geometricLayer.toPoint2D(Tensors.vector(0, 10))));
    }
  }
}
