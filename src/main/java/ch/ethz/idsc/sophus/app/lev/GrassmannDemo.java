// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;

/* package */ class GrassmannDemo extends AbstractHoverDemo {
  public GrassmannDemo() {
    spinnerCount.setValue(5);
    shuffle(spinnerCount.getValue());
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics, LeverRender leverRender) {
    leverRender.renderLevers();
    // ---
    leverRender.renderGrassmannians();
    leverRender.renderGrassmannianOrigin();
    leverRender.renderSequence();
    leverRender.renderOrigin();
  }

  public static void main(String[] args) {
    new GrassmannDemo().setVisible(1200, 900);
  }
}
