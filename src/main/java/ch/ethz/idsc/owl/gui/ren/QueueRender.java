// code by jl
package ch.ethz.idsc.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Collection;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;

public class QueueRender implements RenderInterface {
  private static final int LIMIT = 5000;
  private static final int OFS = 3;
  private static final int SIZE = 8;
  // ---
  private final Collection<GlcNode> collection;

  public QueueRender(Collection<GlcNode> collection) {
    this.collection = collection;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(new Color(0, 192, 192, 128));
    if (collection.size() < LIMIT)
      for (GlcNode node : collection) {
        Tensor x = node.stateTime().state();
        Point2D p = geometricLayer.toPoint2D(x);
        Shape shape = new Ellipse2D.Double(p.getX() - OFS, p.getY() - OFS, SIZE, SIZE);
        graphics.fill(shape);
      }
  }
}
