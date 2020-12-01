// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;

/* package */ class UbongoDesigner extends AbstractDemo {
  private final GridRender gridRender;

  public UbongoDesigner() {
    timerFrame.geometricComponent.setModel2Pixel(Tensors.fromString("{{30, 0, 100}, {0, -30, 500}, {0, 0, 1}}"));
    gridRender = new GridRender(Subdivide.of(0, 10, 10));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    gridRender.render(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    UbongoDesigner ubongoDesigner = new UbongoDesigner();
    // ubongoDesigner.timerFrame.configCoordinateOffset(100, 700);
    ubongoDesigner.setVisible(800, 600);
  }
}
