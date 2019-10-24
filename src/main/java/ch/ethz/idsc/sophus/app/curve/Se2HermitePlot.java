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

/* package */ class Se2HermitePlot implements RenderInterface {
  private final Tensor points;
  private final Scalar scale;

  /** @param points with dimensions N x 2 x 3
   * @param scale */
  public Se2HermitePlot(Tensor points, Scalar scale) {
    this.points = points;
    this.scale = scale;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    for (Tensor point : points) {
      geometricLayer.pushMatrix(Se2Matrix.of(point.get(0)));
      Tensor pv = point.get(1);
      Color color = Hsluv.of(pv.Get(2).number().doubleValue() * 0.3, 1, 0.5, 0.5);
      graphics.setColor(color);
      Tensor vec = Cross.of(pv.extract(0, 2).multiply(scale));
      graphics.draw(geometricLayer.toLine2D(vec));
      geometricLayer.popMatrix();
    }
  }
}
