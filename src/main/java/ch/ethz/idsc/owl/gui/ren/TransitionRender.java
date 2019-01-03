// code by jph
package ch.ethz.idsc.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

/** renders the edges between nodes
 * 
 * the edges are drawn as straight lines with the color of the cost to root
 * 
 * only real-valued costs are supported
 * in particular costs of type {@link VectorScalar} are not supported
 * @see EdgeRender */
public class TransitionRender {
  private final TransitionSpace transitionSpace;
  private RenderInterface renderInterface = EmptyRender.INSTANCE;

  public TransitionRender(TransitionSpace transitionSpace) {
    this.transitionSpace = transitionSpace;
  }

  public RenderInterface getRender() {
    return renderInterface;
  }

  public RenderInterface setCollection(Collection<? extends RrtsNode> collection) {
    return renderInterface = Objects.isNull(collection) || collection.isEmpty() //
        ? EmptyRender.INSTANCE
        : new Render(collection);
  }

  private class Render implements RenderInterface {
    private final Collection<? extends RrtsNode> collection;

    public Render(Collection<? extends RrtsNode> collection) {
      this.collection = collection;
    }

    @Override // from RenderInterface
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      graphics.setColor(Color.BLUE);
      for (RrtsNode parent : collection)
        for (RrtsNode child : parent.children()) {
          Transition transition = transitionSpace.connect(parent.state(), child.state());
          List<StateTime> sampled = transition.sampled(RealScalar.ZERO, RealScalar.ZERO, RealScalar.of(.2));
          Path2D path2d = geometricLayer.toPath2D(Tensor.of(sampled.stream().map(StateTime::state)));
          graphics.draw(path2d);
        }
    }
  }
}
