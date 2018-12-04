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
import java.util.DoubleSummaryStatistics;
import java.util.Objects;

import ch.ethz.idsc.owl.data.tree.StateCostNode;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.ConvexHull;
import ch.ethz.idsc.tensor.sca.Chop;

/** renders the edges between nodes
 * 
 * the edges are drawn as straight lines with the color of the cost to root
 * 
 * only real-valued costs are supported
 * in particular costs of type {@link VectorScalar} are not supported
 * @see EdgeRender */
public class TreeRender implements RenderInterface {
  private static final int NODE_WIDTH = 2;
  private static final Color CONVEXHULL = new Color(192, 192, 0, 128);
  // ---
  private Collection<? extends StateCostNode> collection;
  private Tensor polygon;
  // ---
  private int nodeBound = 2500;

  public TreeRender(Collection<? extends StateCostNode> collection) {
    setCollection(collection);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Collection<? extends StateCostNode> _collection = collection;
    if (Objects.isNull(_collection) || _collection.isEmpty())
      return;
    TreeColor treeColor = TreeColor.ofDimensions(_collection.iterator().next().state().length());
    DoubleSummaryStatistics dss = _collection.stream() //
        .map(StateCostNode::costFromRoot) //
        .map(Scalar::number) //
        .mapToDouble(Number::doubleValue) //
        .filter(Double::isFinite) //
        .summaryStatistics();
    final double min = dss.getMin();
    final double max = dss.getMax();
    long count = dss.getCount();
    if (Objects.nonNull(polygon)) {
      graphics.setColor(CONVEXHULL);
      Path2D path2D = geometricLayer.toPath2D(polygon);
      path2D.closePath();
      graphics.draw(path2D);
    }
    double inverse = (treeColor.nodeColor.length() - 1) / (max - min);
    // System.out.println("count=" + count + ", inverse=" + inverse);
    if (count <= nodeBound || true) // don't draw tree beyond certain node count
      for (StateCostNode node : _collection) {
        double value = node.costFromRoot().number().doubleValue();
        final double interp = (value - min) * inverse;
        graphics.setColor(treeColor.nodeColor.getColor((int) interp));
        final Point2D p1 = geometricLayer.toPoint2D(node.state());
        graphics.fill(new Rectangle2D.Double(p1.getX(), p1.getY(), NODE_WIDTH, NODE_WIDTH));
        StateCostNode parent = node.parent();
        if (Objects.nonNull(parent)) {
          Point2D p2 = geometricLayer.toPoint2D(parent.state());
          graphics.setColor(treeColor.edgeColor.getColor((int) interp));
          Shape shape = new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY());
          graphics.draw(shape);
        }
      }
  }

  public void setCollection(Collection<? extends StateCostNode> collection) {
    this.collection = collection;
    polygon = Objects.nonNull(collection) //
        ? ConvexHull.of(collection.stream() //
            .map(StateCostNode::state) //
            .map(Extract2D::of), Chop._10) //
        : null;
  }
}