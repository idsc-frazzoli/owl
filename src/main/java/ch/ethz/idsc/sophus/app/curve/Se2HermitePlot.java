// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.Hsluv;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.Cross;

public class Se2HermitePlot implements RenderInterface {
  private final Tensor se2h;
  private final Scalar scale;

  public Se2HermitePlot(Tensor se2h, Scalar scale) {
    this.se2h = se2h;
    this.scale = scale;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(new Color(128, 128, 128, 128));
    for (Tensor pointh : se2h) {
      geometricLayer.pushMatrix(Se2Matrix.of(pointh.get(0)));
      Tensor pv = pointh.get(1);
      Tensor vec = Cross.of(pv.extract(0, 2).multiply(scale));
      Color color = Hsluv.of(pv.Get(2).number().doubleValue() * 0.3, 1, 0.5, 1);
      graphics.setColor(color);
      graphics.draw(geometricLayer.toLine2D(vec));
      geometricLayer.popMatrix();
    }
  }
}
