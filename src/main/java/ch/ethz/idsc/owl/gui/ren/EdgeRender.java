// code by jph
package ch.ethz.idsc.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Objects;

import ch.ethz.idsc.owl.data.tree.StateCostNode;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.math.d2.Extract2D;
import ch.ethz.idsc.sophus.ply.d2.ConvexHull;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Chop;

/** renders the edges between nodes
 * 
 * the edges are drawn as straight lines with the color of the cost to root */
public class EdgeRender {
  private static final int NODE_WIDTH = 2;
  private static final Color CONVEXHULL = new Color(192, 192, 0, 128);
  private static final Color COLOR_EDGE = new Color(128, 128, 255, 64);
  // ---
  private final int nodeBound;
  private final Color color;
  private RenderInterface renderInterface = EmptyRender.INSTANCE;

  public EdgeRender(int nodeBound, Color color) {
    this.nodeBound = nodeBound;
    this.color = color;
  }

  public EdgeRender(int nodeBound) {
    this(nodeBound, COLOR_EDGE);
  }

  public EdgeRender() {
    this(TreeRender.LIMIT_DEFAULT);
  }

  public RenderInterface setCollection(Collection<? extends StateCostNode> collection) {
    return renderInterface = Objects.isNull(collection) || collection.isEmpty() //
        ? EmptyRender.INSTANCE //
        : new Render(collection);
  }

  public RenderInterface getRender() {
    return renderInterface;
  }

  class Render implements RenderInterface {
    private final Collection<? extends StateCostNode> collection;
    private final Tensor polygon;

    public Render(Collection<? extends StateCostNode> collection) {
      this.collection = collection;
      polygon = ConvexHull.of(collection.stream() //
          .map(StateCostNode::state) //
          .map(Extract2D.FUNCTION), Chop._10); //
    }

    @Override // from RenderInterface
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      graphics.setColor(CONVEXHULL);
      Path2D path2D = geometricLayer.toPath2D(polygon);
      path2D.closePath();
      graphics.draw(path2D);
      // ---
      if (collection.size() <= nodeBound) { // don't draw tree beyond certain node count
        graphics.setColor(color);
        for (StateCostNode node : collection) {
          final Point2D p1 = geometricLayer.toPoint2D(node.state());
          graphics.fill(new Rectangle2D.Double(p1.getX(), p1.getY(), NODE_WIDTH, NODE_WIDTH));
          StateCostNode parent = node.parent();
          if (Objects.nonNull(parent)) {
            Point2D p2 = geometricLayer.toPoint2D(parent.state());
            Shape shape = new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            graphics.draw(shape);
          }
        }
      }
    }
  }
}
