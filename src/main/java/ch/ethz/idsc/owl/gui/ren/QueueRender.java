// code by jph
package ch.ethz.idsc.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Collection;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.gui.GeometricLayer;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.tensor.Tensor;

public class QueueRender implements RenderInterface {
  private Collection<GlcNode> collection;

  public QueueRender(Collection<GlcNode> collection) {
    this.collection = collection;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(new Color(0, 192, 192, 128));
    for (GlcNode node : collection) {
      Tensor x = node.stateTime().state();
      Point2D p = geometricLayer.toPoint2D(x);
      Shape shape = new Ellipse2D.Double(p.getX() - 3, p.getY() - 3, 8, 8);
      graphics.fill(shape);
    }
  }
}
