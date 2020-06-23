// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;

/* package */ class CoordinatesDemo extends AbstractHoverDemo {
  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics, LeversRender leversRender) {
    leversRender.renderLevers();
    // ---
    leversRender.renderWeights();
    leversRender.renderSequence();
    leversRender.renderOrigin();
  }

  public static void main(String[] args) {
    new CoordinatesDemo().setVisible(1200, 900);
  }
}
