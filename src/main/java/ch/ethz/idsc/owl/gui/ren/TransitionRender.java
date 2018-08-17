// code by jph
package ch.ethz.idsc.owl.gui.ren;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.Objects;

import ch.ethz.idsc.owl.data.tree.StateCostNode;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;

/** renders the edges between nodes
 * 
 * the edges are drawn as straight lines with the color of the cost to root
 * 
 * only real-valued costs are supported
 * in particular costs of type {@link VectorScalar} are not supported
 * @see EdgeRender */
public abstract class TransitionRender implements RenderInterface {
  private Collection<? extends RrtsNode> collection;

  public TransitionRender(Collection<? extends RrtsNode> collection) {
    setCollection(collection);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Collection<? extends StateCostNode> _collection = collection;
    if (Objects.isNull(_collection) || _collection.isEmpty())
      return;
    // TODO JPH render edges
  }

  public abstract void render(GeometricLayer geometricLayer, Graphics2D graphics, RrtsNode rrtsNode);

  public void setCollection(Collection<? extends RrtsNode> collection) {
    this.collection = collection;
  }
}
