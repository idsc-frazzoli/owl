// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.awt.Graphics2D;
import java.util.Collection;

import ch.ethz.idsc.owl.gui.ren.TransitionRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.dubins.DubinsPath;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;

public class DubinsTransitionRender extends TransitionRender {
  private final Se2TransitionSpace se2TransitionSpace;

  public DubinsTransitionRender(Se2TransitionSpace se2TransitionSpace, Collection<? extends RrtsNode> collection) {
    super(collection);
    this.se2TransitionSpace = se2TransitionSpace;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics, RrtsNode rrtsNode) {
    for (RrtsNode child : rrtsNode.children()) {
      Se2Transition se2Transition = se2TransitionSpace.connect(rrtsNode.state(), child.state());
      DubinsPath dubinsPath = se2Transition.dubinsPath();
      // TODO JPH render edges
      dubinsPath.sampler(null);
    }
  }
}
