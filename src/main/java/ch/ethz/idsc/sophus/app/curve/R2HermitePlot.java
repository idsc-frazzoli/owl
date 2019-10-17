// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.Cross;

/* package */ class R2HermitePlot implements RenderInterface {
  private final Tensor points;
  private final Scalar scale;

  public R2HermitePlot(Tensor points, Scalar scale) {
    this.points = points;
    this.scale = scale;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(new Color(128, 128, 128, 128));
    for (Tensor point : points) {
      Tensor pg = point.get(0);
      Tensor vec = Cross.of(point.get(1).multiply(scale));
      graphics.draw(geometricLayer.toLine2D(pg, pg.add(vec)));
    }
  }
}
