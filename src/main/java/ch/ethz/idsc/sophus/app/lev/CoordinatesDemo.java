// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class CoordinatesDemo extends AbstractHoverDemo {
  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics, LeversRender leversRender) {
    Tensor weights = operator(leversRender.getSequence()).apply(leversRender.getOrigin());
    leversRender.renderLevers(logWeighting().equals(LogWeightings.DISTANCES) //
        ? weights.negate()
        : weights);
    // ---
    leversRender.renderWeights(weights);
    leversRender.renderSequence();
    leversRender.renderOrigin();
  }

  public static void main(String[] args) {
    new CoordinatesDemo().setVisible(1200, 900);
  }
}
